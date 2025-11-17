package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
