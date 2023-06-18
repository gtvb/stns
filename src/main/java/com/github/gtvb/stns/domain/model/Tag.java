package com.github.gtvb.stns.domain.model;

public class Tag {
    String uuid;
    String name;
    String createdAt;

    public Tag(String uuid, String name, String createdAt) {
        this.uuid = uuid;
        this.name = name;
        this.createdAt = createdAt;
    }
}
