package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.Comment;
import com.example.ideaboard.auth.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentRepository repo;

    @GetMapping("/userstories/{storyId}/comments")
    public List<Comment> getComments(@PathVariable Long storyId) {
        return repo.findByStoryIdOrderByCreatedAtAsc(storyId);
    }

    @PostMapping("/userstories/{storyId}/comments")
    public Comment addComment(@PathVariable Long storyId, @RequestBody Comment incoming) {
        incoming.setStoryId(storyId);
        return repo.save(incoming);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment incoming) {
        Optional<Comment> opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Comment c = opt.get();
        c.setBody(incoming.getBody());
        repo.save(c);

        return ResponseEntity.ok(c);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        return repo.findById(id)
                .map(c -> {
                    repo.delete(c);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(404).<Void>build());
    }
}
