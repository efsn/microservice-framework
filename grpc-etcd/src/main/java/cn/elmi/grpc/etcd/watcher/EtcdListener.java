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

package cn.elmi.grpc.etcd.watcher;

import lombok.extern.slf4j.Slf4j;
import mousio.client.promises.ResponsePromise;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeysResponse;

import java.util.concurrent.CancellationException;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
public abstract class EtcdListener implements ResponsePromise.IsSimplePromiseResponseHandler<EtcdKeysResponse> {

    private EtcdClient etcdClient;
    private String watchPath;
    private String listenPath;

    public EtcdListener() {
    }

    public EtcdListener(EtcdClient etcdClient, String listenPath) {
        this.etcdClient = etcdClient;
        this.listenPath = listenPath;
    }

    public String getWatchPatch() {
        return watchPath;
    }

    public void setWatchPath(String watchPath) {
        this.watchPath = watchPath;
    }

    public String getListenPath() {
        return listenPath;
    }

    @Override
    public void onResponse(ResponsePromise<EtcdKeysResponse> responsePromise) {
        boolean isClosed = false;
        try {
            EtcdKeysResponse response = responsePromise.get();
            if (null != response) {
                switch (response.action) {
                    case expire:
                    case delete:
                    case set:
                    case update:
                    case create:
                    case compareAndDelete:
                    case compareAndSwap:
                        changeEvent();
                        log.warn("node is {}, action is {}.", response.node.key, response.action.name());
                        break;
                    default:
                        log.warn("node is {}, unkown action is {}.", response.node.key, response.action.name());
                }
            }
        } catch (Exception e) {
            if (e.getCause() instanceof CancellationException) {
                log.warn("etcd client was closed.");
                isClosed = true;
                return;
            } else {
                log.error(e.getMessage(), e);
            }
        } finally {
            for (; !isClosed; ) {
                try {
                    EtcdKeysResponse keysResponse = etcdClient.get(listenPath).send().get();
                    if (null != keysResponse) {
                        long modifyIndex = keysResponse.etcdIndex;
                        etcdClient.get(listenPath).recursive().waitForChange(++modifyIndex).send().addListener(this);
                    } else {
                        log.warn("keys reponse is null.");
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    protected abstract void changeEvent();

}
