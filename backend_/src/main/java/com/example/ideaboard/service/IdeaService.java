package com.example.ideaboard.service;
import com.example.ideaboard.model.Idea;
import com.example.ideaboard.model.IdeaStatus;
import com.example.ideaboard.repository.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
@Service
public class IdeaService {
    private final IdeaRepository ideaRepository;
    @Autowired
    public IdeaService(IdeaRepository ideaRepository) {
        this.ideaRepository = ideaRepository;
    }
    public List<Idea> getAllIdeas() {
        return ideaRepository.findAll();
    }
    public Optional<Idea> getIdeaById(Long id) {
        return ideaRepository.findById(id);
    }
    public Idea createIdea(Idea idea) {
        return ideaRepository.save(idea);
    }
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
    public void deleteIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        ideaRepository.delete(idea);
    }
    public List<Idea> getIdeasByCategory(String category) {
        return ideaRepository.findByCategory(category);
    }
    public List<Idea> getIdeasByStatus(String status) {
        return ideaRepository.findByStatus(status);
    }
    public List<Idea> searchIdeas(String keyword) {
        return ideaRepository.findByTitleContainingIgnoreCase(keyword);
    }
    @Transactional
    public Idea approveIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        idea.setStatus(IdeaStatus.APPROVED);
        return ideaRepository.save(idea);
    }
    @Transactional
    public Idea rejectIdea(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        idea.setStatus(IdeaStatus.REJECTED);
        return ideaRepository.save(idea);
    }
}
