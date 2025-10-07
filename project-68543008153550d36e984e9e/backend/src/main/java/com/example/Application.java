package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Main Spring Boot Application class for the modernized enterprise application.
 * 
 * This application serves as the entry point for a modern, scalable, and secure
 * enterprise system built on Spring Boot with MongoDB as the primary data store.
 * 
 * Key Features:
 * - User authentication and authorization with RBAC
 * - Organization and project management
 * - Task tracking and assignment
 * - Document management
 * - Performance monitoring and caching
 * - Comprehensive security framework
 * - RESTful API with OpenAPI documentation
 * 
 * Architecture:
 * - Clean architecture with separation of concerns
 * - Domain-driven design principles
 * - Microservices-ready structure
 * - Event-driven capabilities
 * 
 * @author Development Team
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.repository")
@EnableMongoAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "com.example.config")
@OpenAPIDefinition(
    info = @Info(
        title = "Enterprise Application API",
        version = "1.0.0",
        description = "Modern enterprise application with comprehensive user management, " +
                     "organization management, project tracking, and document management capabilities. " +
                     "Built with Spring Boot and MongoDB for scalability and performance.",
        contact = @Contact(
            name = "API Support Team",
            email = "support@example.com",
            url = "https://www.example.com/support"
        ),
        license = @License(
            name = "Proprietary",
            url = "https://www.example.com/license"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Development Server"
        ),
        @Server(
            url = "https://api.example.com",
            description = "Production Server"
        )
    }
)
@Slf4j
public class Application {

    /**
     * Main entry point for the Spring Boot application.
     * 
     * Initializes the Spring application context, configures the embedded server,
     * and starts the application. Sets default timezone to UTC for consistent
     * date/time handling across different environments.
     * 
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Set default timezone to UTC for consistent date/time handling
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
        
        log.info("Starting Enterprise Application...");
        log.info("Java Version: {}", System.getProperty("java.version"));
        log.info("Default Timezone: {}", TimeZone.getDefault().getID());
        
        try {
            SpringApplication.run(Application.class, args);
            log.info("Enterprise Application started successfully");
        } catch (Exception e) {
            log.error("Failed to start Enterprise Application", e);
            System.exit(1);
        }
    }

    /**
     * Configures the password encoder bean for secure password hashing.
     * 
     * Uses BCrypt hashing algorithm with a strength of 12 rounds for optimal
     * security and performance balance. BCrypt is resistant to rainbow table
     * attacks and includes built-in salt generation.
     * 
     * @return PasswordEncoder instance configured with BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("Initializing BCrypt password encoder with strength 12");
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     * 
     * Enables controlled access from web applications hosted on different domains.
     * In production, this should be configured to allow only specific trusted origins.
     * 
     * Configuration includes:
     * - Allowed origins (configurable per environment)
     * - Allowed HTTP methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
     * - Allowed headers (including Authorization for JWT tokens)
     * - Credentials support for authenticated requests
     * - Max age for preflight request caching
     * 
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                log.debug("Configuring CORS mappings");
                registry.addMapping("/api/**")
                    .allowedOriginPatterns("*") // In production, specify exact origins
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("Authorization", "X-Total-Count", "X-Page-Number", "X-Page-Size")
                    .allowCredentials(true)
                    .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}