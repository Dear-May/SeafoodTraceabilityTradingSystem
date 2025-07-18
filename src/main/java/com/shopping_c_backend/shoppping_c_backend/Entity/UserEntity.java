package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "userinfo")
@Data
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "nicknamechangtime")
    private Date nicknamechangtime;

    @Column(name = "gender")
    private String gender;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "GiteeID")
    private String GiteeID;

    @Column(name = "GiteeName")
    private String GiteeName;

    @Column(name = "GitHubID")
    private String GitHubID;

    @Column(name = "GitHubName")
    private String GitHubName;

    @Column(name = "GoogleID")
    private String GoogleID;

    @Column(name = "GoogleName")
    private String GoogleName;

    @Column(name = "WechatID")
    private String WechatID;

    private String WechatName = "微信用户";

    @Column(name = "create_at")
    private Date create_at;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", GiteeID='" + GiteeID + '\'' +
                ", GiteeName='" + GiteeName + '\'' +
                ", GitHubID='" + GitHubID + '\'' +
                ", GitHubName='" + GitHubName + '\'' +
                ", GoogleID='" + GoogleID + '\'' +
                ", GoogleName='" + GoogleName + '\'' +
                ", WechatID='" + WechatID + '\'' +
                ", WechatName='" + WechatName + '\'' +
                '}';
    }
}
