package com.example.ideaboard.config;

import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUserStories(UserStoryRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            repo.save(story("Setup project skeleton", "Initialize modules, CI and base configs", "To Do", "Medium", 3, "Alex Perez"));
            repo.save(story("Implement login flow", "Create login endpoint and UI wiring", "In Progress", "High", 5, "Sarah Chen"));
            repo.save(story("User story editor", "Add edit form with validation and persistence", "Testing", "High", 8, "David Kim"));
            repo.save(story("Backlog listing", "List and filter user stories by status", "Done", "Medium", 3, "Rachel Green"));
            repo.save(story("Sprint board UI", "Three-column board with grouping by status", "To Do", "Low", 2, "Mark Johnson"));
            repo.save(story("Comments CRUD", "Add, edit, delete comments on stories", "In Progress", "High", 5, "Sophie Turner"));
            repo.save(story("Reporting stub", "Add basic reports page routing", "Testing", "Low", 2, "James Miller"));
            repo.save(story("Polish styles", "Tweak colors and spacing across views", "Done", "Low", 1, "Jessica Adams"));
        };
    }

    private static UserStory story(String title, String desc, String status, String priority, int sp, String assignedTo) {
        UserStory s = new UserStory();
        s.setTitle(title);
        s.setDescription(desc);
        s.setStatus(status);
        s.setPriority(priority);
        s.setStoryPoints(sp);
        s.setAssignedTo(assignedTo);
        return s;
    }
}
