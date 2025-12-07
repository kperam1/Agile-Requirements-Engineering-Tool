package com.example.ideaboard.repository;

import com.example.ideaboard.model.ReleasePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleasePlanRepository extends JpaRepository<ReleasePlan, Long> {
}
