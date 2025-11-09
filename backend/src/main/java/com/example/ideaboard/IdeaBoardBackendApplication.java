package com.example.ideaboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class IdeaBoardBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdeaBoardBackendApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("IdeaBoard Backend API is running!");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}
