package com.example.agile_re_tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.agile_re_tool",
                "com.example.ideaboard"
        }
)
public class AgileRequirementsEngineeringToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgileRequirementsEngineeringToolApplication.class, args);
    }
}
