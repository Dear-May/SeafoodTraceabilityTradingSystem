package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "live_message")
public class LiveMessageEntity {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "sendID")
    private int sendID;

    @Column(name = "sendType")
    private String sendType;

    @Column(name = "roomID")
    private int roomID;

    @Lob
    @Column(name = "message")
    private String message;

    @Lob
    @Column(name = "time")
    private Data time;
}
