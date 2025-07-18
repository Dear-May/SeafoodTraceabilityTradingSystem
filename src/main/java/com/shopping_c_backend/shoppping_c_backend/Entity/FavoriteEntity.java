package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "favoriteinfo")
@Data
public class FavoriteEntity {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "userid")
    private int userid;

    @Column(name = "goodid")
    private String goodid;

    @Column(name = "time")
    private Date date;

    public FavoriteEntity(int userId, String goodId) {
        this.userid = userId;
        this.goodid = goodId;
    }

    public FavoriteEntity() {
    }

    @Override
    public String toString() {
        return "FavoriteEntity{" +
                "id=" + id +
                ", userid=" + userid +
                ", goodid='" + goodid + '\'' +
                ", date=" + date +
                '}';
    }
}
