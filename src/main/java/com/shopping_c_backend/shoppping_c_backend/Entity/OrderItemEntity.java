package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "orderiteminfo")
@Data
public class OrderItemEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "orderitemid")
    private String orderitemid;

    @Column(name = "orderid")
    private String orderid;

    @Column(name = "good_id")
    private String good_id;

    @Column(name = "spec_id")
    private String spec_id;

    @Column(name = "goodname")
    private String goodname;

    @Column(name = "specname")
    private String specname;

    @Column(name = "specprice")
    private double specprice;

    @Column(name = "specnumber")
    private int specnumber;

    @Column(name = "specimg")
    private String specimg;

    @Override
    public String toString() {
        return "OrderItemEntity{" +
                "orderitemid='" + orderitemid + '\'' +
                ", orderid='" + orderid + '\'' +
                ", good_id='" + good_id + '\'' +
                ", spec_id='" + spec_id + '\'' +
                ", goodname='" + goodname + '\'' +
                ", specname='" + specname + '\'' +
                ", specprice=" + specprice +
                ", specnumber=" + specnumber +
                ", specimg='" + specimg + '\'' +
                '}';
    }
}
