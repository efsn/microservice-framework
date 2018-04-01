package cn.elmi.grpc.server.utils;

import cn.elmi.grpc.server.exception.GrpcServerException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class InetAddressUtil {

    public static InetAddress getLocalHostLanAddress() {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
                    .hasMoreElements(); ) {
                NetworkInterface iface = ifaces.nextElement();
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress() && inetAddr.isReachable(2000)) {
                            return inetAddr;
                        } else if (null == candidateAddress) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }

            if (null != candidateAddress) {
                return candidateAddress;
            }

            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (null == jdkSuppliedAddress) {
                throw new UnknownHostException("the jdk InetAddress#getLocalHost unexpectedly returned null. ");
            }

            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException ue = new UnknownHostException("failed to determine LAN address: " + e);
            ue.initCause(e);
            throw new GrpcServerException(e);
        }
    }

}
