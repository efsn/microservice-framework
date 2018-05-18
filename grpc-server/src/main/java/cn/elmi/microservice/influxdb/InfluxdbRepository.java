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

package cn.elmi.microservice.influxdb;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Method;

/**
 * @author Arthur
 * @since 1.0
 */
//@Component
@Slf4j
public class InfluxdbRepository {
    @Autowired
    private InfluxDB influxDB;

    @Autowired
    private InfluxdbProp influxdbProperties;

    /**
     * 保存请求记录
     *
     * @param request
     */
    @Async
    public void saveRequest(GrpcRequest request) {
        Point point = Point.measurement(influxdbProperties.getMeasurement()).addField("id", request.getClientID())
                .addField("ip", request.getClientIP()).addField("app", request.getApp())
                .addField("service", request.getService()).addField("status", request.getStatus().name())
                .addField("number", request.getNumber()).build();
        log.debug("Save point: {}.", point.toString());
        influxDB.write(influxdbProperties.getDbname(), influxdbProperties.getRetention(), point);
    }

    /**
     * 保存请求记录
     *
     * @param request
     */
    @Async
    public void saveRequestByOkHttp(GrpcRequest request) {
        String str = createInfluxdbData(request);
        RequestBody body = RequestBody.create(MediaType.parse("application/text; charset=utf-8"), str);
        Request req = new Request.Builder().url(influxdbProperties.getInsertRequestURL()).post(body).build();
        OkHttpUtil.enqueue(req);
    }

    /**
     * <pre>
     * 生成 influxdb 数据
     * 格式：grpc_request,key=value,...,key=value number=1
     * </pre>
     *
     * @param request
     * @return
     */
    public String createInfluxdbData(GrpcRequest request) {
        StringBuffer sb = new StringBuffer(influxdbProperties.getMeasurement());
        for (Method method : GrpcRequest.class.getMethods()) {
            String methodName = method.getName();
            if (!methodName.startsWith("get")) {
                continue;
            }

            if (methodName.equals("getClientID")) {
                sb.append(",").append("id=").append(request.getClientID());
            } else if (methodName.equals("getClientIP")) {
                sb.append(",").append("ip=").append(request.getClientIP());
            } else if (methodName.equals("getServerIP")) {
                sb.append(",").append("server=").append(request.getServerIP());
            } else if (!methodName.equals("getNumber") && !methodName.equals("getClass")
                    && !methodName.equals("getElapse")) {
                String prop = methodName.substring(3);
                char[] cs = prop.toCharArray();
                cs[0] = Character.toLowerCase(cs[0]);
                prop = new String(cs);
                try {
                    sb.append(",").append(prop).append("=").append(method.invoke(request));
                } catch (Exception e) {
                    log.error("GrpcRequest transfer failed.", e);
                }
            }
        }
        sb.append(" ").append("elapse=").append(request.getElapse()).append(",number=1");
        log.debug("Grpc request: {}", sb);
        return sb.toString();
    }
}
