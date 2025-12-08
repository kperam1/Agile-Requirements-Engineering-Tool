package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import com.example.ideaboard.auth.repository.SprintRepository;
import com.example.ideaboard.model.Sprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BurndownController {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private UserStoryRepository userStoryRepository;

    @GetMapping("/sprints/{sprintId}/burndown")
    public ResponseEntity<Map<LocalDate, Integer>> getBurndown(@PathVariable Long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId).orElse(null);
        if (sprint == null) return ResponseEntity.notFound().build();

        LocalDate start = sprint.getStartDate();
        LocalDate end = sprint.getEndDate();
        if (start == null || end == null || end.isBefore(start)) return ResponseEntity.badRequest().build();

        List<UserStory> stories = userStoryRepository.findBySprint_Id(sprintId);

        int totalPoints = stories.stream().mapToInt(UserStory::getStoryPoints).sum();

        Map<LocalDate, Integer> remainingByDate = new LinkedHashMap<>();
        LocalDate d = start;

        while (!d.isAfter(end)) {
            final LocalDate day = d;

            int completedUpToDate = stories.stream()
                    .filter(s -> s.getCompletedDate() != null && !s.getCompletedDate().isAfter(day))
                    .mapToInt(UserStory::getStoryPoints)
                    .sum();

            int remaining = totalPoints - completedUpToDate;
            if (remaining < 0) remaining = 0;

            remainingByDate.put(day, remaining);

            d = d.plusDays(1);
        }

        return ResponseEntity.ok(remainingByDate);
    }
}
