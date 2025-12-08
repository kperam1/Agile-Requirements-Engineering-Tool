package com.example.ideaboard.auth.service;

import com.example.ideaboard.model.Sprint;
import com.example.ideaboard.auth.repository.SprintRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SprintService {

    private final SprintRepository repo;

    public SprintService(SprintRepository repo) {
        this.repo = repo;
    }

public List<Sprint> getByProject(long projectId) {
    return repo.findByProject_Id(projectId);
}


    public Sprint create(Sprint s) {
        return repo.save(s);
    }
}
