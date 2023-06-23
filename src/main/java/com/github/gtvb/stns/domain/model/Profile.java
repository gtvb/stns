package com.github.gtvb.stns.domain.model;

public class Profile {
    private String uuid;
    private String userId;
    private String fullName;

    public Profile(String uuid, String userId, String fullName) {
        this.uuid = uuid;
        this.userId = userId;
        this.fullName = fullName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }
}
