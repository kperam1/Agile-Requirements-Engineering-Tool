package com.example.agile_re_tool.model;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long id;
    private Long userStoryId;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(Long userStoryId) {
        this.userStoryId = userStoryId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
