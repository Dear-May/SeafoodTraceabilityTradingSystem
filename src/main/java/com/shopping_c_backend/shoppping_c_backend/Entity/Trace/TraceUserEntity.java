package com.shopping_c_backend.shoppping_c_backend.Entity.Trace;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "traceuserinfo")
public class TraceUserEntity {
    @Id
    @Column(name = "linkID", nullable = false)
    private String linkID;

    @Column(name = "traceID")
    private String traceID;

    @Column(name = "userID")
    private int userID;

    @Column(name = "informationID")
    private String informationID;

    @Column(name = "type")
    private String type;

    @Column(name = "insertTime")
    private Date insertTime;

}