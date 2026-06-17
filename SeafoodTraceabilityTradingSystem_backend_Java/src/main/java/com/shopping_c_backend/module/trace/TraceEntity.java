package com.shopping_c_backend.module.trace;

import lombok.Data;

@Data
public class TraceEntity {
    private String traceID;
    private String goodID;
    private String infoID;
    private String shopID;
    private String status;
    private java.util.Date updateTime;
    private java.util.Date insertTime;
}
