package com.shopping_c_backend.Vo;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String message;
    private Object data;

    public Result() {
    }

    public Result(int code) {
        this.code = code;
        this.message = "";
        this.data = null;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success() {
        return new Result(ResultCode.SUCCESS.getCode(), "success");
    }

    public static Result success(Object data) {
        return new Result(ResultCode.SUCCESS.getCode(), "success", data);
    }

    public static Result success(String msg, Object data) {
        return new Result(ResultCode.SUCCESS.getCode(), msg, data);
    }

    public static Result error(int code, String msg) {
        return new Result(code, msg);
    }

    public static Result error(ResultCode resultCode, String msg) {
        return new Result(resultCode.getCode(), msg);
    }

    public static Result error(ResultCode resultCode) {
        return new Result(resultCode.getCode(), resultCode.name());
    }
}
