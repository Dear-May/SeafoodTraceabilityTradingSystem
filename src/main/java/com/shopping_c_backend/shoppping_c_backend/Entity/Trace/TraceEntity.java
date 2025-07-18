package com.shopping_c_backend.shoppping_c_backend.Entity.Trace;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "traceinfo")
@Data
public class TraceEntity {
    @Id
    @Column(name = "traceID")
    private String traceID;

    @Column(name = "shopID")
    private int shopID;

    @Column(name = "insertTime")
    private Date insertTime;

    @Column(name = "updateTime")
    private Date updateTime;

}
