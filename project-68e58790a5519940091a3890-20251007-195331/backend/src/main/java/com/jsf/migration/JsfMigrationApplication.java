package com.jsf.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the JSF Migration Application.
 * 
 * This application provides a framework for migrating legacy JSF applications
 * to modern web frameworks while maintaining business continuity.
 * 
 * Key features include:
 * - Migration architecture and patterns
 * - Modern UI component library
 * - Automated migration testing
 * - Migration analytics and reporting
 * - Developer enablement tools
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
@EnableScheduling
public class JsfMigrationApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(JsfMigrationApplication.class);

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        logger.info("Starting JSF Migration Application");
        try {
            SpringApplication.run(JsfMigrationApplication.class, args);
            logger.info("JSF Migration Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start JSF Migration Application", e);
            System.exit(1);
        }
    }
}