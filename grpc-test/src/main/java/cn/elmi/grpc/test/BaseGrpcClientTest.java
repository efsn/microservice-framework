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

package cn.elmi.grpc.test;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.testng.annotations.AfterTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * @author Arthur
 * @since 1.0
 */
public class BaseGrpcClientTest {

    private String certFile;
    private String certDomain;
    private String serverIp;
    private int serverPort;
    private String clientId;
    private String clientSecret;
    private String clientGrantType;

    private ManagedChannel channel;

    public BaseGrpcClientTest() {
        InputStream inStream = BaseGrpcClientTest.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            ResourceBundle resource = new PropertyResourceBundle(inStream);
            certFile = resource.getString("cert.path");
            certDomain = resource.getString("cert.domain");
            serverIp = resource.getString("server.ip");
            serverPort = Integer.parseInt(resource.getString("server.port"));
            clientId = resource.getString("client.id");
            clientSecret = resource.getString("client.secret");
            clientGrantType = resource.getString("client.grantType");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        channel = createTLSChannel();
    }

    public ManagedChannel createTLSChannel() {
        SslContext sslContext = null;
        try {
            sslContext = GrpcSslContexts.forClient().trustManager(CertFileUtil.getFile(certFile)).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        InetAddress address = null;
        try {
            address = InetAddress.getByAddress(certDomain, InetAddress.getByName(serverIp).getAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        ManagedChannel channel = NettyChannelBuilder.forAddress(new InetSocketAddress(address, serverPort))
                .negotiationType(NegotiationType.TLS).sslContext(sslContext).build();
        return channel;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientGrantType() {
        return clientGrantType;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    @AfterTest
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

}
