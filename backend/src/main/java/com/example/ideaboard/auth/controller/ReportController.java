package com.example.ideaboard.controller;

import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import com.example.ideaboard.model.Sprint;
import com.example.ideaboard.auth.repository.SprintRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final SprintRepository sprintRepo;
    private final UserStoryRepository storyRepo;

    public ReportController(SprintRepository sprintRepo, UserStoryRepository storyRepo) {
        this.sprintRepo = sprintRepo;
        this.storyRepo = storyRepo;
    }

    @GetMapping("/burndown/{sprintId}")
    public Map<String, Object> burndown(@PathVariable Long sprintId) {

        Sprint sprint = sprintRepo.findById(sprintId).orElseThrow();

        List<UserStory> stories = storyRepo.findBySprint_Id(sprintId);

        int totalPoints = stories.stream()
                .mapToInt(UserStory::getStoryPoints)
                .sum();

        int completed = stories.stream()
                .filter(s -> "Done".equalsIgnoreCase(s.getStatus()))
                .mapToInt(UserStory::getStoryPoints)
                .sum();

        int remaining = totalPoints - completed;

        // Calculate ideal line
        Map<String, Integer> ideal = new LinkedHashMap<>();

        LocalDate start = sprint.getStartDate();
        LocalDate end = sprint.getEndDate();

        long days = start.datesUntil(end.plusDays(1)).count();
        double burnPerDay = (double) totalPoints / (days - 1);

        for (int i = 0; i < days; i++) {
            int idealRemaining = (int) Math.max(0, totalPoints - (burnPerDay * i));
            ideal.put(start.plusDays(i).toString(), idealRemaining);
        }

        Map<String, Object> out = new HashMap<>();
        out.put("totalPoints", totalPoints);
        out.put("completed", completed);
        out.put("remaining", remaining);
        out.put("ideal", ideal);

        return out;
    }
}
