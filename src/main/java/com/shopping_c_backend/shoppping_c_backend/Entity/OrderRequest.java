package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

@Data
public class OrderRequest {
    private String orderId; // 订单号
    private double amount;  // 金额
}
