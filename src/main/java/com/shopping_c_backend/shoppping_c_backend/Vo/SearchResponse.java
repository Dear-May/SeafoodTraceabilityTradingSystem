package com.shopping_c_backend.shoppping_c_backend.Vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class SearchResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String status; // 接收状态
    private List<Result> data; // 接收数据部分
    private String token; // 接收 token

    // 内部类表示结果
    @Setter
    @Getter
    public static class Result implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private long milvus_id; // 图像对应的 milvus ID
        private double distance; // 搜索距离
    }
}
