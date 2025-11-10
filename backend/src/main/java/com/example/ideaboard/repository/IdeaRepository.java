package com.example.ideaboard.repository;
import com.example.ideaboard.model.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    List<Idea> findByCategory(String category);
    List<Idea> findByStatus(String status);
    List<Idea> findByOwnerName(String ownerName);
    List<Idea> findByTitleContainingIgnoreCase(String keyword);
}
