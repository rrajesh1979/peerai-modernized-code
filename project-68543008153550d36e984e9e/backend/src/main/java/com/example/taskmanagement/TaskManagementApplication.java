package com.example.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the Task Management System.
 * This application provides a modern, microservice-based solution for
 * managing tasks, projects, and organizations with robust security
 * and performance optimizations.
 * 
 * @author Task Management Team
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
@EnableScheduling
public class TaskManagementApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementApplication.class);
    
    /**
     * Main method that serves as the entry point for the application.
     * Initializes the Spring Boot application context.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        try {
            logger.info("Starting Task Management Application");
            SpringApplication.run(TaskManagementApplication.class, args);
            logger.info("Task Management Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start Task Management Application", e);
            System.exit(1);
        }
    }
}