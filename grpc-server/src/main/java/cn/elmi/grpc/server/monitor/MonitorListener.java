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

package cn.elmi.grpc.server.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
@Component
public class MonitorListener extends JedisPubSub {

    public static final String MONITER_PATTERNS = "grpc.microservice.*";

    @Autowired
    private MonitorContext monitorContext;

    public void onSubscribe(String channel, int subscribedChannels) {
        log.debug("onSubscribe: channel[], subscribedChannels[{}]", channel, subscribedChannels);
    }

    public void onPSubscribe(String pattern, int subscribedChannels) {
        log.debug("onPSubscribe: pattern[{}], subscribedChannels[{}]", pattern, subscribedChannels);
    }

    public void onMessage(String channel, String message) {
        log.debug("onMessage: channel[{}], message[{}]", channel, message);
    }

    public void onPMessage(String pattern, String channel, String message) {
        log.info("onPMessage:pattern[{}], channel[{}], message[{}]", pattern, channel, message);
        monitorContext.execute(channel, message);
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        log.debug("onUnsubscribe: channel[{}], subscribedChannels[{}]", channel, subscribedChannels);
    }

    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        log.debug("onPUnsubscribe: pattern[{}], subscribedChannels[{}]", pattern, subscribedChannels);
    }

}
