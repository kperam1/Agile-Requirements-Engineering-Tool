package com.example.ideaboard.controller;

import com.example.ideaboard.model.Idea;
import com.example.ideaboard.service.IdeaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Idea API endpoints.
 */
@RestController
@RequestMapping("/api/ideas")
@CrossOrigin(origins = "*")
public class IdeaController {

    private final IdeaService ideaService;

    @Autowired
    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    /**
     * GET /api/ideas - Get all ideas
     */
    @GetMapping
    public ResponseEntity<List<Idea>> getAllIdeas() {
        List<Idea> ideas = ideaService.getAllIdeas();
        return ResponseEntity.ok(ideas);
    }

    /**
     * GET /api/ideas/{id} - Get idea by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Idea> getIdeaById(@PathVariable Long id) {
        return ideaService.getIdeaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/ideas - Create a new idea
     */
    @PostMapping
    public ResponseEntity<Idea> createIdea(@Valid @RequestBody Idea idea) {
        Idea createdIdea = ideaService.createIdea(idea);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIdea);
    }

    /**
     * PUT /api/ideas/{id} - Update an existing idea
     */
    @PutMapping("/{id}")
    public ResponseEntity<Idea> updateIdea(@PathVariable Long id, @Valid @RequestBody Idea ideaDetails) {
        try {
            Idea updatedIdea = ideaService.updateIdea(id, ideaDetails);
            return ResponseEntity.ok(updatedIdea);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/ideas/{id} - Delete an idea
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdea(@PathVariable Long id) {
        try {
            ideaService.deleteIdea(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/ideas/category/{category} - Get ideas by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Idea>> getIdeasByCategory(@PathVariable String category) {
        List<Idea> ideas = ideaService.getIdeasByCategory(category);
        return ResponseEntity.ok(ideas);
    }

    /**
     * GET /api/ideas/status/{status} - Get ideas by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Idea>> getIdeasByStatus(@PathVariable String status) {
        List<Idea> ideas = ideaService.getIdeasByStatus(status);
        return ResponseEntity.ok(ideas);
    }

    /**
     * GET /api/ideas/search?keyword={keyword} - Search ideas by title
     */
    @GetMapping("/search")
    public ResponseEntity<List<Idea>> searchIdeas(@RequestParam String keyword) {
        List<Idea> ideas = ideaService.searchIdeas(keyword);
        return ResponseEntity.ok(ideas);
    }
}
