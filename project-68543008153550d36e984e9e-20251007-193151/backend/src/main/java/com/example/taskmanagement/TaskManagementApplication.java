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
 * This application provides a modern, microservice-based solution for task and project management
 * with features including user authentication, organization management, project tracking,
 * task assignment, and document management.
 * 
 * @author Task Management Team
 * @version 1.0
 */
@SpringBootApplication
@EnableMongoAuditing // Enables auditing for MongoDB entities (createdAt, lastModifiedAt, etc.)
@EnableAsync // Enables asynchronous method execution
@EnableScheduling // Enables scheduled tasks
public class TaskManagementApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementApplication.class);

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        try {
            logger.info("Starting Task Management Application...");
            SpringApplication.run(TaskManagementApplication.class, args);
            logger.info("Task Management Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start Task Management Application", e);
            throw e; // Re-throw to allow Spring Boot to handle the exception
        }
    }
}