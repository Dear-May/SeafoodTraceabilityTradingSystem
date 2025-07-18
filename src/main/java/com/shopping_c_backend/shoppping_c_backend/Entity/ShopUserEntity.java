package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "shopuserinfo")
@Data
public class ShopUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "shopid")
    private int shopid;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    private String role;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status;

    @Column(name = "inserttime")
    private Date inserttime;

    @Override
    public String toString() {
        return "ShopUserEntity{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", shopid=" + shopid +
                ", nickname='" + nickname + '\'' +
                ", role='" + role + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", inserttime=" + inserttime +
                '}';
    }

}
