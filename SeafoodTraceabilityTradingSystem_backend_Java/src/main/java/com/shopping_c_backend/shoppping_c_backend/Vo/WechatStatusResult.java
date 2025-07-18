package com.shopping_c_backend.shoppping_c_backend.Vo;

import lombok.Data;

@Data
public class WechatStatusResult {
    private int code;
    private String msg;
    private String openid;
}
