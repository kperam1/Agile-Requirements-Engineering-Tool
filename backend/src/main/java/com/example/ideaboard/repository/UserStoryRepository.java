package com.example.ideaboard.auth.repository;

import com.example.ideaboard.auth.model.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    int countBySprintReadyTrue();
    long countByStatusIgnoreCase(String status);
    

}

