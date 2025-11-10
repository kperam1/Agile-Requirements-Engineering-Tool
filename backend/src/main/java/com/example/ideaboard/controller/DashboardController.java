package com.example.ideaboard.controller;

import com.example.ideaboard.repository.IdeaRepository;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserStoryRepository userStoryRepository;

    @GetMapping
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIdeas", ideaRepository.count());
        stats.put("userStories", userStoryRepository.count());
        stats.put("sprintReady", 15);      // temporary static placeholder
        stats.put("teamVelocity", 32);     // temporary static placeholder
        return stats;
    }
}
