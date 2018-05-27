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

package cn.elmi.grpc.client;

import cn.elmi.grpc.client.exception.GrpcClientException;
import cn.elmi.grpc.etcd.watcher.EtcdListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netflix.loadbalancer.ServerListUpdater;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdKeysResponse;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Arthur
 * @since 1.0
 */
public class EtcdNotificationUpdate implements ServerListUpdater {

    private final AtomicBoolean isActive = new AtomicBoolean(false);
    private final AtomicLong lastUpdated = new AtomicLong(System.currentTimeMillis());
    private final ExecutorService refreshExecutor;
    private final EtcdClient etcdClient;
    private final String listenPath;

    public EtcdNotificationUpdate(EtcdClient etcdClient, String listenPath) {
        this(etcdClient, listenPath, getDefaultRefreshExecutor());
    }

    public EtcdNotificationUpdate(EtcdClient etcdClient, String listenPath, ExecutorService refreshExecutor) {
        this.etcdClient = etcdClient;
        this.listenPath = listenPath;
        this.refreshExecutor = refreshExecutor;
    }

    @Override
    public void start(UpdateAction updateAction) {
        if (isActive.compareAndSet(false, true)) {
            try {
                EtcdResponsePromise<EtcdKeysResponse> responsePromise = etcdClient.get(listenPath).recursive()
                        .waitForChange().send();
                responsePromise.addListener(new EtcdListener(etcdClient, listenPath) {

                    @Override
                    protected void changeEvent() {
                        refreshExecutor.submit(() -> {
                            updateAction.doUpdate();
                            lastUpdated.set(System.currentTimeMillis());
                        });
                    }

                });
            } catch (Exception e) {
                throw new GrpcClientException(e);
            }
        }
    }

    @Override
    public void stop() {
        if (isActive.compareAndSet(true, false)) {

        }
    }

    @Override
    public String getLastUpdate() {
        return new Date(lastUpdated.get()).toString();
    }

    @Override
    public long getDurationSinceLastUpdateMs() {
        return System.currentTimeMillis() - lastUpdated.get();
    }

    @Override
    public int getNumberMissedCycles() {
        return 0;
    }

    @Override
    public int getCoreThreads() {
        if (isActive.get() && null != refreshExecutor && refreshExecutor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) refreshExecutor).getCorePoolSize();
        }
        return 0;
    }

    public static ExecutorService getDefaultRefreshExecutor() {
        return LazyHolder.DEFAULT_SERVER_LIST_UPDATE_EXECUTOR;
    }

    private static class LazyHolder {
        private static final ExecutorService DEFAULT_SERVER_LIST_UPDATE_EXECUTOR = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder().setNameFormat("EtcdNotificationUpdate-%d").setDaemon(true).build());

        private static final Thread SHUTDOWN_THREAD = new Thread(() -> {
            DEFAULT_SERVER_LIST_UPDATE_EXECUTOR.shutdown();
        });

        static {
            Runtime.getRuntime().addShutdownHook(SHUTDOWN_THREAD);
        }
    }

}
