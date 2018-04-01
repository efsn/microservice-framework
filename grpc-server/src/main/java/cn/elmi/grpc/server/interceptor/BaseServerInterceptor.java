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

import cn.elmi.grpc.server.consts.RequestStatus;
import cn.elmi.grpc.server.utils.GrpcUtil;
import cn.elmi.grpc.server.utils.InetAddressUtil;
import cn.elmi.microservice.influxdb.GrpcRequest;
import io.grpc.*;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall.Listener;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
public abstract class BaseServerInterceptor implements ServerInterceptor {

    public static final String IP_PATTERN = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";

    /*
     * (non-Javadoc)
     *
     * @see io.grpc.ServerInterceptor#interceptCall(io.grpc.ServerCall,
     * io.grpc.Metadata, io.grpc.ServerCallHandler)
     */
    @Override
    public abstract <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                               ServerCallHandler<ReqT, RespT> next);

    public void addHeader(Metadata headers, String key, String value) {
        headers.put(Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);
    }

    public boolean containsHeader(Metadata headers, String key) {
        return headers.containsKey(Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
    }

    public String getHeader(Metadata headers, String key) {
        return headers.get(Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
    }

    public <ReqT, RespT> Listener<ReqT> close(ServerCall<ReqT, RespT> call, Metadata headers, Status status,
                                              String description) {
        addHeader(headers, GrpcUtil.IS_CLOSE, "yes");
        call.close(status, new Metadata());
        return new Listener<ReqT>() {
        };
    }

    public <ReqT, RespT> String getRemoteIP(ServerCall<ReqT, RespT> call, Metadata headers) {
        String clientIP = headers.get(Key.of(GrpcUtil.X_FORWARDED_FOR, Metadata.ASCII_STRING_MARSHALLER));
        if (null == clientIP || !clientIP.matches(IP_PATTERN)) {
            clientIP = GrpcUtil.getClientIP(call);
        }
        return clientIP;
    }

    public <ReqT, RespT> GrpcRequest parseReqeust(ServerCall<ReqT, RespT> call, Metadata headers) {
        String method = call.getMethodDescriptor().getFullMethodName();
        String clientID = headers.get(Key.of(GrpcUtil.CLIENT_ID, Metadata.ASCII_STRING_MARSHALLER));
        if (null == clientID) {
            clientID = GrpcUtil.NON;
        }
        String clientIP = getRemoteIP(call, headers);

        log.debug("{} invoke {}, client ip is {}", clientID, method, clientIP);
        Matcher matcher = Pattern.compile(GrpcUtil.SERVICE_REG).matcher(method);
        if (matcher.find()) {
            log.debug("pacage:{}, service:{}, method:{}", matcher.group(1), matcher.group(2), matcher.group(3));
        }

        GrpcRequest request = new GrpcRequest();
        request.setApp("aireye");
        request.setClientID(clientID);
        request.setClientIP(clientIP);
        request.setServerIP(InetAddressUtil.getLocalHostLanAddress().getHostAddress());
        request.setNumber(1);
        request.setService(method);
        request.setStatus(RequestStatus.OK);
        return request;
    }
}
