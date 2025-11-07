package com.taiga.storyservice.story.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StoryRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    @NotBlank
    private String acceptanceCriteria;

    private String assignee;       // vtiruma3, Sakshi Agarwal, keerthana, mounika
    private Integer storyPoints;   // spinner value
    private String priority;       // High | Medium | Low
    private String status;         // To Do | In Progress | New | Closed

    // getters/setters
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
