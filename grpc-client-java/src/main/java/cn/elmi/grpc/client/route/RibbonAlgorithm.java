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

package cn.elmi.grpc.client.route;

import cn.elmi.grpc.client.EtcdNotificationUpdate;
import cn.elmi.grpc.client.GrpcServer;
import cn.elmi.grpc.client.GrpcServerList;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.loadbalancer.*;
import mousio.etcd4j.EtcdClient;

/**
 * @author Arthur
 * @version 1.0
 */
public class RibbonAlgorithm implements RouteAlgorithm {

    private final String server;
    private final EtcdClient etcdClient;
    private DynamicServerListLoadBalancer<GrpcServer> loadBalancer;

    public RibbonAlgorithm(String className, EtcdClient etcdClient) {
//        String postfix = GrpcClientUtil.findPostfix(className);
//        this.server = className.substring(0, className.lastIndexOf(postfix));
        this.server = className;
        this.etcdClient = etcdClient;
        init();
    }

    @Override
    public void init() {
        DefaultClientConfigImpl config = DefaultClientConfigImpl.getClientConfigWithDefaultValues();
        config.setProperty(CommonClientConfigKey.ServerListUpdaterClassName, EtcdNotificationUpdate.class.getName());

        String path = "/grpc/server/" + server;
        loadBalancer = new DynamicServerListLoadBalancer<>(config, new AvailabilityFilteringRule(), new DummyPing(),
                new GrpcServerList(etcdClient, server), new ZoneAffinityServerListFilter<>(),
                new EtcdNotificationUpdate(etcdClient, path));
    }

    @Override
    public Node getTransportNode() {
        Server server = loadBalancer.chooseServer();
        if (null != server) {
            return new Node(server.getHost(), server.getPort());
        }
        return null;
    }

    public DynamicServerListLoadBalancer<GrpcServer> getLoadBalancer() {
        return loadBalancer;
    }

}
