package com.shopping_c_backend.module.live;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "live_online_user")
public class LiveOnlineUserEntity {
    private int onlineUserId;
    private int roomId;
    private String username;
    private String userAvatar;


}
