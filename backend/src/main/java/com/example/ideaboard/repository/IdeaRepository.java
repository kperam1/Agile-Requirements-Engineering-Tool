package com.example.ideaboard.repository;

import com.example.ideaboard.model.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Idea entity.
 */
@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    /**
     * Find all ideas by category.
     */
    List<Idea> findByCategory(String category);

    /**
     * Find all ideas by status.
     */
    List<Idea> findByStatus(String status);

    /**
     * Find all ideas by owner name.
     */
    List<Idea> findByOwnerName(String ownerName);

    /**
     * Search ideas by title containing keyword (case-insensitive).
     */
    List<Idea> findByTitleContainingIgnoreCase(String keyword);
}
