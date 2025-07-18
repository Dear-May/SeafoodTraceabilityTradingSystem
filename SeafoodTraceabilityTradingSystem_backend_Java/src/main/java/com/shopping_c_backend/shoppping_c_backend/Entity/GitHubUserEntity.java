package com.shopping_c_backend.shoppping_c_backend.Entity;

import lombok.Data;

@Data
public class GitHubUserEntity {
    private final String source = "GitHub";
    private String login;
    private String id;
    private String email;

    @Override
    public String toString() {
        return "GitHubUser{" +
                "login='" + login + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}

