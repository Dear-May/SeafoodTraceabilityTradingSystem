package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "specificationinfo")
@Data
public class SpecificationEntity implements Serializable {
    private final static long serialVersionUID = 1L;

    @Id
    @Column(name = "specificationid")
    private String specificationID;

    @Column(name = "mainproductid")
    private String mainProductID;

    @Column(name = "specname")
    private String specName;

    @Column(name = "price")
    private double price;

    @Column(name = "status")
    private boolean status;

    @Column(name = "updatetime")
    private Date updateTime;

    @Column(name = "number")
    private int number;

    @Column(name = "showurl")
    public String Showurl;

    @Override
    public String toString() {
        return "SpecificationEntity{" +
                "specificationID='" + specificationID + '\'' +
                ", mainProductID='" + mainProductID + '\'' +
                ", specName='" + specName + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", updateTime=" + updateTime +
                ", number=" + number +
                ", ShowUrl='" + Showurl + '\'' +
                '}';
    }
}
