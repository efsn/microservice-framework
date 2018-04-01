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

import cn.elmi.grpc.client.pool.ChannelPoolFactory;
import cn.elmi.grpc.client.props.GrpcClientProp;
import cn.elmi.grpc.client.route.Node;
import cn.elmi.grpc.client.route.RibbonAlgorithm;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.grpc.ManagedChannel;
import mousio.etcd4j.EtcdClient;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arthur
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(GrpcClientProp.class)
public class GrpcClientConfiguration {
    @Autowired
    private EtcdClient etcdClient;

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public GenericKeyedObjectPool<Node, ManagedChannel> grpcClientPool(GrpcClientProp grpcClientPorp) {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setJmxEnabled(false);
        config.setMaxTotalPerKey(grpcClientPorp.getPoolMaxTotalPerKey());
        config.setMaxIdlePerKey(grpcClientPorp.getPoolMaxIdlePerKey());
        config.setMinIdlePerKey(grpcClientPorp.getPoolMinIdlePerKey());
        config.setMaxWaitMillis(grpcClientPorp.getPoolMaxWait());
        config.setTestOnReturn(true);

        ChannelPoolFactory factory = new ChannelPoolFactory(grpcClientPorp.isTls(), grpcClientPorp.getCert(),
                grpcClientPorp.getDomain());
        return new GenericKeyedObjectPool<>(factory, config);
    }

    @Bean
    public Cache<String, String> cache(GrpcClientProp grpcClientProp) {
        return CacheBuilder.from(grpcClientProp.getCache()).build();
    }

    @Bean
    public RibbonAlgorithm route(GrpcClientProp grpcClientProp) {
        return new RibbonAlgorithm(grpcClientProp.getServer(), etcdClient);
    }
}
