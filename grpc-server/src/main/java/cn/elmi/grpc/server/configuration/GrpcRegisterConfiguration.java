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

package cn.elmi.grpc.server.configuration;

import cn.elmi.grpc.etcd.EtcdAutoConfiguration;
import cn.elmi.grpc.etcd.register.EtcdRegister;
import cn.elmi.grpc.server.props.GrpcServerProp;
import cn.elmi.grpc.server.utils.InetAddressUtil;
import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Arthur
 * @since 1.0
 */
@Configuration
@AutoConfigureAfter(GrpcAutoConfiguration.class)
@EnableConfigurationProperties(GrpcServerProp.class)
@Import(EtcdAutoConfiguration.class)
@Slf4j
public class GrpcRegisterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EtcdRegister etcdRegister(EtcdClient etcdClient, GrpcServerProp grpcServerProp) {
        EtcdRegister etcdRegister = new EtcdRegister();
        etcdRegister.setClient(etcdClient);

        String ip = InetAddressUtil.getLocalHostLanAddress().getHostAddress();
        String address = ip + ":" + String.valueOf(grpcServerProp.getPort());

        log.info("register grpc service to etcd.");
        log.info("grpc server address: {}", address);

        etcdRegister.setKey(address);
        etcdRegister.setValue(address);
        etcdRegister.setPrefix("/grpc/server/" + grpcServerProp.getName());
        for (String interfaceName : grpcServerProp.getServicesNames()) {
            etcdRegister.getServices().add("/grpc/server/" + interfaceName);
            log.info("grpc service: {}", interfaceName);
        }

        etcdRegister.setStarted(grpcServerProp.isRegist());
        return etcdRegister;
    }

}
