package com.illia.config;

import com.mongodb.ConnectionString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class MongoDBConfig {

    @Bean
    public MongoClientFactoryBean mongo() throws IOException {
        var mongoClientFactory = new MongoClientFactoryBean();
        ConnectionString connectionString = new ConnectionString(readConnectionString());
        mongoClientFactory.setConnectionString(connectionString);
        return mongoClientFactory;
    }

    private String readConnectionString() throws IOException {
        return Files.readString(Path.of("mongo-client-uri.txt"));
    }

}
