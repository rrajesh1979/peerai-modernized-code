package com.example.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the Migration Application.
 * This application is designed to facilitate the migration from a legacy JSP application
 * to a modern Spring Boot framework with MongoDB as the database.
 */
@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
@OpenAPIDefinition(
    info = @Info(
        title = "Migration Application API",
        version = "1.0",
        description = "API for the JSP to Modern Framework Migration Application",
        contact = @Contact(
            name = "Development Team",
            email = "dev@example.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    )
)
public class MigrationApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(MigrationApplication.class);

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting Migration Application");
        SpringApplication.run(MigrationApplication.class, args);
        logger.info("Migration Application started successfully");
    }

    /**
     * Configure CORS for the application.
     * This allows cross-origin requests from specified origins.
     * 
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000", "https://example.com")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}