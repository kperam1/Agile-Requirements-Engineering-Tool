package com.example.ideaboard.auth.controller;

import com.example.ideaboard.model.Sprint;
import com.example.ideaboard.auth.service.SprintService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
@CrossOrigin(origins = "*")
public class SprintController {

    private final SprintService service;

    public SprintController(SprintService service) {
        this.service = service;
    }

    @GetMapping("/{projectId}")
    public List<Sprint> getByProject(@PathVariable long projectId) {
        return service.getByProject(projectId);
    }

    @PostMapping
    public Sprint create(@RequestBody Sprint sprint) {
        return service.create(sprint);
    }
}
