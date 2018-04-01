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

package cn.elmi.microservice.repository;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arthur
 * @since 1.0
 */
@Data
@Document(collection = "CLIENT")
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String clientId;
    private String clientSecret;
    private String grantType;
    private Set<String> whiteIp = new HashSet<>();
    private Set<String> blackIp = new HashSet<>();
    private Set<String> permission = new HashSet<>();
    private String status;
    private String profileId; // 渠道对应的账号
    private Validation validation = new Validation(); // 订单校验

    @Data
    public static class Validation implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer maxUnPaidNum;
        private Integer maxUnPaidDate;

        @Override
        public String toString() {
            return MessageFormat.format("Validation{maxUnPaidNum={0}, maxUnPaidDate={1}}", maxUnPaidNum, maxUnPaidDate);
        }

    }
}
