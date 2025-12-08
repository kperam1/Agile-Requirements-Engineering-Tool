package com.example.ideaboard.repository;

import com.example.ideaboard.model.Idea;
import com.example.ideaboard.model.IdeaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findByCategory(String category);

    List<Idea> findByStatus(IdeaStatus status);

    List<Idea> findByOwnerName(String ownerName);

    List<Idea> findByTitleContainingIgnoreCase(String keyword);


    // ⭐ KEEP your existing method
    List<Idea> findByProject_Id(Long projectId);

    int countByProject_Id(Long projectId);


    // ⭐ ADD THESE ALIASES (Dashboard + IdeaService expect these)
    List<Idea> findByProjectId(Long projectId);

    int countByProjectId(Long projectId);
}
