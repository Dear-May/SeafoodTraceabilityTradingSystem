package com.shopping_c_backend.module.trace;

import lombok.Data;

@Data
public class TraceUserEntity {
    private int id;
    private int userID;
    private int shopID;
    private String token;
    private java.util.Date registerTime;
    private java.util.Date updateTime;
}
