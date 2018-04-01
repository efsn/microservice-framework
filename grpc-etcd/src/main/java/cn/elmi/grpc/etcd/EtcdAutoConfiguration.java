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

package cn.elmi.grpc.etcd;

import cn.elmi.grpc.etcd.props.EtcdProp;
import lombok.extern.slf4j.Slf4j;
import mousio.client.retry.RetryNTimes;
import mousio.etcd4j.EtcdClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arthur
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(EtcdProp.class)
@Slf4j
public class EtcdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EtcdClient etcdClient(EtcdProp etcdClientProp) {
        List<URI> uris = etcdClientProp.getUris();
        if (CollectionUtils.isEmpty(uris)) {
            log.error("uri has not been set");
            return null;
        }

        EtcdClient client = new EtcdClient(uris.toArray(new URI[0]));
        client.setRetryHandler(new RetryNTimes(etcdClientProp.getBeforeRetryTime(), etcdClientProp.getRetryTimes()));
        String urls = etcdClientProp.getUris().stream().map(x -> x.toString()).collect(Collectors.joining(","));
        if (null != client.version()) {
            log.info("etcd version is {}, urls are [{}]", client.version().getCluster(), urls);
        } else {
            log.info("etcd urls are [{}] is invalid", urls);
        }
        return client;
    }

}
