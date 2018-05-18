/**
 * Copyright (c) 2018 Arthur Chan (codeyn@163.com).
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.elmi.example.server.service;

import cn.elmi.components.cache.CacheChannel;
import cn.elmi.components.cache.model.CacheElement;
import cn.elmi.grpc.example.oauth.OAuthGrpc;
import cn.elmi.grpc.example.oauth.OAuthRequest;
import cn.elmi.grpc.example.oauth.OAuthResponse;
import cn.elmi.grpc.server.annotation.GrpcService;
import cn.elmi.grpc.server.consts.CacheRegions;
import cn.elmi.grpc.server.interceptor.model.AccessToken;
import cn.elmi.grpc.server.interceptor.model.SubjectType;
import cn.elmi.grpc.server.utils.ApplicationUtil;
import cn.elmi.grpc.server.utils.GrpcUtil;
import cn.elmi.microservice.repository.Client;
import cn.elmi.microservice.repository.ClientRepository;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
@Component
@GrpcService(auth = false)
public class OAuthService extends OAuthGrpc.OAuthImplBase {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CacheChannel channel;

    @Autowired
    private Pool<Jedis> pool;
    private OAuthRequest request;
    private StreamObserver<OAuthResponse> responseObserver;

    @Override
    public void getAccessToken(OAuthRequest request, StreamObserver<OAuthResponse> responseObserver) {
        this.request = request;
        this.responseObserver = responseObserver;
        log.debug("[SERVICE:{}:START] - get access token....", "getAccessToken");
        String clientId = request.getClientId();
        SubjectType st = SubjectType.get(request.getGrantType());
        if (StringUtils.isEmpty(clientId)) {
            responseObserver.onError(create(GrpcUtil.CLIENT_ID + " INVALID", Status.INVALID_ARGUMENT));
        } else if (SubjectType.UNKNOWN == st) {
            log.error("Unknown grant type {} for {}", request.getGrantType(), clientId);
            responseObserver.onError(create("INVALID Grant type", Status.INVALID_ARGUMENT));
        } else {
            if (!auth(request)) {
                log.error("Authen failed: clientId={}", clientId);
                responseObserver.onError(create("Token UNAUTHENTICATED", Status.UNAUTHENTICATED));
            } else {
                createToken(request, responseObserver);
            }
        }
        log.debug("[SERVICE:{}:END]", "getAccessToken");
    }

    private Exception create(String error, Status status) {
        Metadata header = new Metadata();
        header.put(Metadata.Key.of("error", Metadata.ASCII_STRING_MARSHALLER), error);
        return new StatusRuntimeException(status, header);
    }

    /**
     * 检查客户端信息
     */
    private boolean auth(OAuthRequest request) {
        SubjectType st = SubjectType.get(request.getGrantType());

        CacheElement<String, Client> elm = channel.get(CacheRegions.CLIENT_REGION, request.getClientId(), () -> {
            return clientRepository.findByClientId(request.getClientId());
        });
        Client client = elm.getValue();

        // 目前只支持两种模式，非客户端模式即密码模式，检验用户账号和密码
        return client != null && st != null && st.getType().equals(client.getGrantType())
                && null != request.getClientSecret() && request.getClientSecret().equals(client.getClientSecret())
                && "NORMAL".equals(client.getStatus());
    }

    private void createToken(OAuthRequest request, StreamObserver<OAuthResponse> responseObserver) {
        AccessToken accessToken = null;
        String val = null;
        SubjectType st = SubjectType.get(request.getGrantType());
        try {
            switch (st) {
                case CLIENT:
                    String key = request.getClientId();
                    try (Jedis jedis = pool.getResource()) {
                        String incrKey = key + "_incr";
                        if (1 == jedis.incr(incrKey)) {
                            jedis.expire(incrKey, 10);

                            // del old unexpired token
                            CacheElement<String, String> elm = channel.get(CacheRegions.TOKEN_CLIENT, key);
                            String oldToken = elm.getValue();
                            if (StringUtils.isNotBlank(oldToken)) {
                                String[] tokenInfo = oldToken.toString().split("@");
                                channel.evict(CacheRegions.TOKEN_REGION, tokenInfo[0]);
                            }

                            val = generateToken() + "@7200000";
                            channel.put(CacheRegions.TOKEN_CLIENT, key, val);
                        } else {
                            val = (String) channel.get(CacheRegions.TOKEN_CLIENT, key).getValue();
                        }
                    }
                    break;

                case PASSWORD:
                    key = request.getClientId() + request.getUserName();
                    try (Jedis jedis = pool.getResource()) {
                        String incrKey = key + "_incr";
                        if (1 == jedis.incr(incrKey)) {
                            jedis.expire(incrKey, 10);

                            // del old unexpired token
                            CacheElement<String, String> elm = channel.get(CacheRegions.TOKEN_CLIENT, key);
                            String oldToken = elm.getValue();
                            if (StringUtils.isNotBlank(oldToken)) {
                                String[] tokenInfo = oldToken.toString().split("@");
                                channel.evict(CacheRegions.TOKEN_REGION, tokenInfo[0]);
                            }

                            val = generateToken() + "@864001000";
                            channel.put(CacheRegions.TOKEN_PASSWD, key, val);
                        } else {
                            val = (String) channel.get(CacheRegions.TOKEN_PASSWD, key).getValue();
                        }
                    }
                    break;
                default:
            }

            if (null != val && val.indexOf("@") > 0) {
                String[] tokenInfo = val.toString().split("@");
                accessToken = new AccessToken(request.getClientId(), tokenInfo[0], st, Long.parseLong(tokenInfo[1]));
            }

            if (accessToken != null) {
                ApplicationUtil.put(accessToken);
                OAuthResponse response = OAuthResponse.newBuilder().setAccessToken(accessToken.getToken())
                        .setExpired(accessToken.getExpire()).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(new RuntimeException("Allocate token failed. please try later."));
            }

        } catch (Exception e) {
            log.error("Allocate token failed. clientID = {}", request.getClientId());
            responseObserver.onError(new RuntimeException("Allocate token failed. please try later."));
        }
    }

    private String generateToken() throws UnsupportedEncodingException {
        return DigestUtils.md5DigestAsHex(UUID.fromString(UUID.randomUUID().toString()).toString().getBytes("UTF-8"))
                .toUpperCase();
    }

}
