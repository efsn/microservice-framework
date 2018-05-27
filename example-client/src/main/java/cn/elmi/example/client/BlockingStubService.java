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

package cn.elmi.example.client;

import cn.elmi.grpc.client.annotation.GrpcClient;
import cn.elmi.grpc.example.hello.HelloGrpc;
import cn.elmi.grpc.example.hello.HelloRequest;
import cn.elmi.grpc.example.hello.HelloResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Arthur
 * @since 1.0
 */
@Component
@Slf4j
public class BlockingStubService {

    @GrpcClient
    private HelloGrpc.HelloBlockingStub helloBlockingStub;

    public void say(String message) {
        HelloRequest request = HelloRequest.newBuilder().setQuestion(message).build();
        HelloResponse response = helloBlockingStub.say(request);
        log.debug("Blocking say: {}", response.getAnswer());
    }

}
