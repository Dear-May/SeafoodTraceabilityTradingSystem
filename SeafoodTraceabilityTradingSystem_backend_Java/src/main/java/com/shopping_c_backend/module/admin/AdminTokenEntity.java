package com.shopping_c_backend.module.admin;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "adminToken")
public class AdminTokenEntity {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "token")
    private String token;
}
