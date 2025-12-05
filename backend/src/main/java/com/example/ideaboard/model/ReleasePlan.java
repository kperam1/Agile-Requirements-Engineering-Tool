package com.example.ideaboard.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "releases")
public class ReleasePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String version;

    @Column(length = 2000)
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private double progress;

    // comma-separated lists for simplicity
    @Column(length = 2000)
    private String sprints;

    @Column(length = 2000)
    private String team;

    private String status;

    @Column(length = 2000)
    private String lastUpdateNote;

    public ReleasePlan() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }

    public String getSprints() { return sprints; }
    public void setSprints(String sprints) { this.sprints = sprints; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLastUpdateNote() { return lastUpdateNote; }
    public void setLastUpdateNote(String lastUpdateNote) { this.lastUpdateNote = lastUpdateNote; }
}
