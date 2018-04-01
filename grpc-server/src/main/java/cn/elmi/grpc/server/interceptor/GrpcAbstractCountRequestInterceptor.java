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
public abstract class GrpcAbstractCountRequestInterceptor implements ServerInterceptor {

    String appName = "rest2";

    /*
     * (non-Javadoc)
     *
     * @see io.grpc.ServerInterceptor#interceptCall(io.grpc.ServerCall,
     * io.grpc.Metadata, io.grpc.ServerCallHandler)
     */
    @Override
    public abstract <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                               ServerCallHandler<ReqT, RespT> next);

    /**
     * <pre>
     * 生成访问记录
     * 格式： token:clientID:clientIP:APP:service:Status:time
     * </pre>
     *
     * @param call
     * @param headers
     * @param status
     * @return
     */
    @Deprecated
    public <ReqT, RespT> String countKey(ServerCall<ReqT, RespT> call, Metadata headers, Status status) {
        StringBuffer sb = new StringBuffer("token:");
        String clientId = headers.get(Key.of(GrpcUtil.CLIENT_ID, Metadata.ASCII_STRING_MARSHALLER));
        String method = call.getMethodDescriptor().getFullMethodName();
        String clientIP = GrpcUtil.getClientIP(call);
        sb.append(clientId).append(":");
        sb.append(clientIP).append(":");
        sb.append(appName).append(":");
        sb.append(method).append(":");
        sb.append(status.getCode().name()).append(":");
        sb.append(Long.toString(System.currentTimeMillis()).substring(0, 10));
        return sb.toString();
    }

    public <ReqT, RespT> GrpcRequest parseReqeust(ServerCall<ReqT, RespT> call, Metadata headers, Status status) {
        String clientId = headers.get(Key.of(GrpcUtil.CLIENT_ID, Metadata.ASCII_STRING_MARSHALLER));
        String method = call.getMethodDescriptor().getFullMethodName();
        String clientIP = GrpcUtil.getClientIP(call);

        log.debug("{} invoke {}, client ip is {}", clientId, method, clientIP);
        Matcher matcher = Pattern.compile(GrpcUtil.SERVICE_REG).matcher(method);
        if (matcher.find()) {
            log.debug("pacage:{}, service:{}, method:{}", matcher.group(1), matcher.group(2), matcher.group(3));
        }

        GrpcRequest request = new GrpcRequest();
        request.setApp(appName);
        request.setClientID(clientId);
        request.setClientIP(clientIP);
        request.setServerIP(InetAddressUtil.getLocalHostLanAddress().getHostAddress());
        request.setNumber(1);
        request.setService(method);
        request.setStatus(RequestStatus.OK);
        return request;
    }

}
