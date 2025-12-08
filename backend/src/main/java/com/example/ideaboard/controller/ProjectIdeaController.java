package com.example.ideaboard.controller;

import com.example.ideaboard.model.Idea;
import com.example.ideaboard.model.Project;
import com.example.ideaboard.service.IdeaService;
import com.example.ideaboard.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectIdeaController {

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private ProjectRepository projectRepository;

    // GET all ideas for a project
    @GetMapping("/{projectId}/ideas")
    public ResponseEntity<List<Idea>> getIdeasByProject(@PathVariable Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        List<Idea> ideas = ideaService.getIdeasByProject(projectId);
        return ResponseEntity.ok(ideas);
    }

    // CREATE idea under a project
    @PostMapping("/{projectId}/ideas")
    public ResponseEntity<Idea> createIdeaUnderProject(
            @PathVariable Long projectId,
            @RequestBody Idea idea
    ) {
        if (!projectRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        Idea created = ideaService.createIdea(projectId, idea);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
