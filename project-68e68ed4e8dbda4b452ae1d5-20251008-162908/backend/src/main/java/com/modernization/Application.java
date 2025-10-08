package com.modernization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;

/**
 * Main application class for the modernized system.
 * This application replaces the legacy JSF system with a modern Spring Boot architecture.
 * It provides RESTful APIs for frontend applications and integrates with MongoDB for data storage.
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
@OpenAPIDefinition(
    info = @Info(
        title = "Modernization API",
        version = "1.0.0",
        description = "API documentation for the modernized system replacing legacy JSF application",
        contact = @Contact(
            name = "Modernization Team",
            email = "modernization@example.com"
        )
    )
)
public class Application {

    /**
     * Main method to bootstrap the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    /**
     * Configures CORS for the application to allow cross-origin requests.
     * This is particularly important for separating frontend and backend concerns
     * in the modernized architecture.
     * 
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000") // Development frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}