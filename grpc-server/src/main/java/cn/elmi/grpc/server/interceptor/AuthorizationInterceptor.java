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

package cn.elmi.grpc.server.interceptor;

import cn.elmi.grpc.server.consts.CacheRegions;
import cn.elmi.grpc.server.consts.RequestStatus;
import cn.elmi.grpc.server.utils.GrpcUtil;
import cn.elmi.microservice.repository.Client;
import cn.elmi.microservice.repository.ClientRepository;
import com.hnair.components.cache.CacheChannel;
import com.hnair.components.cache.model.CacheElement;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 授权拦截器, 检查 IP 黑白名单及接口访问权限
 * </pre>
 *
 * @author silva
 */
@Component
@Slf4j
public class AuthorizationInterceptor extends BaseServerInterceptor {

    @Autowired
    private CacheChannel channel;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                      ServerCallHandler<ReqT, RespT> next) {
        log.debug("Authorization begin");
        boolean valid = false;
        String clientId = headers.get(Key.of(GrpcUtil.CLIENT_ID, Metadata.ASCII_STRING_MARSHALLER));

        /* 客户端配置信息 */
        CacheElement<String, Client> elm = channel.get(CacheRegions.CLIENT_REGION, clientId, () -> {
            return clientRepository.findByClientId(clientId);
        });

        Client client = elm.getValue();

        if (null != client) {
            String clientIP = GrpcUtil.getClientIP(call);
            // IP白名单必须有，IP黑名单有的不管白名单有没有都拒绝，权限为空表示可以访问所有接口
            boolean white = false, black = false, perm = client.getPermission().isEmpty();
            // IP 白名单
            for (String whiteIP : client.getWhiteIp()) {
                if (clientIP.matches(whiteIP)) {
                    white = true;
                    break;
                }
            }

            // IP 黑名单
            for (String blackIP : client.getBlackIp()) {
                if (clientIP.matches(blackIP)) {
                    black = true;
                    log.warn("Black iP: {}", clientIP);
                    break;
                }
            }

            // 接口
            String method = call.getMethodDescriptor().getFullMethodName();
            for (String permission : client.getPermission()) {
                if (method.equalsIgnoreCase(permission)) {
                    perm = true;
                    break;
                }
            }

            valid = white && !black && perm;
            log.debug("Authorization[{}/{}/{}] result: {}", clientId, clientIP, method, valid);
        }

        Listener<ReqT> listener;
        if (!valid) {
            listener = close(call, headers, Status.PERMISSION_DENIED, RequestStatus.DY.getDescription());
        } else {
            listener = next.startCall(call, headers);
        }

        return listener;
    }

}
