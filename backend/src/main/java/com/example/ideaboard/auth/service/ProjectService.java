package com.example.ideaboard.auth.service;

import com.example.ideaboard.model.Project;
import com.example.ideaboard.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public List<Project> getAll() {
        return repo.findAll();
    }

    public Project getOne(long id) {
        return repo.findById(id).orElse(null);
    }

    public Project create(Project p) {
        return repo.save(p);
    }

    public Project update(long id, Project updated) {
        return repo.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            return repo.save(existing);
        }).orElse(null);
    }

    public void delete(long id) {
        repo.deleteById(id);
    }
}
