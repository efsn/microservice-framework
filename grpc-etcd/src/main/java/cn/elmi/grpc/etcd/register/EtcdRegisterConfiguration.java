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

package cn.elmi.grpc.etcd.register;

import cn.elmi.grpc.etcd.props.EtcdDiscoveryProp;
import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Arthur
 * @since 1.0
 */
@EnableScheduling
@AutoConfigureAfter(name = "com.hna.grpc.server.GrpcServerBootstrap")
@EnableConfigurationProperties(EtcdDiscoveryProp.class)
@Slf4j
public class EtcdRegisterConfiguration {

    @Autowired(required = false)
    private EtcdRegister etcdRegister;

    @Autowired
    private EtcdDiscoveryProp etcdDiscoveryProp;

    private boolean isRefresh;

    @Scheduled(initialDelayString = "${etcd.discovery.heartbeat:5000}", fixedRateString = "${spring.cloud.etcd.discovery.heartbeat:5000}")
    protected void sendHeartBeat() {
        register();
    }

    private void register() {
        if (null != etcdRegister && etcdRegister.isStarted()) {
            log.info("grpc server regist...");
            String key = etcdRegister.getPrefix() + "/" + etcdRegister.getKey();
            String value = etcdRegister.getValue();
            int ttl = etcdDiscoveryProp.getTtl();

            EtcdKeysResponse response;
            try {
                if (isRefresh) {
                    response = etcdRegister.getClient().refresh(key, ttl).send().get();
                    log.info("Regist refresh path[{}] ttl[{}]", key, ttl);
                } else {
                    response = etcdRegister.getClient().put(key, value).ttl(ttl).send().get();
                    log.info("regist put new path[{}] value[{}] ttl[{}]", key, value, ttl);
                }
            } catch (Exception e) {
                log.error("Regist fail.", e);
                try {
                    response = etcdRegister.getClient().delete(etcdRegister.getPrefix()).send().get();
                    etcdRegister.getClient().put(key, value).ttl(ttl).send().get();
                    log.info("delete and regist put new path[{}] value[{}] ttl[{}]", key, value, ttl);
                } catch (Exception e2) {
                    log.error("Regist again fail.", e2);
                }
            }

            isRefresh = true;
        }
    }

}
