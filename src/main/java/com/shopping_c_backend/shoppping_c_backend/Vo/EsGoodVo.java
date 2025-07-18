package com.shopping_c_backend.shoppping_c_backend.Vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EsGoodVo {
    private String goodId;         // 商品ID
    private String goodName;       // 商品名称
    private Double price;          // 商品价格
    private Integer comments;      // 评论数
    private Date uploadTime;       // 上架时间
    private Boolean status;        // 状态
    private String showUrl;        // 展示链接
    private Integer shopId;        // 店铺ID
    private String location;       // 商品位置

    private List<SpecificationVo> specifications; // 商品规格列表

    @Data
    public static class SpecificationVo {
        private String specificationId; // 规格ID
        private String specName;        // 规格名称
        private Double price;           // 规格价格
        private Boolean status;         // 规格状态
        private Date updateTime;        // 更新时间
        private Integer number;         // 库存数量
        private String showUrl;         // 展示链接

        public SpecificationVo() {
        }

        public SpecificationVo(String specificationID, String specName, double price, boolean status, Date updateTime, int number, String showurl) {
            this.specificationId = specificationID;
            this.specName = specName;
            this.price = price;
            this.status = status;
            this.updateTime = updateTime;
            this.number = number;
            this.showUrl = showurl;
        }
    }
}

