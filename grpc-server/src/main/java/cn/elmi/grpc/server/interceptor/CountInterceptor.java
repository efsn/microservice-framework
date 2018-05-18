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
import cn.elmi.grpc.server.monitor.MonitorContext;
import cn.elmi.grpc.server.utils.GrpcUtil;
import cn.elmi.microservice.influxdb.GrpcRequest;
import io.grpc.*;
import io.grpc.ServerCall.Listener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Arthur
 * @since 1.0
 */
@Component
@Slf4j
public class CountInterceptor extends BaseServerInterceptor {

    /*
    @Autowired
    private InfluxdbRepository influxdb;
    */

    @Autowired
    private MonitorContext monitorContext;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.hna.grpc.server.interceptor.AireyeBaseServerInterceptor#interceptCall(io.
     * grpc.ServerCall, io.grpc.Metadata, io.grpc.ServerCallHandler)
     */
    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                      ServerCallHandler<ReqT, RespT> next) {
        log.debug("Count begin.");
        GrpcRequest request = parseReqeust(call, headers);
        Listener<ReqT> listener;
        boolean closed = false;

        // 检查渠道是否已禁用
        boolean exceeded = monitorContext.isPosForbid(request.getClientID());

        if (GrpcUtil.NON.equals(request.getClientID())) {
            request.setStatus(RequestStatus.IL);
            listener = close(call, headers, Status.PERMISSION_DENIED, RequestStatus.IL.getDescription());
            closed = true;
        } else if (exceeded) {
            log.warn("Client({}) is forbidden.", request.getClientID());
            request.setStatus(RequestStatus.EC);
            listener = close(call, headers, Status.OUT_OF_RANGE, RequestStatus.EC.getDescription());
            closed = true;
        } else {
            Listener<ReqT> delegate = next.startCall(call, headers);
            log.debug("After trace.");
            final String traceID = MDC.get(GrpcUtil.TRACE_ID);

            /* 被后续拦截器拒绝的请求，header 里应包含 colse */
            closed = (null != getHeader(headers, GrpcUtil.IS_CLOSE));

            if (!closed) {
                /* 封装一下 listener, 记录业务请求耗时，并统一处理业务异常 */
                Listener<ReqT> weave = new ForwardingServerCallListener<ReqT>() {
                    /*
                     * (non-Javadoc)
                     *
                     * @see io.grpc.ForwardingServerCallListener#delegate()
                     */
                    @Override
                    protected Listener<ReqT> delegate() {
                        return delegate;
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see io.grpc.ForwardingServerCallListener#onHalfClose()
                     */
                    @Override
                    public void onHalfClose() {
                        try {
                            MDC.put(GrpcUtil.TRACE_ID, traceID);
                            long begin = System.currentTimeMillis();
                            delegate.onHalfClose();
                            // 记录业务方法处理耗时
                            request.setElapse(System.currentTimeMillis() - begin);
                            MDC.clear();
                        } catch (Throwable t) {
                            // 统一处理异常
                            log.error("Grpc service invoke failed.", t);
                            request.setStatus(RequestStatus.EX);
                            // 调用 call.close() 发送 Status 和 metadata
                            Metadata trailers = new Metadata();
                            Metadata.Key<String> key = Metadata.Key.of("error", Metadata.ASCII_STRING_MARSHALLER);
                            if (t instanceof NullPointerException) {
                                trailers.put(key, "null pointer.");
                            } else {
                                trailers.put(key, "server error");
                            }
                            // 这个方式和 onError()本质是一样的
                            call.close(Status.UNKNOWN, trailers);
                        } finally {
                            log.debug("Save grpc request record: {}", request);
//                            influxdb.saveRequestByOkHttp(request);
                        }
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see io.grpc.ForwardingServerCallListener#onCancel()
                     */
                    @Override
                    public void onCancel() {
                        delegate.onCancel();
                        log.debug("*** cancel ***");
                    }
                };

                listener = weave;
            } else {
                listener = delegate;
            }
        }

        if (closed) {
            if (RequestStatus.OK == request.getStatus()) {
                request.setStatus(RequestStatus.DY);
            }
            /* 直接保存请求记录 */
//            influxdb.saveRequestByOkHttp(request);
        }

        MDC.clear();
        return listener;
    }

}
