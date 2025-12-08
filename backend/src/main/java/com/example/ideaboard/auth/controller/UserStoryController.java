package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import com.example.ideaboard.model.Project;
import com.example.ideaboard.model.Sprint;
import com.example.ideaboard.repository.ProjectRepository;
import com.example.ideaboard.auth.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserStoryController {

    @Autowired
    private UserStoryRepository userStoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintRepository sprintRepository;

    @GetMapping("/projects/{projectId}/stories")
    public ResponseEntity<List<UserStory>> getStoriesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(userStoryRepository.findByProject_Id(projectId));
    }

    @PostMapping("/projects/{projectId}/stories")
    public ResponseEntity<UserStory> createStoryForProject(
            @PathVariable Long projectId,
            @RequestBody UserStory story
    ) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return ResponseEntity.notFound().build();

        story.setProject(project);

        if (story.getSprint() != null && story.getSprint().getId() != null) {
            Sprint sprint = sprintRepository.findById(story.getSprint().getId()).orElse(null);
            story.setSprint(sprint);
        } else {
            story.setSprint(null);
        }

        return ResponseEntity.ok(userStoryRepository.save(story));
    }

    @GetMapping("/userstories/{id}")
    public ResponseEntity<UserStory> getStoryById(@PathVariable Long id) {
        return userStoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/userstories/{id}")
    public ResponseEntity<UserStory> updateStory(
            @PathVariable Long id,
            @RequestBody UserStory incoming
    ) {
        return userStoryRepository.findById(id)
                .map(existing -> {
                    String oldStatus = existing.getStatus();
                    String newStatus = incoming.getStatus();

                    existing.setTitle(incoming.getTitle());
                    existing.setDescription(incoming.getDescription());
                    existing.setAcceptanceCriteria(incoming.getAcceptanceCriteria());
                    existing.setAssignedTo(incoming.getAssignedTo());
                    existing.setPriority(incoming.getPriority());
                    existing.setStatus(newStatus);
                    existing.setStoryPoints(incoming.getStoryPoints());
                    existing.setMvp(incoming.getMvp());
                    existing.setSprintReady(incoming.getSprintReady());

                    if ("Done".equalsIgnoreCase(newStatus) && !"Done".equalsIgnoreCase(oldStatus)) {
                        existing.setCompletedDate(LocalDate.now());
                    }

                    if (!"Done".equalsIgnoreCase(newStatus)) {
                        existing.setCompletedDate(null);
                    }

                    if (incoming.getProject() != null && incoming.getProject().getId() != null) {
                        Project p = projectRepository.findById(incoming.getProject().getId()).orElse(null);
                        existing.setProject(p);
                    }

                    if (incoming.getSprint() != null && incoming.getSprint().getId() != null) {
                        Sprint s = sprintRepository.findById(incoming.getSprint().getId()).orElse(null);
                        existing.setSprint(s);
                    } else {
                        existing.setSprint(null);
                    }

                    return ResponseEntity.ok(userStoryRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/userstories/{id}/assign-sprint/{sprintId}")
    public ResponseEntity<UserStory> assignStoryToSprint(
            @PathVariable Long id,
            @PathVariable Long sprintId
    ) {
        Sprint sprint = sprintRepository.findById(sprintId).orElse(null);
        if (sprint == null) return ResponseEntity.notFound().build();

        return userStoryRepository.findById(id)
                .map(story -> {
                    story.setSprint(sprint);
                    return ResponseEntity.ok(userStoryRepository.save(story));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sprints/{sprintId}/stories")
    public ResponseEntity<List<UserStory>> getStoriesBySprint(@PathVariable Long sprintId) {
        return ResponseEntity.ok(userStoryRepository.findBySprint_Id(sprintId));
    }

    @DeleteMapping("/userstories/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable Long id) {
        return userStoryRepository.findById(id)
                .map(story -> {
                    userStoryRepository.delete(story);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/userstories/project/{projectId}")
    public ResponseEntity<List<UserStory>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(userStoryRepository.findByProject_Id(projectId));
    }
}
