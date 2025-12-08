package com.example.ideaboard.auth.service;

import com.example.ideaboard.auth.model.ReleasePlan;
import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.ReleasePlanRepository;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import com.example.ideaboard.model.Project;
import com.example.ideaboard.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReleasePlanService {

    private final ReleasePlanRepository releaseRepo;
    private final UserStoryRepository storyRepo;
    private final ProjectRepository projectRepo;

    public ReleasePlanService(
            ReleasePlanRepository releaseRepo,
            UserStoryRepository storyRepo,
            ProjectRepository projectRepo
    ) {
        this.releaseRepo = releaseRepo;
        this.storyRepo = storyRepo;
        this.projectRepo = projectRepo;
    }

    // GET all releases for a project
    public List<ReleasePlan> getReleasesForProject(Long projectId) {
        return releaseRepo.findByProject_Id(projectId);
    }

    // CREATE release plan
    public ReleasePlan createRelease(Long projectId, ReleasePlan rp) {
        Project p = projectRepo.findById(projectId).orElseThrow();
        rp.setProject(p);
        return releaseRepo.save(rp);
    }

    // ASSIGN story to release
    public void assignStory(Long releaseId, Long storyId) {
        ReleasePlan rp = releaseRepo.findById(releaseId).orElseThrow();
        UserStory story = storyRepo.findById(storyId).orElseThrow();

        story.setReleasePlan(rp);
        storyRepo.save(story);
    }

    // REMOVE story from release
    public void removeStory(Long releaseId, Long storyId) {
        UserStory story = storyRepo.findById(storyId).orElseThrow();
        story.setReleasePlan(null);
        storyRepo.save(story);
    }

    // DELETE release — detach stories first
    public void deleteRelease(Long id) {

        List<UserStory> stories = storyRepo.findByReleasePlan_Id(id);
        for (UserStory s : stories) {
            s.setReleasePlan(null);
            storyRepo.save(s);
        }

        releaseRepo.deleteById(id);
    }

public List<UserStory> getAvailableStories(Long projectId) {
    return storyRepo.findByProject_IdAndReleasePlanIsNull(projectId);
}

}
