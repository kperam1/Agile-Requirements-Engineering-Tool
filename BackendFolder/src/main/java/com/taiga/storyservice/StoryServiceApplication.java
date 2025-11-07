package com.taiga.storyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.taiga.storyservice.story")
@EntityScan(basePackages = "com.taiga.storyservice.story")
public class StoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoryServiceApplication.class, args);
    }
}
