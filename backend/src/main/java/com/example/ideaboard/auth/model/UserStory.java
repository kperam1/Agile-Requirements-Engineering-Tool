package com.example.ideaboard.auth.model;

import com.example.ideaboard.model.Project;
import com.example.ideaboard.model.Sprint;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_story")
public class UserStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String acceptanceCriteria;
    private String priority;
    private String assignedTo;
    private boolean mvp;
    private boolean sprintReady;
    private String status;
    private int storyPoints;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    @JsonBackReference("story-sprint")
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "release_plan_id")
    @JsonBackReference("release-story")
    private ReleasePlan releasePlan;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public boolean getMvp() {
        return mvp;
    }

    public void setMvp(boolean mvp) {
        this.mvp = mvp;
    }

    public boolean getSprintReady() {
        return sprintReady;
    }

    public void setSprintReady(boolean sprintReady) {
        this.sprintReady = sprintReady;
    }

    public boolean isSprintReady() {
        return sprintReady;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public ReleasePlan getReleasePlan() {
        return releasePlan;
    }

    public void setReleasePlan(ReleasePlan releasePlan) {
        this.releasePlan = releasePlan;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }
}
