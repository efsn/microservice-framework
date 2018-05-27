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

package cn.elmi.example.client.util;

import cn.elmi.grpc.example.oauth.OAuthGrpc;
import cn.elmi.grpc.example.oauth.OAuthRequest;
import cn.elmi.grpc.example.oauth.OAuthResponse;
import cn.elmi.grpc.test.BaseGrpcClientTest;
import cn.elmi.grpc.test.HeaderClientInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
public class OAuthGrpcClientTest extends BaseGrpcClientTest {

    @Test
    public void getAccessToken() {
        OAuthGrpc.OAuthBlockingStub oauthBlockingStub = OAuthGrpc.newBlockingStub(getChannel()).withInterceptors(new HeaderClientInterceptor(getClientId(), null));
        OAuthRequest oAuthRequest = OAuthRequest.newBuilder().setClientId(getClientId()).setClientSecret(getClientSecret()).setGrantType(getClientGrantType()).build();
        OAuthResponse oauthResponse = oauthBlockingStub.getAccessToken(oAuthRequest);
        log.info("Retrieve access token[{}] success.", oauthResponse.getAccessToken());
    }

}