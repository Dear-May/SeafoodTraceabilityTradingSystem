package com.shopping_c_backend.module.order;

import lombok.Data;

@Data
public class OrderRequest {
    private String orderId; // 订单号
    private double amount;  // 金额
}
