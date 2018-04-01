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

import cn.elmi.grpc.client.annotation.GrpcClient;
import cn.elmi.grpc.client.exception.GrpcClientException;
import cn.elmi.grpc.client.interceptor.HeaderClientInterceptor;
import cn.elmi.grpc.client.props.GrpcClientProp;
import cn.elmi.grpc.client.route.DirectAlgorithm;
import cn.elmi.grpc.client.route.Node;
import cn.elmi.grpc.client.route.RibbonAlgorithm;
import cn.elmi.grpc.client.route.RouteAlgorithm;
import cn.elmi.grpc.client.utils.GrpcClientUtil;
import cn.elmi.grpc.etcd.EtcdAutoConfiguration;
import com.google.common.cache.Cache;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PreDestroy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arthur
 * @since 1.0
 */
@Configuration
@ConditionalOnClass(GrpcClient.class)
@Import({GrpcClientConfiguration.class, EtcdAutoConfiguration.class})
@Slf4j
public class GrpcClientBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Class<?>> beansToProcess = new HashMap<>();
    private Map<String, GrpcClientBean> grpcClientMap = new ConcurrentHashMap<>();

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private GenericKeyedObjectPool<Node, ManagedChannel> pool;

    @Autowired
    private Cache<String, String> cache;

    @Autowired
    private GrpcClientProp grpcClientProp;

    @Autowired
    private RibbonAlgorithm ribbonAlgorithm;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Class<?> clazz = bean.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(GrpcClient.class)) {
                    beansToProcess.put(beanName, clazz);
                }
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GrpcClient.class) && method.getParameterCount() == 1) {
                    beansToProcess.put(beanName, clazz);
                }
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beansToProcess.containsKey(beanName)) {
            Object target = getTargetBean(bean);
            Class<?> clazz = beansToProcess.get(beanName);
            for (Field field : clazz.getDeclaredFields()) {
                GrpcClient anno = AnnotationUtils.getAnnotation(field, GrpcClient.class);
                if (null != anno) {
                    if (beanFactory.containsBean(field.getName())) {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field, target, beanFactory.getBean(field.getName()));
                    } else {
                        String realClassName = getRealClassName(field.getType());

                        GrpcClientBean grpcClientBean = createGrpcClientBean(field.getType(), realClassName, anno);
                        grpcClientBean.setAuth(anno.auth());
                        grpcClientMap.put(beanName + "-" + realClassName, grpcClientBean);
                        ProxyFactory proxyFactory = getProxyFactoryForGrpcClient(target, field.getType(),
                                field.getName());

                        addPoolAdvice(proxyFactory, beanName + "-" + realClassName);
                        proxyFactory.setFrozen(true);
                        proxyFactory.setProxyTargetClass(true);

                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field, target, proxyFactory.getProxy());
                    }
                }
            }

            for (Method method : clazz.getDeclaredMethods()) {
                GrpcClient anno = AnnotationUtils.getAnnotation(method, GrpcClient.class);

                if (null != anno && 1 == method.getParameterCount()) {
                    Parameter para = method.getParameters()[0];
                    String realClassName = getRealClassName(para.getType());

                    GrpcClientBean thriftClientBean = createGrpcClientBean(para.getType(), realClassName, anno);
                    grpcClientMap.put(beanName + "-" + realClassName, thriftClientBean);
                    ProxyFactory proxyFactory = getProxyFactoryForGrpcClient(target, para.getType(), method.getName());
                    addPoolAdvice(proxyFactory, beanName + "-" + realClassName);

                    proxyFactory.setFrozen(true);
                    proxyFactory.setProxyTargetClass(true);

                    ReflectionUtils.makeAccessible(method);
                    ReflectionUtils.invokeMethod(method, target, proxyFactory.getProxy());
                }

            }
        }

        return bean;
    }

    private String getRealClassName(Class<?> clazz) {
        return clazz.getCanonicalName();
    }

    private GrpcClientBean createGrpcClientBean(Class<?> type, String className, GrpcClient anno) {
        GrpcClientBean grpcClientBean = new GrpcClientBean();

        RouteAlgorithm router = anno.address().isEmpty() ? ribbonAlgorithm : new DirectAlgorithm(anno.address());

        grpcClientBean.setRouter(router);
        grpcClientBean.setTimeout(anno.timeout());
        grpcClientBean.setRetryTimes(anno.retryTimes());
        grpcClientBean.setClientName(className);
        return grpcClientBean;
    }

    private Object getTargetBean(Object bean) {
        Object target = bean;
        try {
            while (AopUtils.isAopProxy(target)) {
                target = ((Advised) target).getTargetSource().getTarget();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GrpcClientException("get target bean error.", e);
        }
        return target;
    }

    private ProxyFactory getProxyFactoryForGrpcClient(Object bean, Class<?> type, String name) {
        ProxyFactory proxyFactory;
        try {
            String canonicalName = type.getCanonicalName();
            Object client = GrpcClientUtil.createGrpcClient(canonicalName,
                    // TODO localhost???
                    ManagedChannelBuilder.forAddress("localhost", 9090).build());
            proxyFactory = new ProxyFactory(client);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InvalidPropertyException(bean.getClass(), name, e.getMessage());
        }
        return proxyFactory;
    }

    private void addPoolAdvice(ProxyFactory proxyFactory, final String beanName) {
        proxyFactory.addAdvice((MethodInterceptor) x -> {
            Object[] args = x.getArguments();
            GrpcClientBean grpcClientBean = grpcClientMap.get(beanName);

            for (int i = 0; i < grpcClientBean.getRetryTimes(); i++) {
                Node node = null;
                ManagedChannel channel = null;

                try {
                    node = grpcClientBean.getRouter().getTransportNode();
                    if (null == node) {
                        throw new GrpcClientException("no available channel node, bean name is " + beanName);
                    }

                    node.setTimeout(grpcClientBean.getTimeout());
                    channel = pool.borrowObject(node);

                    Object client = GrpcClientUtil.createGrpcClient(grpcClientBean.getClientName(), channel);

                    HeaderClientInterceptor interceptor;
                    /* 添加 token */
                    if (grpcClientBean.isAuth()) {
                        interceptor = new HeaderClientInterceptor(grpcClientProp.getId(), cache.getIfPresent("token"));
                    } else {
                        interceptor = new HeaderClientInterceptor(grpcClientProp.getId(), null);
                    }
                    client = ((AbstractStub<?>) client).withInterceptors(interceptor);
                    return ReflectionUtils.invokeMethod(x.getMethod(), client, args);
                } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | SecurityException
                        | NoSuchMethodException e) {
                    throw new GrpcClientException(ExceptionUtils.getMessage(e) + ", bean name is " + beanName, e);
                } catch (UndeclaredThrowableException e) {
                    if (e.getUndeclaredThrowable() instanceof SocketTimeoutException) {
                        SocketTimeoutException innerException = (SocketTimeoutException) e.getUndeclaredThrowable();
                        Throwable realException = innerException.getCause();
                        if (realException instanceof SocketTimeoutException) {
                            if (null != channel) {
                                channel.shutdown();
                            }
                            throw new GrpcClientException(ExceptionUtils.getMessage(e) + ", bean name is " + beanName,
                                    e);
                        } else if (realException instanceof SocketTimeoutException) {
                            pool.clear(node);
                            if (null != channel) {
                                channel.shutdown();
                            }
                            handleException(i, grpcClientBean.getRetryTimes(), beanName, e);
                            continue;
                        } else {
                            handleException(i, grpcClientBean.getRetryTimes(), beanName, e);
                            continue;
                        }
                    } else {
                        throw new GrpcClientException(ExceptionUtils.getMessage(e) + ", bean name is " + beanName, e);
                    }
                } catch (Exception e) {
                    log.error("Invoke grpc server fail.", e);
                    handleException(i, grpcClientBean.getRetryTimes(), beanName, e);
                    continue;
                } finally {
                    if (null != pool && null != channel) {
                        pool.returnObject(node, channel);
                    }
                }
            }
            throw new GrpcClientException("rpc client call failed, bean name is " + beanName);
        });
    }

    private void handleException(int index, int retryTimes, String beanName, Throwable t) {
        if (index == retryTimes) {
            throw new GrpcClientException(ExceptionUtils.getMessage(t) + ", bean name is " + beanName, t);
        }
    }

    @PreDestroy
    private void destroy() {
        grpcClientMap.forEach((k, v) -> {
            if (v.getRouter() instanceof RibbonAlgorithm) {
                ((RibbonAlgorithm) v.getRouter()).getLoadBalancer().shutdown();
            }
        });
    }

}
