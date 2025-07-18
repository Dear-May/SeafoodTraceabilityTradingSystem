package com.shopping_c_backend.shoppping_c_backend.Manager;

import java.util.concurrent.*;

public class OrderTimeoutManager {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<String, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();

    public static void setOrderTimeout(String orderId, long timeoutMillis) {
        cancelTimeout(orderId);

        ScheduledFuture<?> task = scheduler.schedule(() -> {
            OrderStatusManager.setOrderStatus(orderId, "未扫码");
            System.out.println("订单超时重置：orderId=" + orderId);
        }, timeoutMillis, TimeUnit.MILLISECONDS);

        timeoutTasks.put(orderId, task);
    }

    public static void cancelTimeout(String orderId) {
        ScheduledFuture<?> task = timeoutTasks.remove(orderId);
        if (task != null) {
            task.cancel(false);
        }
    }
}

