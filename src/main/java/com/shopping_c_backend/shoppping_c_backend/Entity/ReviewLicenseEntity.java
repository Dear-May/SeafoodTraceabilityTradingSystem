package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reviewlicense")
@Data
public class ReviewLicenseEntity implements Serializable {
    private static final long serialVersionUID = 191354035470386396L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "shopid")
    private Integer shopid;

    @Column(name = "imageurl")
    private String imageurl;

    @Column(name = "inserttime")
    private Date inserttime;

    @Column(name = "updatetime")
    private Date updatetime;

    @Column(name = "mangerid")
    private Integer mangerid;

    @Column(name = "status")
    private String status;
    /**
     * 社会信用代码
     */
    @Column(name = "socialcreditcode")
    private String socialcreditcode;
    /**
     * 名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 注册地址
     */
    @Column(name = "location")
    private String location;
    /**
     * 注册资本
     */
    @Column(name = "registeredcapital")
    private String registeredcapital;
    /**
     * 实缴资本
     */
    @Column(name = "paidincapital")
    private String paidincapital;
    /**
     * 成立日期
     */
    @Column(name = "dateofestablishment")
    private String dateofestablishment;
    /**
     * 经营范围
     */
    @Column(name = "businessscope")
    private String businessscope;
    /**
     * 法定代表人
     */
    @Column(name = "legalrepresentative")
    private String legalrepresentative;
    /**
     * 企业类型
     */
    @Column(name = "type")
    private String type;
    /**
     * 登记机关
     */
    @Column(name = "registrationauthority")
    private String registrationauthority;
    /**
     * 组成形式
     */
    @Column(name = "compositionform")
    private String compositionform;
    /**
     * 证件编号
     */
    @Column(name = "idnumber")
    private String idnumber;
    /**
     * 有效期限
     */
    @Column(name = "periodofvalidity")
    private String periodOfValidity;
    /**
     * 有效期开始日期
     */
    @Column(name = "startingdateofvalidityperiod")
    private String startingdateofvalidityperiod;
    /**
     * 核准日期
     */
    @Column(name = "dateofapproval")
    private String dateofapproval;
    /**
     * 税务登记号
     */
    @Column(name = "taxregistrationnumber")
    private String taxregistrationnumber;


}

