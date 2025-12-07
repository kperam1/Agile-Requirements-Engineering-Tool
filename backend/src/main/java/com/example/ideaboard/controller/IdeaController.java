package com.example.ideaboard.controller;

import com.example.ideaboard.model.Idea;
import com.example.ideaboard.service.IdeaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas")
@CrossOrigin(origins = "*")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping
    public ResponseEntity<List<Idea>> getAllIdeas() {
        return ResponseEntity.ok(ideaService.getAllIdeas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idea> getIdeaById(@PathVariable Long id) {
        return ideaService.getIdeaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Idea> createIdea(
            @PathVariable Long projectId,
            @Valid @RequestBody Idea idea
    ) {
        Idea created = ideaService.createIdea(projectId, idea);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Idea> updateIdea(
            @PathVariable Long id,
            @Valid @RequestBody Idea ideaDetails
    ) {
        try {
            return ResponseEntity.ok(ideaService.updateIdea(id, ideaDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdea(@PathVariable Long id) {
        try {
            ideaService.deleteIdea(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Idea>> getIdeasByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ideaService.getIdeasByCategory(category));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Idea>> getIdeasByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ideaService.getIdeasByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Idea>> searchIdeas(@RequestParam String keyword) {
        return ResponseEntity.ok(ideaService.searchIdeas(keyword));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Idea> approveIdea(@PathVariable Long id) {
        return ResponseEntity.ok(ideaService.approveIdea(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Idea> rejectIdea(@PathVariable Long id) {
        return ResponseEntity.ok(ideaService.rejectIdea(id));
    }
}
