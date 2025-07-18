package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "addressinfo")
@Data
public class AddressEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "userid")
    private int userid;

    @Column(name = "consignee")
    private String consignee;

    @Column(name = "phone")
    private String phone;

    @Column(name = "country")
    private String country = "china";

    @Column(name = "area")
    private String area;

    @Column(name = "detailed_address")
    private String detailed_address;

    @Column(name = "status")
    private String status;

    public AddressEntity(int id, int userid, String consignee, String phone, String country, String area, String detailed_address, String status) {
        this.id = id;
        this.userid = userid;
        this.consignee = consignee;
        this.phone = phone;
        this.area = area;
        this.detailed_address = detailed_address;
        this.status = status;
    }


    public AddressEntity(int id, int userid, String consignee, String phone, String area, String detailed_address, String status) {
        this.id = id;
        this.userid = userid;
        this.consignee = consignee;
        this.phone = phone;
        this.area = area;
        this.detailed_address = detailed_address;
        this.status = status;
    }

    public AddressEntity(int userid, String consignee, String phone, String country, String area, String detailed_address, String status) {
        this.userid = userid;
        this.consignee = consignee;
        this.phone = phone;
        this.area = area;
        this.detailed_address = detailed_address;
        this.status = status;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "id=" + id +
                ", userid=" + userid +
                ", consignee='" + consignee + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", area='" + area + '\'' +
                ", detailed_address='" + detailed_address + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
