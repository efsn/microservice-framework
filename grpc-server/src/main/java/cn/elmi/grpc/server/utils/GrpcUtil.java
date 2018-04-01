package cn.elmi.grpc.server.utils;

import io.grpc.Attributes.Key;
import io.grpc.ServerCall;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author silva
 */
@Slf4j
public class GrpcUtil {

    /* 客户端 ID */
    public static final String CLIENT_ID = "client_id";

    /* 客户端真实 IP */
    public static final String X_FORWARDED_FOR = "x-forwarded-for";

    /* 客户端 IP */
    public static final String CLIENT_IP = "remote-addr";

    /* 客户端 IP */
    public static final String NON = "NON";

    /* 终端用户 IP */
    public static final String REMOTE_IP = "remoteip";

    /* 是否关闭请求 */
    public static final String IS_CLOSE = "IS_CLOSE";

    /* 令牌 */
    public static final String TOKEN = "token";

    /* 客户端 IP 格式 */
    public static final String CLIENT_IP_REG = "\\/?(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}):\\d{1,5}";

    /* 服务名格式 */
    public static final String SERVICE_REG = "(.*)\\.([0-9A-Za-z_]+)\\/([0-9A-Za-z_]+)";

    public static final String INTERFACE_REG = ".+Grpc\\.[^.]+ImplBase$";

    /* 追踪ID */
    public static final String TRACE_ID = "trace-id";

    public static final String LANGUAGE_CODE = "language-code";

    public static final String USERSESSION_ID = "usersessionid";

    public static final String CURRENCY_CODE = "currency-code";

    public static Set<String> services = new HashSet<>();

    public static <ReqT, RespT> String getClientIP(ServerCall<ReqT, RespT> serverCall) {
        String clientIP = "";
        for (Key<?> key : serverCall.getAttributes().keys()) {
            if (CLIENT_IP.equals(key.toString())) {
                String remoteAddr = serverCall.getAttributes().get(key).toString();
                Matcher matcher = Pattern.compile(GrpcUtil.CLIENT_IP_REG).matcher(remoteAddr);
                if (matcher.find()) {
                    clientIP = matcher.group(1);
                }
                break;
            }
        }
        return clientIP;
    }

    public static void api(Class<?> clazz) {
        try {
            String name = clazz.getCanonicalName();
            Class<?> grpc = Class.forName(name.substring(0, name.lastIndexOf(".")));
            Field serviceName = grpc.getField("SERVICE_NAME");
            Object obj = serviceName.get(null);
            // 获取方法
            for (Method method : clazz.getDeclaredMethods()) {
                log.info("Grpc API: {}/{}", obj, method.getName());
                if (!"bindService".equals(method.getName())) {
                    services.add(obj + "/" + method.getName());
                }
            }
        } catch (Exception e) {
            log.error("Load all grpc service failed.", e);
        }
    }

}
