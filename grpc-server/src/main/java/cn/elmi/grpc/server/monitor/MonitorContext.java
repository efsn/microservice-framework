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

import cn.elmi.grpc.etcd.register.EtcdRegister;
import cn.elmi.grpc.server.consts.MessageType;
import cn.elmi.grpc.server.props.GrpcServerProp;
import cn.elmi.grpc.server.utils.InetAddressUtil;
import com.hnair.components.cache.CacheChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arthur
 * @since 1.0
 */
@Component
@Slf4j
public class MonitorContext {

    @Value("${grpc.server.airline:00}")
    private String airline;

    @Autowired(required = false)
    private EtcdRegister etcdRegister;

    private Set<String> forbidOTA = new HashSet<>(100);
    private Set<String> forbidAPI = new HashSet<>(50);

    @Autowired
    private GrpcServerProp grpcServerProp;

    @Autowired
    private CacheChannel cacheChannel;

    private String node;

    public void execute(String channel, String message) {
        if (channel.endsWith(airline)) {
            MessageType ft = MessageType.get(channel.substring(0, channel.length() - 2));
            // 禁用渠道
            if (MessageType.OTA_FORBID == ft) {
                log.info("Forbid OTA: {}.", message);
                forbidOTA.add(message);
                cacheChannel.evict("client", message);
            }
            // 全渠道禁用接口
            else if (MessageType.API_FORBID == ft) {
                log.info("Forbid API: {}.", message);
                forbidAPI.add(message);
            }
            // 渠道解禁
            else if (MessageType.OTA_ENABLE == ft) {
                log.info("Enable OTA: {}.", message);
                forbidOTA.remove(message);
                cacheChannel.evict("client", message);
            }
            // 全渠道启用接口
            else if (MessageType.API_ENABLE == ft) {
                log.info("Enable API: {}.", message);
                forbidAPI.remove(message);
            }
            // 下线：即不再往 etcd 注册
            else if (MessageType.OFFLINE == ft) {
                log.debug("Offline:  {}", message);
                if (null == node) {
                    node = InetAddressUtil.getLocalHostLanAddress().getHostAddress() + ":"
                            + String.valueOf(grpcServerProp.getPort());
                }

                if (node.equals(message)) {
                    log.info("Offline:  {}", message);
                    etcdRegister.setStarted(false);
                }
            }
            // 上线：往 etcd 注册
            else if (MessageType.ONLINE == ft) {
                log.debug("Online:  {}", message);
                if (null == node) {
                    node = InetAddressUtil.getLocalHostLanAddress() + ":" + grpcServerProp.getPort();
                }

                if (node.equals(message)) {
                    log.info("Online:  {}", message);
                    etcdRegister.setStarted(true);
                }
            }
        }
    }

    public boolean isPosForbid(String pos) {
        return forbidOTA.contains(pos);
    }

    public boolean isApiForbid(String api) {
        return forbidAPI.contains(api);
    }
}
