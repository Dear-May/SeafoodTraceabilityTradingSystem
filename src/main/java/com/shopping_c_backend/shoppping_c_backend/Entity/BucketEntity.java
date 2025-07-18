package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "bucketinfo")
@Data
public class BucketEntity {
    @Id
    @Column(name = "bucketid")
    private int bucketid;

    @Column(name = "userid")
    private int userid;

    @Column(name = "goodid")
    private String goodid;

    @Column(name = "specificationid")
    private String specificationid;

    @Column(name = "number")
    private int number;

    @Column(name = "inserttime")
    private Date inserttime;

    @Column(name = "updatetime")
    private Date updatetime;

    public BucketEntity() {
    }

    public BucketEntity(int userId, String goodid, String specid, int number) {
        this.userid = userId;
        this.goodid = goodid;
        this.specificationid = specid;
        this.number = number;
    }

    public BucketEntity(int userId, String goodid, String specid) {
        this.userid = userId;
        this.goodid = goodid;
        this.specificationid = specid;
    }

    @Override
    public String toString() {
        return "BucketEntity{" +
                "bucketid=" + bucketid +
                ", userid=" + userid +
                ", goodid='" + goodid + '\'' +
                ", specificationid='" + specificationid + '\'' +
                ", number=" + number +
                ", inserttime=" + inserttime +
                ", updatetime=" + updatetime +
                '}';
    }
}
