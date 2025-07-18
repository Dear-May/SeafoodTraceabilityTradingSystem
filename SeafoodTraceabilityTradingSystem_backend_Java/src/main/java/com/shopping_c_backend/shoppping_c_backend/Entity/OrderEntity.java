package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "orderinfo")
@Data
public class OrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "orderid")
    private String orderid;

    @Column(name = "userid")
    private int userid;

    @Column(name = "shopid")
    private int shopid;

    @Column(name = "addressid")
    private int addressid;

    @Column(name = "totalprice")
    private double totalprice;

    @Column(name = "insertdate")
    private Date insertdate;

    @Column(name = "updatedate")
    private Date updatedate;

    @Column(name = "status")
    private String status;

    @Column(name = "remark")
    private String remark;

    @Override
    public String toString() {
        return "OrderEntity{" +
                "orderid='" + orderid + '\'' +
                ", userid=" + userid +
                ", shopid=" + shopid +
                ", addressid=" + addressid +
                ", totalprice=" + totalprice +
                ", insertdate=" + insertdate +
                ", updatedate=" + updatedate +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
