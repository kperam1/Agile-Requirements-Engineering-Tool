package com.example.ideaboard.service;

import com.example.ideaboard.model.Idea;
import com.example.ideaboard.model.IdeaStatus;
import com.example.ideaboard.model.Project;
import com.example.ideaboard.repository.IdeaRepository;
import com.example.ideaboard.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<Idea> getAllIdeas() {
        return ideaRepository.findAll();
    }

    public Optional<Idea> getIdeaById(Long id) {
        return ideaRepository.findById(id);
    }

    // ⭐ FIXED: CREATE IDEA & SET PROJECT
    public Idea createIdea(Long projectId, Idea idea) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));

        idea.setProject(p);
        return ideaRepository.save(idea);
    }

    // ⭐ FIXED UPDATE IDEA
    public Idea updateIdea(Long id, Idea changes) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found: " + id));

        idea.setTitle(changes.getTitle());
        idea.setCategory(changes.getCategory());
        idea.setDescription(changes.getDescription());
        idea.setStatus(changes.getStatus());
        idea.setOwnerName(changes.getOwnerName());
        idea.setPrimaryActor(changes.getPrimaryActor());

        return ideaRepository.save(idea);
    }

    public void deleteIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found: " + id));
        ideaRepository.delete(idea);
    }

    public List<Idea> getIdeasByProject(Long projectId) {
    return ideaRepository.findByProjectId(projectId);
}


    public List<Idea> getIdeasByCategory(String category) {
        return ideaRepository.findByCategory(category);
    }

public List<Idea> getIdeasByStatus(String status) {
    IdeaStatus s = IdeaStatus.valueOf(status.toUpperCase());
    return ideaRepository.findByStatus(s);
}


    public List<Idea> searchIdeas(String keyword) {
        return ideaRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Idea approveIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found: " + id));

        idea.setStatus(IdeaStatus.APPROVED);
        return ideaRepository.save(idea);
    }

    public Idea rejectIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found: " + id));

        idea.setStatus(IdeaStatus.REJECTED);
        return ideaRepository.save(idea);
    }
}
