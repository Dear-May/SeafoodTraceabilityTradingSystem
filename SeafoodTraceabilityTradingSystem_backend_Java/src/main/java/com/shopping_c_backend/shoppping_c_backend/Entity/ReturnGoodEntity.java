package com.shopping_c_backend.shoppping_c_backend.Entity;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "returngoodinfo")
public class ReturnGoodEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "returnId")
    private String returnId;

    @Column(name = "orderId")
    private String orderId;

    @Lob
    @Column(name = "text")
    private String text;

    @Lob
    @Column(name = "images")
    private JSONArray images;

    @Column(name = "workUserId")
    private int workUserId;

    @Column(name = "insertTime")
    private Date insertTime;

    @Column(name = "updateTime")
    private Date updateTime;

    @Column(name = "originalStatus")
    private String originalStatus;
}
