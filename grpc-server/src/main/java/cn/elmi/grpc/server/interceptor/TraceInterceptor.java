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

import cn.elmi.grpc.server.utils.GrpcUtil;
import io.grpc.*;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ServerCall.Listener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * <pre>
 * 基于MDC实现日志追踪
 * </pre>
 *
 * @author Arthur
 * @since 1.0
 */
@Component
@Slf4j
public class TraceInterceptor extends BaseServerInterceptor {

    @Value("${event.logging.traceEnabled:false}")
    private boolean traceEnabled;

    // 'true' represents Full-Way tracing is enabled(tracking client all operations
    // by a trace id)
    @Value("${event.logging.fullwayEnabled:false}")
    private boolean fullwayEnabled;

    private static Metadata.Key<String> traceKey = Metadata.Key.of(GrpcUtil.TRACE_ID, Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                      ServerCallHandler<ReqT, RespT> next) {
        log.debug("Trace begin.");
        String fullMethodName = call.getMethodDescriptor().getFullMethodName();
        log.debug("Method: {}.", fullMethodName);

        /* 客户端 IP */
        String remoteip = getRemoteIP(call, headers);

        /* 用户 IP，仅对官网有效: 官网访问 grpc 接口时需要带上一个头信息：key=user-addr, value=用户IP */
        String userIP = containsHeader(headers, GrpcUtil.REMOTE_IP) ? getHeader(headers, GrpcUtil.REMOTE_IP) : remoteip;
        Context context = Context.current().withValue(Context.key(GrpcUtil.REMOTE_IP), userIP);

        if (traceEnabled) {
            String traceId = headers.get(Metadata.Key.of(GrpcUtil.TRACE_ID, Metadata.ASCII_STRING_MARSHALLER));
            if (StringUtils.isEmpty(traceId)) {
                traceId = UUID.randomUUID().toString().replaceAll("\\-", "");
            }
            MDC.put(GrpcUtil.TRACE_ID, traceId);
            MDC.put(GrpcUtil.REMOTE_IP, userIP);
            final String tid = traceId;
            SimpleForwardingServerCall<ReqT, RespT> simpleForwardingServerCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(
                    call) {

                @Override
                public void sendHeaders(Metadata responseHeaders) {
                    responseHeaders.put(traceKey, tid);
                    super.sendHeaders(responseHeaders);
                }

            };
            return Contexts.interceptCall(context, simpleForwardingServerCall, headers, next);
        }
        return Contexts.interceptCall(context, call, headers, next);
    }

}
