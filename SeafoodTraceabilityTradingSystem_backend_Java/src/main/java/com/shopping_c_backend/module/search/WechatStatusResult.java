package com.shopping_c_backend.module.search;

import lombok.Data;

@Data
public class WechatStatusResult {
    private int code;
    private String msg;
    private String openid;
}
