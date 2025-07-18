package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "chat_messages")
public class ChatMessageEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "senderId")
    private int senderId;

    @Column(name = "senderType")
    private String senderType;

    @Column(name = "receiverId")
    private int receiverId;

    @Column(name = "receiverType")
    private String receiverType;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "contentType")
    private String contentType;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "isRead")
    private boolean isRead;
}
