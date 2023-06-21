package com.github.gtvb.stns.domain.model;

public class User {
    String uuid;
    String username;
    String userPassword;
    String createdAt;

    public User() {

    }

    public User(String uuid, String username, String userPassword, String createdAt) {
        this.uuid = uuid;
        this.username = username;
        this.userPassword = userPassword;
        this.createdAt = createdAt;
    }

    public User(String uuid, String username, String userPassword) {
        this.uuid = uuid;
        this.username = username;
        this.userPassword = userPassword;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}