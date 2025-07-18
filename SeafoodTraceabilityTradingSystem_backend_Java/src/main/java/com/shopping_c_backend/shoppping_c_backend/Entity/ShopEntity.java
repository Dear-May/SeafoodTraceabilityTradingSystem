package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "shopinfo")
@Data
public class ShopEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ShopID")
    private int ShopID;

    @Column(name = "ShopName")
    private String ShopName;

    @Column(name = "ShopAvatar")
    private String ShopAvatar;

    @Column(name = "ShopDesc")
    private String ShopDesc;

    @Column(name = "status")
    private String status;

    @Column(name = "livestatus")
    private String livestatus;

    @Override
    public String toString() {
        return "ShopEntity{" +
                "ShopID=" + ShopID +
                ", ShopName='" + ShopName + '\'' +
                ", ShopAvatar='" + ShopAvatar + '\'' +
                ", ShopDesc='" + ShopDesc + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
