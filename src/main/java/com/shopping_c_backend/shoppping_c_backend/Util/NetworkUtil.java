package com.shopping_c_backend.shoppping_c_backend.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil {
    public static String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to get local IP address", e);
        }
    }
}

