package com.shopping_c_backend.module.trace;

import lombok.Data;

@Data
public class TraceInformationEntity {
    private String infoID;
    private String traceID;
    private String info;
    private int userID;
    private int shopID;
    private String images;
    private java.util.Date updateTime;
    private java.util.Date insertTime;
}
