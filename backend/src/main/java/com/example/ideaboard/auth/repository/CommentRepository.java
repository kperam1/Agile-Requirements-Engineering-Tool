package com.example.ideaboard.auth.repository;

import com.example.ideaboard.auth.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStoryIdOrderByCreatedAtAsc(Long storyId);
}
