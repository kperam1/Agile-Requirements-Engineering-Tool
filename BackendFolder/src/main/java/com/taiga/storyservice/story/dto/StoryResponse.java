package com.taiga.storyservice.story.dto;

public class StoryResponse {
    private Long id;
    private String title;
    private String description;
    private String acceptanceCriteria;
    private String assignee;
    private Integer storyPoints;
    private String priority;
    private String status;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAcceptanceCriteria() { return acceptanceCriteria; }
    public void setAcceptanceCriteria(String acceptanceCriteria) { this.acceptanceCriteria = acceptanceCriteria; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
