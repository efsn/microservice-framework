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

import cn.elmi.grpc.server.GrpcServer;
import cn.elmi.grpc.server.annotation.GrpcService;
import cn.elmi.grpc.server.exception.GrpcServerException;
import cn.elmi.grpc.server.props.GrpcCertProp;
import cn.elmi.grpc.server.props.GrpcServerProp;
import cn.elmi.grpc.server.utils.ApplicationUtil;
import cn.elmi.grpc.server.utils.CertFileUtil;
import cn.elmi.grpc.server.utils.GrpcUtil;
import cn.elmi.microservice.influxdb.InfluxdbProp;
import cn.elmi.microservice.repository.APIRepository;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties({GrpcServerProp.class, GrpcCertProp.class, InfluxdbProp.class})
@Slf4j
public class GrpcAutoConfiguration implements ApplicationContextAware {

    @Autowired
    private GrpcServerProp grpcServerProp;

    @Autowired
    private GrpcCertProp grpcCertProp;

    private ApplicationContext applicationContext;

    @Autowired
    private InfluxdbProp influxdbProp;

    @Autowired
    private APIRepository apiRepository;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcServer createGrpcServer(Server server) {
        return new GrpcServer(server);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("grpc.server.port")
    public Server server() {
        String[] serviceNames = applicationContext.getBeanNamesForAnnotation(GrpcService.class);
        if (null == serviceNames || serviceNames.length < 1) {
            String errorMessages = "grpc service not found.";
            log.error(errorMessages);
            throw new GrpcServerException(errorMessages);
        }

        ServerBuilder<?> serverBuilder = bindService();
        if (grpcCertProp.isTls() && !StringUtils.isEmpty(grpcCertProp.getCert())
                && !StringUtils.isEmpty(grpcCertProp.getKey())) {
            File cert = new File(grpcCertProp.getCert());
            File key = null;
            if (cert.exists()) {
                key = new File(grpcCertProp.getKey());
            } else {
                cert = CertFileUtil.getFile(grpcCertProp.getCert());
                key = CertFileUtil.getFile(grpcCertProp.getKey());
            }
            serverBuilder.useTransportSecurity(cert, key);
        }
        return serverBuilder.build();
    }

    /*
    @Bean
    public InfluxDB influxdb() {
        InfluxDB influxDB = InfluxDBFactory.connect(influxdbProp.getUrl(), influxdbProp.getUsername(),
                influxdbProp.getPassword());
        influxDB.enableBatch(2000, 100, TimeUnit.MILLISECONDS);
        return influxDB;
    }
    */

    private ServerBuilder<?> bindService() {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(grpcServerProp.getPort());
        String[] serviceNames = applicationContext.getBeanNamesForAnnotation(GrpcService.class);
        for (String serviceName : serviceNames) {
            log.debug("Bind Service : {}", serviceName);
            Object obj = applicationContext.getBean(serviceName);
            GrpcService grpcService = obj.getClass().getAnnotation(GrpcService.class);
            String superName = obj.getClass().getSuperclass().getCanonicalName();

            try {
                if (superName.matches(GrpcUtil.INTERFACE_REG)) {
                    GrpcUtil.api(obj.getClass().getSuperclass());

                    Method bindService = obj.getClass().getMethod("bindService");
                    grpcServerProp.getServicesNames().add(superName);
                    Object o = bindService.invoke(obj);

                    // 注意拦截器的顺序: 后添加的先执行
                    List<ServerInterceptor> interceptors = new ArrayList<>();
                    for (String name : grpcServerProp.getInterceptors()) {
                        if (grpcService.auth()
                                || !"TokenInterceptor".equals(name.substring(name.lastIndexOf(".") + 1))) {
                            interceptors.add((ServerInterceptor) ApplicationUtil.getBean(Class.forName(name)));
                            log.debug("Auto register interceptor : {}", name);
                        }
                    }
                    serverBuilder.addService(ServerInterceptors.intercept((ServerServiceDefinition) o,
                            interceptors.toArray(new ServerInterceptor[interceptors.size()])));
                }
            } catch (Exception e) {
                log.error("bind grpc service fail: super is {}, class is {}", superName, serviceName, e);
                throw new GrpcServerException("bind grpc service fail.", e);
            }
        }

        log.info("Grpc api count is: {}", GrpcUtil.services.size());

        /*
        if (GrpcUtil.services.size() > 0) {
            log.info("Save apis.");
            API api = new API(grpcServerProp.getName(), System.currentTimeMillis(), GrpcUtil.services);
            apiRepository.save(api);
        }
        */

        return serverBuilder;
    }

}
