package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "goodinfo")
@Data
public class GoodEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "GoodID")
    private String GoodID;

    @Column(name = "GoodName")
    private String GoodName;

    @Column(name = "Price")
    private double Price;

    @Column(name = "Comments")
    private int Comments;

    @Column(name = "ShowURL")
    private String ShowURL;

    @Column(name = "ShopID")
    private int ShopID;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "Location")
    private String Location;

    @Column(name = "uplodatime")
    private Date uplodatime;

    @Override
    public String toString() {
        return "GoogEntity{" +
                "GoodID='" + GoodID + '\'' +
                ", GoodName='" + GoodName + '\'' +
                ", Price=" + Price +
                ", Comments=" + Comments +
                ", ShowURL='" + ShowURL + '\'' +
                ", ShopID=" + ShopID +
                ", status=" + status +
                '}';
    }
}
