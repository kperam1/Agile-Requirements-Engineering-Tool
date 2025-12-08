package com.example.ideaboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // Prevent infinite recursion when a Project has Sprints or UserStories
    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private java.util.List<com.example.ideaboard.auth.model.UserStory> stories;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private java.util.List<Sprint> sprints;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private java.util.List<com.example.ideaboard.auth.model.ReleasePlan> releases;
}
