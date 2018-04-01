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

import cn.elmi.grpc.server.consts.RequestStatus;
import lombok.Data;

/**
 * @author Arthur
 * @since 1.0
 */
@Data
public class GrpcRequest {

    private String clientID;
    private String clientIP;
    private String serverIP;
    private String app;
    private String service;
    private RequestStatus status;
    /* 耗时 */
    private long elapse = 0;
    private int number;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("grpc_request[");
        sb.append(clientID).append(":");
        sb.append(clientIP).append(":");
        sb.append(app).append(":");
        sb.append(elapse).append(":");
        sb.append(service).append(":");
        sb.append(status).append("]");
        return sb.toString();
    }
}
