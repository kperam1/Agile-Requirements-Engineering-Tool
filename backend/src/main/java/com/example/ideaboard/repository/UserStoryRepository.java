package com.example.ideaboard.auth.repository;

import com.example.ideaboard.auth.model.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {

    int countBySprintReadyTrue();

    long countByStatusIgnoreCase(String status);

    // Correct Spring Data naming for @ManyToOne project
    List<UserStory> findByProject_Id(Long projectId);

    // Optional alias — helps avoid errors in case controller uses it
    List<UserStory> findByProjectId(Long projectId);

    List<UserStory> findBySprint_Id(Long sprintId);
}
