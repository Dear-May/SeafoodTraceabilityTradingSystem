package com.shopping_c_backend.shoppping_c_backend.Vo;

import lombok.Data;

@Data
public class Result {
    private int code;

    public Result(int code) {
        this.code = code;
    }

}
