package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "chat_sessions")
@Data
public class ChatSessionEntity {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "userId")
    private int userId;

    @Column(name = "shopId")
    private int shopId;

    @Lob
    @Column(name = "lastMessage")
    private String lastMessage;

    @Column(name = "lastMessageAt")
    private Date lastMessageAt;

    @Column(name = "unreadByUser")
    private int unreadByUser;

    @Column(name = "unreadByShop")
    private int unreadByShop;

    @Column(name = "openedByUser")
    private boolean openedByUser;

    @Column(name = "openedByShop")
    private boolean openedByShop;
}
