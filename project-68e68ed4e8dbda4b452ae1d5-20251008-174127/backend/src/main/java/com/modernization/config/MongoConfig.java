package com.modernization.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * MongoDB configuration class for the application.
 * Configures MongoDB connection settings, validation, and repositories.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.modernization.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.connection-timeout:10000}")
    private int connectionTimeout;

    @Value("${spring.data.mongodb.max-connection-idle-time:60000}")
    private int maxConnectionIdleTime;

    @Value("${spring.data.mongodb.max-connection-pool-size:100}")
    private int maxConnectionPoolSize;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxConnectionIdleTime(maxConnectionIdleTime, TimeUnit.MILLISECONDS)
                           .maxSize(maxConnectionPoolSize))
                .applyToSocketSettings(builder -> 
                    builder.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS))
                .build();
        
        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.modernization.model");
    }

    /**
     * Configures MongoDB validation using Bean Validation (JSR-380)
     * @param localValidatorFactoryBean the validator factory
     * @return a validating event listener for MongoDB operations
     */
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(
            LocalValidatorFactoryBean localValidatorFactoryBean) {
        return new ValidatingMongoEventListener(localValidatorFactoryBean);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Creates a custom MongoTemplate with the configured client and database name
     * @return configured MongoTemplate instance
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}