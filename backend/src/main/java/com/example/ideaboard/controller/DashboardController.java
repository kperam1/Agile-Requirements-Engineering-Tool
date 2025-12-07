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

    @GetMapping("/{projectId}")
    public Map<String, Object> getDashboardForProject(@PathVariable Long projectId) {

        int totalIdeas = ideaRepository.countByProject_Id(projectId);

        List<UserStory> projectStories = userStoryRepository.findByProject_Id(projectId);
        int userStories = projectStories.size();

        int sprintReady = (int) projectStories.stream()
                .filter(s -> Boolean.TRUE.equals(s.isSprintReady()))
                .count();

        int totalPoints = projectStories.stream()
                .filter(s -> s.getStoryPoints() != null)
                .mapToInt(UserStory::getStoryPoints)
                .sum();

        int donePoints = projectStories.stream()
                .filter(s -> s.getStoryPoints() != null &&
                             "Done".equalsIgnoreCase(s.getStatus()))
                .mapToInt(UserStory::getStoryPoints)
                .sum();

        long todoCount = projectStories.stream().filter(s -> "To Do".equalsIgnoreCase(s.getStatus())).count();
        long inProgressCount = projectStories.stream().filter(s -> "In Progress".equalsIgnoreCase(s.getStatus())).count();
        long testingCount = projectStories.stream().filter(s -> "Testing".equalsIgnoreCase(s.getStatus())).count();
        long doneCount = projectStories.stream().filter(s -> "Done".equalsIgnoreCase(s.getStatus())).count();

        double progress = 0.0;
        if (totalPoints > 0) {
            progress = (double) donePoints / totalPoints;
        }

        Map<String, Object> out = new HashMap<>();
        out.put("totalIdeas", totalIdeas);
        out.put("userStories", userStories);
        out.put("sprintReady", sprintReady);
        out.put("teamVelocity", donePoints);

        Map<String, Object> sprint = new HashMap<>();
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
