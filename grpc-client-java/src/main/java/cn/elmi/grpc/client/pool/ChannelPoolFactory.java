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

package cn.elmi.grpc.client.pool;

import cn.elmi.grpc.client.exception.GrpcClientException;
import cn.elmi.grpc.client.route.Node;
import cn.elmi.grpc.client.utils.CertFileUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arthur
 * @since 1.0
 */
public class ChannelPoolFactory extends BaseKeyedPooledObjectFactory<Node, ManagedChannel> {

    private static Map<String, Long> forbiddenMap = new ConcurrentHashMap<>();

    private static final int FORBIDDEN_TIME = 5000;
    private boolean tls;
    private String cert;
    private String domain;

    public ChannelPoolFactory() {
    }

    public ChannelPoolFactory(boolean tls, String cert, String domain) {
        this.tls = tls;
        this.cert = cert;
        this.domain = domain;
    }

    public boolean validateObject(Node key, PooledObject<ManagedChannel> value) {
        ManagedChannel channel = value.getObject();
        return !channel.isShutdown() && !channel.isTerminated();
    }

    @Override
    public void destroyObject(Node key, PooledObject<ManagedChannel> value) {
        ManagedChannel channel = value.getObject();
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }

    @Override
    public ManagedChannel create(Node key) {
        ManagedChannel channel = null;
        String address = key.getIp() + ":" + key.getPort();
        try {
            channel = tls ? createTLSChannel(key)
                    : ManagedChannelBuilder.forAddress(key.getIp(), key.getPort()).usePlaintext().build();
            if (forbiddenMap.containsKey(address)
                    && System.currentTimeMillis() - forbiddenMap.get(address).longValue() < FORBIDDEN_TIME) {
                throw new GrpcClientException(FORBIDDEN_TIME + "ms forbid to connect the node: " + address);
            }
        } catch (Exception e) {
            forbiddenMap.put(address, System.currentTimeMillis());
            throw new GrpcClientException(ExceptionUtils.getMessage(e) + " ip is " + key.getIp() + ":" + key.getPort(),
                    e);
        }

        return channel;
    }

    @Override
    public PooledObject<ManagedChannel> wrap(ManagedChannel value) {
        return new DefaultPooledObject<>(value);
    }

    private ManagedChannel createTLSChannel(Node key) {
        SslContext sslContext = null;
        try {
            sslContext = GrpcSslContexts.forClient().trustManager(CertFileUtil.getFile(cert)).build();
        } catch (Exception e) {
            throw new GrpcClientException(e);
        }

        InetAddress address = null;
        try {
            address = InetAddress.getByAddress(domain, InetAddress.getByName(key.getIp()).getAddress());
        } catch (UnknownHostException e) {
            throw new GrpcClientException(e);
        }

        ManagedChannel channel = NettyChannelBuilder.forAddress(new InetSocketAddress(address, key.getPort()))
                .negotiationType(NegotiationType.TLS).sslContext(sslContext).build();
        return channel;
    }

}
