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

package cn.elmi.grpc.server;

import cn.elmi.grpc.server.exception.GrpcServerException;
import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
public class GrpcServer implements Runnable {

    private Server server;
    private boolean started;

    public GrpcServer(Server server) {
        this.server = server;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void run() {
        log.info("grpc server starting.");
        try {
            started = null != server.start();
        } catch (IOException e) {
            log.error("grpc server start error.", e);
            throw new GrpcServerException("grpc server start error.", e);
        }
    }

    public void shutdown() {
        server.shutdown();
    }

}
