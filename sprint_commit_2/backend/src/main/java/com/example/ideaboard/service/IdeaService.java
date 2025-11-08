package com.example.ideaboard.service;

import com.example.ideaboard.model.Idea;
import com.example.ideaboard.repository.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Ideas.
 */
@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;

    @Autowired
    public IdeaService(IdeaRepository ideaRepository) {
        this.ideaRepository = ideaRepository;
    }

    /**
     * Get all ideas.
     */
    public List<Idea> getAllIdeas() {
        return ideaRepository.findAll();
    }

    /**
     * Get idea by ID.
     */
    public Optional<Idea> getIdeaById(Long id) {
        return ideaRepository.findById(id);
    }

    /**
     * Create a new idea.
     */
    public Idea createIdea(Idea idea) {
        return ideaRepository.save(idea);
    }

    /**
     * Update an existing idea.
     */
    public Idea updateIdea(Long id, Idea ideaDetails) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));

        idea.setTitle(ideaDetails.getTitle());
        idea.setCategory(ideaDetails.getCategory());
        idea.setDescription(ideaDetails.getDescription());
        idea.setStatus(ideaDetails.getStatus());
        idea.setOwnerName(ideaDetails.getOwnerName());

        return ideaRepository.save(idea);
    }

    /**
     * Delete an idea.
     */
    public void deleteIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        ideaRepository.delete(idea);
    }

    /**
     * Get ideas by category.
     */
    public List<Idea> getIdeasByCategory(String category) {
        return ideaRepository.findByCategory(category);
    }

    /**
     * Get ideas by status.
     */
    public List<Idea> getIdeasByStatus(String status) {
        return ideaRepository.findByStatus(status);
    }

    /**
     * Search ideas by title.
     */
    public List<Idea> searchIdeas(String keyword) {
        return ideaRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
