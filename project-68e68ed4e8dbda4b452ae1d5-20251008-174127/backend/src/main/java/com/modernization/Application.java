package com.modernization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

/**
 * Main application class for the modernized system.
 * This application replaces a legacy JSF system with a modern Spring Boot architecture.
 * It provides RESTful APIs for user management, project tracking, task management,
 * document handling, and notification services.
 * 
 * @author Modernization Team
 */
@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
@EnableAsync
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "Modernized Business Management API",
        version = "1.0.0",
        description = "API for the modernized business management system replacing legacy JSF application",
        contact = @Contact(
            name = "Modernization Team",
            email = "modernization@company.com"
        ),
        license = @License(
            name = "Company License",
            url = "https://company.com/license"
        )
    )
)
public class Application {

    /**
     * Main method that serves as the entry point for the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}