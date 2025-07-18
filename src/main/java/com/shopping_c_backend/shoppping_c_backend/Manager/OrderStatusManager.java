package com.shopping_c_backend.shoppping_c_backend.Manager;

import java.util.concurrent.ConcurrentHashMap;

public class OrderStatusManager {
    private static final ConcurrentHashMap<String, String> orderStatus = new ConcurrentHashMap<>();

    // 设置订单状态
    public static void setOrderStatus(String orderId, String status) {
        orderStatus.put(orderId, status);
    }

    // 获取订单状态
    public static String getOrderStatus(String orderId) {
        return orderStatus.getOrDefault(orderId, "未扫码");
    }

    // 检查是否可以扫码
    public static boolean canScan(String orderId) {
        String status = getOrderStatus(orderId);
        return !"已扫码".equals(status); // 仅当状态不是 "已扫码" 时允许扫码
    }
}

