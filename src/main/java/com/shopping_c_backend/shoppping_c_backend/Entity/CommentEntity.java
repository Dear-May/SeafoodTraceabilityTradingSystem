package com.shopping_c_backend.shoppping_c_backend.Entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "commentinfo")
@Data
public class CommentEntity {
    @Id
    @Column(name = "commentid")
    private int commentid;

    @Column(name = "orderid")
    private String orderid;

    @Lob
    @Column(name = "text")
    private String text;

    @Lob
    @Column(name = "images")
    private JSONArray images;

    @Column(name = "rate")
    private float rate;

    @Column(name = "inserttime")
    private Date inserttime;

    @Column(name = "updatetime")
    private Date updatetime;

    @Column(name = "status")
    private String status;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
