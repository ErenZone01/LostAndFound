package com.service_notification.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "service_notification"; // Nom de votre base de données MongoDB
    }

    @Bean
    @Override
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017"); // URI de connexion à MongoDB
    }

    @Override
    protected boolean autoIndexCreation() {
        return true; // Active la création automatique des index
    }

    @Bean
    public GridFSBucket gridFSBucket() {
        // Choisis ici le nom de ta base (doit correspondre à ta config
        // spring.data.mongodb.database)
        MongoDatabase database = mongoClient().getDatabase("service_annonce");
        return GridFSBuckets.create(database);
    }

}