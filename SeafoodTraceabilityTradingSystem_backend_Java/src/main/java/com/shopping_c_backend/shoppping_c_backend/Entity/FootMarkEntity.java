package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "footmarkinfo")
@Data
public class FootMarkEntity {
    @Id
    @Column(name = "id")
    int id;

    @Column(name = "userid")
    int userid;

    @Column(name = "goodid")
    String goodId;

    @Column(name = "time")
    Date time;

    @Column(name = "count")
    int count;

    @Override
    public String toString() {
        return "FootMarkEntity{" +
                "id=" + id +
                ", userid=" + userid +
                ", goodId='" + goodId + '\'' +
                ", time=" + time +
                ", count=" + count +
                '}';
    }
}
