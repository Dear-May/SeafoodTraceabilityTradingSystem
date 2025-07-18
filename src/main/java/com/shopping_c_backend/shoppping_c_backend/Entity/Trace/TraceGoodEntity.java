package com.shopping_c_backend.shoppping_c_backend.Entity.Trace;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tracegoodinfo")
@Data
public class TraceGoodEntity {
    @Id
    @Column(name = "linkID")
    private String linkID;

    @Column(name = "traceID")
    private String traceID;

    @Column(name = "goodID")
    private String goodID;
}
