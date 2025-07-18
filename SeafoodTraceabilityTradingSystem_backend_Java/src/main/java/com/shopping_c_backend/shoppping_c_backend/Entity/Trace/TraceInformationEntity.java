package com.shopping_c_backend.shoppping_c_backend.Entity.Trace;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "traceinformation")
public class TraceInformationEntity {
    @Id
    @Column(name = "linkID", nullable = false)
    private String linkID;

    @Column(name = "traceID")
    private String traceID;

    @Column(name = "insertTime")
    private Date insertTime;

    @Lob
    @Column(name = "text")
    private String text;

    @Lob
    @Column(name = "imgUrL")
    private JSONArray imgUrL;

}