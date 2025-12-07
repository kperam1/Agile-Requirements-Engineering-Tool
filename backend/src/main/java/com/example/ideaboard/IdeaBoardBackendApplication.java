package com.example.ideaboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class IdeaBoardBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdeaBoardBackendApplication.class, args);
    }
}
