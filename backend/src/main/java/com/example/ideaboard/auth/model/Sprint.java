package com.example.ideaboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;

    // Prevent recursion from: Sprint → Stories → Sprint → Stories ...
    @JsonIgnore
    @OneToMany(mappedBy = "sprint")
    private List<com.example.ideaboard.auth.model.UserStory> stories;
}
