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

package cn.elmi.grpc.client.utils;

import io.grpc.Channel;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Arthur
 * @since 1.0
 */
public class GrpcClientUtil {

    public static final String REG = ".+(BlockingStub|FutureStub)";
    public static final String SUB_REG = ".+(Stub)";

    public static Object createGrpcClient(String name, Channel channel) throws Exception {
        String prefix = name.substring(0, name.lastIndexOf('.'));
        String postfix = findPostfix(name);
        if (StringUtils.isNotBlank(postfix)) {
            String methodName = "new" + postfix;
            Class<?> clientClass = Class.forName(prefix);
            Method method = clientClass.getMethod(methodName, Channel.class);
            return method.invoke(null, channel);
        }
        return null;
    }

    public static String findPostfix(String className) {
        String postfix = findPostfixByReg(className);
        return null != postfix ? postfix : findPostfixBySubReg(className);
    }

    public static String findPostfix(String className, String reg) {
        Matcher matcher = Pattern.compile(reg).matcher(className);
        return matcher.matches() ? matcher.group(1) : null;
    }

    public static String findPostfixByReg(String className) {
        return findPostfix(className, REG);
    }

    public static String findPostfixBySubReg(String className) {
        return findPostfix(className, SUB_REG);
    }

}
