package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.ReleasePlan;
import com.example.ideaboard.auth.model.UserStory;  
import com.example.ideaboard.auth.service.ReleasePlanService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/releases")
@CrossOrigin(origins = "*")
public class ReleasePlanController {

    private final ReleasePlanService service;

    public ReleasePlanController(ReleasePlanService service) {
        this.service = service;
    }

    @GetMapping("/{projectId}")
    public List<ReleasePlan> getForProject(@PathVariable Long projectId) {
        return service.getReleasesForProject(projectId);
    }

    // ⭐ FIXED: Explicitly tell Spring this endpoint consumes JSON
    @PostMapping(
            value = "/{projectId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ReleasePlan create(
            @PathVariable Long projectId,
            @RequestBody ReleasePlan rp
    ) {
        return service.createRelease(projectId, rp);
    }

    @PostMapping("/{releaseId}/assign/{storyId}")
    public void assign(@PathVariable Long releaseId, @PathVariable Long storyId) {
        service.assignStory(releaseId, storyId);
    }

    @PostMapping("/{releaseId}/remove/{storyId}")
    public void remove(@PathVariable Long releaseId, @PathVariable Long storyId) {
        service.removeStory(releaseId, storyId);
    }

    @DeleteMapping("/{releaseId}")
    public void delete(@PathVariable Long releaseId) {
        service.deleteRelease(releaseId);
    }

@GetMapping("/available/{projectId}")
public List<UserStory> getAvailableStories(@PathVariable Long projectId) {
    return service.getAvailableStories(projectId);
}


}
