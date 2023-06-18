package com.github.gtvb.stns.domain.model;

public class User {
    String uuid;
    String username;
    String userPassword;
    String createdAt;

    public User(String uuid, String username, String userPassword, String createdAt) {
        this.uuid = uuid;
        this.username = username;
        this.userPassword = userPassword;
        this.createdAt = createdAt;
    }
}
