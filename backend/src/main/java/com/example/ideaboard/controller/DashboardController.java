package com.example.ideaboard.controller;

import com.example.ideaboard.repository.IdeaRepository;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import com.example.ideaboard.auth.model.UserStory;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
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
    public Map<String, Object> getDashboard() {

        int totalIdeas = ideaRepository.findAll().size();
        int userStories = userStoryRepository.findAll().size();

        int sprintReady = userStoryRepository.countBySprintReadyTrue();

        List<UserStory> allStories = userStoryRepository.findAll();

        int teamVelocity = allStories.stream()
                .filter(s -> s.getStatus() != null && s.getStatus().equalsIgnoreCase("Done"))
                .mapToInt(s -> s.getStoryPoints() == null ? 0 : s.getStoryPoints())
                .sum();

        long todoCount = allStories.stream()
                .filter(s -> "To Do".equalsIgnoreCase(s.getStatus()))
                .count();

        long inProgressCount = allStories.stream()
                .filter(s -> "In Progress".equalsIgnoreCase(s.getStatus()))
                .count();

        long testingCount = allStories.stream()
                .filter(s -> "Testing".equalsIgnoreCase(s.getStatus()))
                .count();

        long doneCount = allStories.stream()
                .filter(s -> "Done".equalsIgnoreCase(s.getStatus()))
                .count();

        int totalPoints = allStories.stream()
                .filter(s -> s.getStoryPoints() != null)
                .mapToInt(UserStory::getStoryPoints)
                .sum();

        int donePoints = allStories.stream()
                .filter(s -> s.getStoryPoints() != null
                        && s.getStatus() != null
                        && s.getStatus().equalsIgnoreCase("Done"))
                .mapToInt(UserStory::getStoryPoints)
                .sum();

        double progress = 0.0;
        if (totalPoints > 0) {
            progress = (double) donePoints / totalPoints;
        }

        Map<String, Object> out = new HashMap<>();
        out.put("totalIdeas", totalIdeas);
        out.put("userStories", userStories);
        out.put("sprintReady", sprintReady);
        out.put("teamVelocity", teamVelocity);

        Map<String, Object> sprint = new HashMap<>();
        sprint.put("name", "Active Sprint");
        sprint.put("todoCount", todoCount);
        sprint.put("inProgressCount", inProgressCount);
        sprint.put("testingCount", testingCount);
        sprint.put("doneCount", doneCount);
        sprint.put("totalPoints", totalPoints);
        sprint.put("donePoints", donePoints);
        sprint.put("progress", progress);

        out.put("sprint", sprint);

        return out;
    }
}
