package com.github.gtvb.stns.domain.model;

public class Profile {
    String uuid;
    String userId;
    // String noteDisplayStyle;
    String fullName;

    public Profile(String uuid, String userId, String fullName) {
        this.uuid = uuid;
        this.userId = userId;
        // this.noteDisplayStyle = noteDisplayStyle;
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
