package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/userstories")
@CrossOrigin(origins = "*")
public class UserStoryController {

    @Autowired
    private UserStoryRepository userStoryRepository;

    @PostMapping
    public UserStory createStory(@RequestBody UserStory story) {
        return userStoryRepository.save(story);
    }

    @GetMapping
    public List<UserStory> getAllStories() {
        return userStoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStory> getStoryById(@PathVariable Long id) {
        Optional<UserStory> opt = userStoryRepository.findById(id);
        return opt.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserStory> updateStory(
            @PathVariable Long id,
            @RequestBody UserStory incoming) {

        return userStoryRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(incoming.getTitle());
                    existing.setDescription(incoming.getDescription());
                    existing.setAcceptanceCriteria(incoming.getAcceptanceCriteria());
                    existing.setAssignedTo(incoming.getAssignedTo());
                    existing.setPriority(incoming.getPriority());
                    existing.setStatus(incoming.getStatus());
                    existing.setStoryPoints(incoming.getStoryPoints());
                    existing.setMvp(incoming.getMvp());
                    existing.setSprintReady(incoming.getSprintReady());
                    UserStory saved = userStoryRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
    Optional<UserStory> existing = userStoryRepository.findById(id);
    if (existing.isPresent()) {
        userStoryRepository.delete(existing.get());
        return ResponseEntity.noContent().build();
    } else {
        return ResponseEntity.notFound().build();
    }
}

}
