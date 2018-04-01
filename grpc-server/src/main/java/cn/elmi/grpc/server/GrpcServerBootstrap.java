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

import cn.elmi.grpc.server.configuration.GrpcRegisterConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.SmartLifecycle;

/**
 * @author Arthur
 * @since 1.0
 */
@ConditionalOnBean(GrpcServer.class)
@AutoConfigureAfter(GrpcRegisterConfiguration.class)
@Slf4j
public class GrpcServerBootstrap implements SmartLifecycle {

    @Autowired
    private GrpcServer server;

    private int phase = Integer.MAX_VALUE;

    @Override
    public void start() {
        if (null != server) {
            new Thread(server, "grpc server").start();
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        if (null != server) {
            return server.isStarted();
        }
        return false;
    }

    @Override
    public int getPhase() {
        return phase;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        if (isRunning()) {
            log.info("grpc server shutdown");
            server.shutdown();
            if (null != callback) {
                callback.run();
            }
        }
    }

}
