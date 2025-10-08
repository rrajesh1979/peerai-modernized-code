package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the E-commerce application.
 * This application is built using Spring Boot and MongoDB for data persistence.
 * It serves as the foundation for modernizing a legacy JSF system while maintaining
 * business continuity and implementing modern architecture patterns.
 *
 * @author E-commerce Team
 * @version 1.0
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
@EnableScheduling
public class EcommerceApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(EcommerceApplication.class);

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        logger.info("Starting E-commerce Application...");
        try {
            SpringApplication.run(EcommerceApplication.class, args);
            logger.info("E-commerce Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start E-commerce Application", e);
            throw e;
        }
    }
}