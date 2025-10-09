package com.example.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Task Management System.
 * 
 * This application provides functionality for managing organizations, projects,
 * tasks, and documents with a focus on modern architecture principles.
 * 
 * Features include:
 * - User authentication and authorization
 * - Organization and project management
 * - Task tracking and assignment
 * - Document management
 * - Performance monitoring
 */
@SpringBootApplication
@EnableMongoAuditing // Enables auditing for MongoDB entities (createdAt, lastModifiedAt, etc.)
@EnableAsync // Enables asynchronous processing for better performance
@EnableScheduling // Enables scheduled tasks for background processing
public class TaskManagementApplication {

    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }
}