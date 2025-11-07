package com.taiga.storyservice.story;

import com.taiga.storyservice.story.dto.StoryRequest;
import com.taiga.storyservice.story.dto.StoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoryService {

    private final StoryRepository repo;

    public StoryService(StoryRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public StoryResponse create(StoryRequest req) {
        Story s = new Story();
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        s.setAcceptanceCriteria(req.getAcceptanceCriteria());
        s.setAssignee(req.getAssignee());
        s.setStoryPoints(req.getStoryPoints());
        s.setPriority(req.getPriority());
        s.setStatus(req.getStatus());
        s = repo.save(s);
        return toResponse(s);
    }

    @Transactional
    public StoryResponse updateAcceptanceCriteria(Long id, String acceptanceCriteria) {
        Story s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Story not found: " + id));
        s.setAcceptanceCriteria(acceptanceCriteria);
        s = repo.save(s);
        return toResponse(s);
    }

    @Transactional(readOnly = true)
    public StoryResponse get(Long id) {
        Story s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Story not found: " + id));
        return toResponse(s);
    }

    private StoryResponse toResponse(Story s) {
        StoryResponse resp = new StoryResponse();
        resp.setId(s.getId());
        resp.setTitle(s.getTitle());
        resp.setDescription(s.getDescription());
        resp.setAcceptanceCriteria(s.getAcceptanceCriteria());
        resp.setAssignee(s.getAssignee());
        resp.setStoryPoints(s.getStoryPoints());
        resp.setPriority(s.getPriority());
        resp.setStatus(s.getStatus());
        return resp;
    }
}
