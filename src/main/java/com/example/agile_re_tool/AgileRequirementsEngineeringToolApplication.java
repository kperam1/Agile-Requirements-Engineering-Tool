package com.example.agile_re_tool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.agile_re_tool", "com.agile.requirements"})
@EnableJpaRepositories(basePackages = {"com.agile.requirements.repository"})
@EntityScan(basePackages = {"com.agile.requirements.model"})
public class AgileRequirementsEngineeringToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgileRequirementsEngineeringToolApplication.class, args);
    }
}
