package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

@Data
public class UserBehaviorEntity {
    private int userId;
    private String goodId;
    private String behavior;

    public UserBehaviorEntity(int userid, String goodId, String order) {
        this.userId = userid;
        this.goodId = goodId;
        this.behavior = order;
    }

    @Override
    public String toString() {
        return "UserBehaviorEntity{" +
                "userId=" + userId +
                ", goodId='" + goodId + '\'' +
                ", behavior='" + behavior + '\'' +
                '}';
    }
}
