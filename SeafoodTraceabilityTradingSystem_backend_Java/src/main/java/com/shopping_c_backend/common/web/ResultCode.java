package com.shopping_c_backend.common.web;

public enum ResultCode {
    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    INVALID_USERNAME(402),
    ERROR(500);

    private final int code;

    ResultCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResultCode fromCode(int code) {
        for (ResultCode rc : ResultCode.values()) {
            if (rc.code == code) {
                return rc;
            }
        }
        return ERROR;
    }
}
