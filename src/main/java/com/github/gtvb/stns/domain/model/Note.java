package com.github.gtvb.stns.domain.model;

public class Note {
    private String uuid;
    private String userId;
    private String title;
    private String contents;
    private String createdAt;

    public Note(String uuid, String userId, String title, String contents, String createdAt) {
        this.uuid = uuid;
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }
    
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
