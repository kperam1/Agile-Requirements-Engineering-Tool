package com.taiga.storyservice.story;

import jakarta.persistence.*;

@Entity
@Table(name = "stories")
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String description;

    @Lob
    @Column(name = "acceptance_criteria")
    private String acceptanceCriteria;

    @Column(length = 100)
    private String assignee;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(length = 20)
    private String priority; // High | Medium | Low

    @Column(length = 20)
    private String status;   // To Do | In Progress | New | Closed

    // --- Getters & Setters ---
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
