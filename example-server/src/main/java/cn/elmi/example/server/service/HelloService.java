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

import cn.elmi.grpc.example.hello.HelloGrpc;
import cn.elmi.grpc.example.hello.HelloRequest;
import cn.elmi.grpc.example.hello.HelloResponse;
import cn.elmi.grpc.server.annotation.GrpcService;
import io.grpc.stub.StreamObserver;

/**
 * @author Arthur
 * @since 1.0
 */
@GrpcService
public class HelloService extends HelloGrpc.HelloImplBase {

    @Override
    public void say(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String question = request.getQuestion();
        HelloResponse response = null;
        if ("Hello".equalsIgnoreCase(question)) {
            response = HelloResponse.newBuilder().setAnswer("fuck the world").build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
