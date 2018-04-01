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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Arthur
 * @since 1.0
 */
public class DirectAlgorithm implements RouteAlgorithm {

    private final String directAddress;
    private static final int MAX_INDEX = Integer.MAX_VALUE;

    private List<Node> nodeList = new CopyOnWriteArrayList<>();
    private AtomicInteger index = new AtomicInteger(0);

    public DirectAlgorithm(String directAddress) {
        this.directAddress = directAddress;
        init();
    }

    @Override
    public void init() {
        String[] addressList = directAddress.split(",");
        for (String address : addressList) {
            String[] addrs = address.split(":");
            nodeList.add(new Node(addrs[0], Integer.parseInt(addrs[1])));
        }
    }

    @Override
    public Node getTransportNode() {
        int size = nodeList.size();

        int i = index.getAndIncrement();
        if (i > MAX_INDEX) {
            index.set(0);
        }

        return 0 == size ? null : nodeList.get(i % size);
    }

}
