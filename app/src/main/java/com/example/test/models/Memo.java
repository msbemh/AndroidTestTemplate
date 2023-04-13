package com.example.test.models;

import java.io.Serializable;

public class Memo implements Serializable {
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private String title;
    private String content;

    public Memo(String uuid, String title, String content) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
