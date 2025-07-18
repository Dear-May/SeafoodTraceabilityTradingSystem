package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThirdPartyEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    String source;
    String id;
    String name;

    @Override
    public String toString() {
        return "ThirdPartyEntity{" +
                "source='" + source + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
