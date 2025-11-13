package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User story not found with id: " + id));
        return ResponseEntity.ok(story);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserStory> updateStory(@PathVariable Long id, @RequestBody UserStory storyDetails) {
        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User story not found with id: " + id));
        
        story.setTitle(storyDetails.getTitle());
        story.setDescription(storyDetails.getDescription());
        story.setAcceptanceCriteria(storyDetails.getAcceptanceCriteria());
        story.setAssignedTo(storyDetails.getAssignedTo());
        story.setPriority(storyDetails.getPriority());
        story.setStatus(storyDetails.getStatus());
        story.setStoryPoints(storyDetails.getStoryPoints());
        
        UserStory updatedStory = userStoryRepository.save(story);
        return ResponseEntity.ok(updatedStory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable Long id) {
        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User story not found with id: " + id));
        
        userStoryRepository.delete(story);
        return ResponseEntity.ok().body("User story deleted successfully");
    }
}
