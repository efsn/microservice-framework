package cn.elmi.grpc.server.utils;

import cn.elmi.grpc.server.consts.CacheRegions;
import cn.elmi.grpc.server.interceptor.model.AccessToken;
import com.hnair.components.cache.CacheChannel;
import com.hnair.components.cache.model.CacheElement;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        if (ApplicationUtil.applicationContext == null) {
            ApplicationUtil.applicationContext = arg0;
        }
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过name获取 Bean.
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    // 根据token获取Subject
    public static AccessToken get(String token) {
        CacheElement<String, AccessToken> elm = getBean(CacheChannel.class).get(CacheRegions.TOKEN_REGION, token);
        return elm.getValue();
    }

    // 强制刷新缓存中的数据
    public static void put(AccessToken accessToken) {
        if (null != accessToken) {
            getBean(CacheChannel.class).put(CacheRegions.TOKEN_REGION, accessToken.getToken(), accessToken);
        }
    }

}