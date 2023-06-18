package com.github.gtvb.stns.domain.model;

public class Note {
    String uuid;
    String userId;
    String title;
    String contents;
    String createdAt;

    public Note(String uuid, String userId, String title, String contents, String createdAt) {
        this.uuid = uuid;
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.createdAt = createdAt;
    }
}
