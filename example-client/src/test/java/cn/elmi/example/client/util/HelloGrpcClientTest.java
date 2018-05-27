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

import cn.elmi.grpc.example.hello.HelloGrpc;
import cn.elmi.grpc.example.hello.HelloRequest;
import cn.elmi.grpc.example.hello.HelloResponse;
import cn.elmi.grpc.test.HeaderClientInterceptor;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
public class HelloGrpcClientTest extends GrpcClientTest {


    private HelloGrpc.HelloBlockingStub blockingStub;

    @BeforeTest
    public void setUp() {
        blockingStub = HelloGrpc.newBlockingStub(getChannel()).withInterceptors(new HeaderClientInterceptor(getClientId(), getAccessToken()));
    }

    @Test
    public void say() {
        String message = "hello";
        HelloRequest request = HelloRequest.newBuilder().setQuestion(message).build();

        HelloResponse response;
        try {
            response = blockingStub.say(request);
            log.info("Result: " + getJsonJacksonFormat().printToString((response)));
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus(), e);
        }
    }

}
