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
    private final IdeaService ideaService;
    @Autowired
    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }
    @GetMapping
    public ResponseEntity<List<Idea>> getAllIdeas() {
        List<Idea> ideas = ideaService.getAllIdeas();
        return ResponseEntity.ok(ideas);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Idea> getIdeaById(@PathVariable Long id) {
        return ideaService.getIdeaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Idea> createIdea(@Valid @RequestBody Idea idea) {
        Idea createdIdea = ideaService.createIdea(idea);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIdea);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Idea> updateIdea(@PathVariable Long id, @Valid @RequestBody Idea ideaDetails) {
        try {
            Idea updatedIdea = ideaService.updateIdea(id, ideaDetails);
            return ResponseEntity.ok(updatedIdea);
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
        List<Idea> ideas = ideaService.getIdeasByCategory(category);
        return ResponseEntity.ok(ideas);
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Idea>> getIdeasByStatus(@PathVariable String status) {
        List<Idea> ideas = ideaService.getIdeasByStatus(status);
        return ResponseEntity.ok(ideas);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Idea>> searchIdeas(@RequestParam String keyword) {
        List<Idea> ideas = ideaService.searchIdeas(keyword);
        return ResponseEntity.ok(ideas);
    }
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Idea> approveIdea(@PathVariable Long id) {
        try {
            Idea updatedIdea = ideaService.approveIdea(id);
            return ResponseEntity.ok(updatedIdea);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Idea> rejectIdea(@PathVariable Long id) {
        try {
            Idea updatedIdea = ideaService.rejectIdea(id);
            return ResponseEntity.ok(updatedIdea);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
