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

package cn.elmi.grpc.client.interceptor;

import io.grpc.*;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
public class HeaderClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> CLIENT_ID = Metadata.Key.of("client_id",
            Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> ACCESS_TOKEN = Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);

    private String accessToken, clientId;

    public HeaderClientInterceptor(String clientId, String accessToken) {
        this.clientId = clientId;
        this.accessToken = accessToken;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {

        return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(CLIENT_ID, clientId);
                if (StringUtils.isNotEmpty(accessToken)) {
                    headers.put(ACCESS_TOKEN, accessToken);
                }
                super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {

                    @Override
                    public void onHeaders(Metadata headers) {
                        log.info("header received from server: {}", headers);
                        super.onHeaders(headers);
                    }

                }, headers);
            }

        };

    }

}
