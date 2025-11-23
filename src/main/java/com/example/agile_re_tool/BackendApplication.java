package com.example.agile_re_tool;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Entity
    @Table(name = "user_story")
    public static class UserStory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank
        @Size(max = 255)
        @Column(nullable = false, length = 255)
        private String title;

        @Lob
        @Column(name = "description")
        private String description;

        @Lob
        @Column(name = "acceptance_criteria")
        private String acceptanceCriteria;

        @Column(name = "assignee", length = 120)
        private String assignee;

        @Column(name = "estimate_type", length = 50)
        private String estimateType;

        @Column(name = "story_points")
        private Integer storyPoints;

        @Column(name = "size", length = 20)
        private String size;          

        @Column(name = "time_estimate", length = 120)
        private String timeEstimate;

        @Column(name = "priority", length = 50)
        private String priority;

        @Column(name = "status", length = 50)
        private String status;

        @Column(name = "created_at")
        private Instant createdAt = Instant.now();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getAcceptanceCriteria() { return acceptanceCriteria; }
        public void setAcceptanceCriteria(String acceptanceCriteria) { this.acceptanceCriteria = acceptanceCriteria; }

        public String getAssignee() { return assignee; }
        public void setAssignee(String assignee) { this.assignee = assignee; }

        public String getEstimateType() { return estimateType; }
        public void setEstimateType(String estimateType) { this.estimateType = estimateType; }

        public Integer getStoryPoints() { return storyPoints; }
        public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getTimeEstimate() { return timeEstimate; }
        public void setTimeEstimate(String timeEstimate) { this.timeEstimate = timeEstimate; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    public interface UserStoryRepository extends JpaRepository<UserStory, Long> {

    }

    @RestController
    @RequestMapping("/api/stories")
    @Validated
    public static class UserStoryController {
        private final UserStoryRepository repo;

        public UserStoryController(UserStoryRepository repo) {
            this.repo = repo;
        }

        @PostMapping
        public ResponseEntity<UserStory> create(@RequestBody @Validated UserStoryCreateDto dto) {
            UserStory s = new UserStory();
            s.setTitle(dto.getTitle());
            s.setDescription(dto.getDescription());
            s.setAcceptanceCriteria(dto.getAcceptanceCriteria());
            s.setAssignee(dto.getAssignee());
            s.setEstimateType(dto.getEstimateType());
            s.setStoryPoints(dto.getStoryPoints());
            s.setSize(dto.getSize());
            s.setTimeEstimate(dto.getTimeEstimate());
            s.setPriority(dto.getPriority());
            s.setStatus(dto.getStatus());
            s.setCreatedAt(Instant.now());
            UserStory saved = repo.save(s);
            return ResponseEntity.ok(saved);
        }

        @GetMapping("/{id}")
        public ResponseEntity<UserStory> getById(@PathVariable Long id) {
            Optional<UserStory> o = repo.findById(id);
            return o.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}")
        public ResponseEntity<UserStory> update(@PathVariable Long id, @RequestBody @Validated UserStoryCreateDto dto) {
            return repo.findById(id).map(existing -> {
                existing.setTitle(dto.getTitle());
                existing.setDescription(dto.getDescription());
                existing.setAcceptanceCriteria(dto.getAcceptanceCriteria());
                existing.setAssignee(dto.getAssignee());
                existing.setEstimateType(dto.getEstimateType());
                existing.setStoryPoints(dto.getStoryPoints());
                existing.setSize(dto.getSize());
                existing.setTimeEstimate(dto.getTimeEstimate());
                existing.setPriority(dto.getPriority());
                existing.setStatus(dto.getStatus());
                UserStory saved = repo.save(existing);
                return ResponseEntity.ok(saved);
            }).orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping
        public List<UserStory> listAll() {
            return repo.findAll();
        }
    }

    public static class UserStoryCreateDto {
        @NotBlank
        private String title;
        private String description;
        private String acceptanceCriteria;
        private String assignee;
        private String estimateType;
        private Integer storyPoints;
        private String size;
        private String timeEstimate;
        private String priority;
        private String status;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAcceptanceCriteria() { return acceptanceCriteria; }
        public void setAcceptanceCriteria(String acceptanceCriteria) { this.acceptanceCriteria = acceptanceCriteria; }
        public String getAssignee() { return assignee; }
        public void setAssignee(String assignee) { this.assignee = assignee; }
        public String getEstimateType() { return estimateType; }
        public void setEstimateType(String estimateType) { this.estimateType = estimateType; }
        public Integer getStoryPoints() { return storyPoints; }
        public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        public String getTimeEstimate() { return timeEstimate; }
        public void setTimeEstimate(String timeEstimate) { this.timeEstimate = timeEstimate; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
