package com.example.ideaboard.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class IdeaDto {

    private Long id;
    private String title;
    private String category;
    private String description;
    private IdeaStatus status;

    @JsonProperty("primaryActor")
    @JsonAlias("ownerName")
    private String primaryActor;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ⭐ NEW: store project ID inside DTO
    private Long projectId;

    public IdeaDto() {}

    @JsonProperty("project")
    public void unpackProject(Object projectObj) {
        // backend sends: { "project": { "id": 1, ... } }
        try {
            if (projectObj instanceof java.util.Map<?, ?> projectMap) {
                Object idObj = projectMap.get("id");
                if (idObj instanceof Number num) {
                    this.projectId = num.longValue();
                }
            }
        } catch (Exception ignored) {}
    }

    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public IdeaDto(Long id, String title, String category, String description,
                   IdeaStatus status, String primaryActor,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.status = status;
        this.primaryActor = primaryActor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public IdeaStatus getStatus() { return status; }
    public void setStatus(IdeaStatus status) { this.status = status; }

    public String getPrimaryActor() { return primaryActor; }
    public void setPrimaryActor(String primaryActor) { this.primaryActor = primaryActor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "IdeaDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", projectId=" + projectId +
                '}';
    }
}
