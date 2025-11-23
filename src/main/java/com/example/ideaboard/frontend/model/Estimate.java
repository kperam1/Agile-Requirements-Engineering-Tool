package com.example.ideaboard.frontend.model;

public class Estimate {
    private Long id;
    private Integer points;
    private String estimatedBy;
    private String estimatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public String getEstimatedBy() { return estimatedBy; }
    public void setEstimatedBy(String estimatedBy) { this.estimatedBy = estimatedBy; }
    public String getEstimatedAt() { return estimatedAt; }
    public void setEstimatedAt(String estimatedAt) { this.estimatedAt = estimatedAt; }
}
