package com.example.ideaboard.auth.repository;

import com.example.ideaboard.auth.model.ReleasePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReleasePlanRepository extends JpaRepository<ReleasePlan, Long> {
    List<ReleasePlan> findByProject_Id(Long projectId);
}
