package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import lombok.extern.slf4j.Slf4j;

/**
 * Main Spring Boot Application class for the modernized enterprise application.
 * 
 * This application serves as the entry point for a modern, cloud-native Java application
 * that manages users, organizations, projects, tasks, and documents with MongoDB as the
 * primary data store.
 * 
 * Key Features:
 * - Modern Spring Boot architecture with clean separation of concerns
 * - MongoDB integration with auditing support
 * - Comprehensive security framework with RBAC
 * - Caching support for performance optimization
 * - Asynchronous processing capabilities
 * - Scheduled task execution
 * - Method-level security annotations
 * 
 * Architecture Highlights:
 * - Microservices-ready design
 * - RESTful API endpoints
 * - Event-driven capabilities
 * - Comprehensive monitoring and logging
 * - Production-ready error handling
 * 
 * @author Development Team
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.repository")
@EnableMongoAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@ConfigurationPropertiesScan(basePackages = "com.example.config")
@Slf4j
public class Application {

    /**
     * Main entry point for the Spring Boot application.
     * 
     * Initializes the Spring application context, configures all beans,
     * and starts the embedded web server.
     * 
     * The application startup process includes:
     * - Loading configuration properties
     * - Initializing MongoDB connections
     * - Setting up security filters
     * - Configuring caching mechanisms
     * - Starting scheduled tasks
     * - Initializing monitoring and metrics
     * 
     * @param args Command line arguments passed to the application.
     *             Supported arguments include:
     *             --spring.profiles.active: Specify active Spring profiles (dev, test, prod)
     *             --server.port: Override default server port
     *             --spring.data.mongodb.uri: Override MongoDB connection string
     */
    public static void main(String[] args) {
        try {
            log.info("Starting Application...");
            log.info("Java Version: {}", System.getProperty("java.version"));
            log.info("Operating System: {} {}", 
                    System.getProperty("os.name"), 
                    System.getProperty("os.version"));
            
            // Configure system properties for optimal performance
            configureSystemProperties();
            
            // Start the Spring Boot application
            SpringApplication application = new SpringApplication(Application.class);
            
            // Add custom initialization logic if needed
            application.addListeners(new ApplicationStartupListener());
            
            // Run the application
            var context = application.run(args);
            
            // Log successful startup
            String[] activeProfiles = context.getEnvironment().getActiveProfiles();
            String profiles = activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default";
            
            log.info("Application started successfully!");
            log.info("Active profiles: {}", profiles);
            log.info("Application is ready to accept requests");
            
            // Log important endpoints and configuration
            logApplicationInfo(context);
            
        } catch (Exception e) {
            log.error("Failed to start application", e);
            log.error("Error message: {}", e.getMessage());
            log.error("Root cause: {}", 
                    e.getCause() != null ? e.getCause().getMessage() : "Unknown");
            
            // Exit with error code
            System.exit(1);
        }
    }
    
    /**
     * Configures system properties for optimal application performance.
     * 
     * Sets JVM properties related to:
     * - Network timeouts
     * - DNS caching
     * - File encoding
     * - Security providers
     */
    private static void configureSystemProperties() {
        // Set default file encoding to UTF-8
        System.setProperty("file.encoding", "UTF-8");
        
        // Configure network timeouts
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
        
        // Configure DNS caching (important for cloud environments)
        System.setProperty("networkaddress.cache.ttl", "60");
        System.setProperty("networkaddress.cache.negative.ttl", "10");
        
        log.debug("System properties configured successfully");
    }
    
    /**
     * Logs important application information after successful startup.
     * 
     * @param context The Spring application context
     */
    private static void logApplicationInfo(org.springframework.context.ConfigurableApplicationContext context) {
        try {
            var environment = context.getEnvironment();
            
            String serverPort = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "/");
            String mongoUri = environment.getProperty("spring.data.mongodb.uri", "Not configured");
            
            // Mask sensitive information in MongoDB URI
            if (mongoUri.contains("@")) {
                mongoUri = mongoUri.replaceAll("://[^@]+@", "://***:***@");
            }
            
            log.info("=".repeat(80));
            log.info("Application Configuration:");
            log.info("  Server Port: {}", serverPort);
            log.info("  Context Path: {}", contextPath);
            log.info("  MongoDB URI: {}", mongoUri);
            log.info("  Base URL: http://localhost:{}{}", serverPort, contextPath);
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.warn("Could not log application info", e);
        }
    }
    
    /**
     * Custom application startup listener for additional initialization logic.
     */
    private static class ApplicationStartupListener implements org.springframework.boot.context.event.ApplicationReadyEvent {
        // This is a placeholder for custom startup logic
        // Actual implementation would be in a separate class
    }
}