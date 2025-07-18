package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

@Data
public class GoogleUserEntity {
    private final String source = "Google";
    private String id;
    private String name;
    private String email;

    @Override
    public String toString() {
        return "GoogleUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
