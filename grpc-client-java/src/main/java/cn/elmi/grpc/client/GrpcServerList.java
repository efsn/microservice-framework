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

package cn.elmi.grpc.client;

import cn.elmi.grpc.client.route.Node;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;
import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arthur
 * @since 1.0
 */
@Slf4j
public class GrpcServerList extends AbstractServerList<GrpcServer> {

    private final EtcdClient etcdClient;
    private String serviceId;

    public GrpcServerList(EtcdClient etcdClient, String serviceId) {
        this.etcdClient = etcdClient;
        this.serviceId = serviceId;
    }

    @Override
    public List<GrpcServer> getInitialListOfServers() {
        return getServers();
    }

    @Override
    public List<GrpcServer> getUpdatedListOfServers() {
        return getServers();
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        serviceId = clientConfig.getClientName();
    }

    private List<GrpcServer> getServers() {
        if (null == etcdClient) {
            log.warn("etcd client is null.");
            return Collections.emptyList();
        }

        try {
            EtcdKeysResponse response = etcdClient.getDir("/grpc/server/".concat(serviceId)).send().get();

            if (CollectionUtils.isEmpty(response.node.nodes)) {
                log.warn("reponse node is empty.");
                return Collections.emptyList();
            }

            List<GrpcServer> servers = new ArrayList<>();
            response.node.nodes.stream().forEach(x -> {
                String appName = extractAppName(x.getKey());
                Node thriftNodeAddress = addressToNode(x.getValue());

                GrpcServer server = new GrpcServer(appName, thriftNodeAddress.getIp(), thriftNodeAddress.getPort());
                servers.add(server);
            });

            log.info("service id {} url is [{}]", serviceId, servers.stream()
                    .map(x -> x.getHost().concat(":").concat(x.getHostPort())).collect(Collectors.joining(",")));
            return servers;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Node addressToNode(String path) {
        String address = extractLastPath(path);
        String[] ipport = address.split(":");
        return new Node(ipport[0], new Integer(ipport[1]));
    }

    private String extractLastPath(String path) {
        int lastSlash = path.lastIndexOf("/");
        return path.substring(lastSlash + 1, path.length());
    }

    private String extractAppName(String path) {
        int lastSlash = path.lastIndexOf("/");
        String lastPath = path.substring(0, lastSlash);
        return extractLastPath(lastPath);
    }

}
