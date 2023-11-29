package cse.gradle.Server;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.List;

import org.bson.Document;
import static com.mongodb.client.model.Filters.*;


public class MongoDB {
    private String uri;
    private String databaseName;
    private String collectionName;
    private MongoClient mongoClient;
    private MongoDatabase db;
    public MongoCollection<Document> collection;

    public MongoDB(String uri, String databaseName, String collectionName) {
        this.uri = uri;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    public void connect() {
        try {
            mongoClient = MongoClients.create(uri);
            db = mongoClient.getDatabase(databaseName);
            collection = db.getCollection(collectionName);
            System.out.println("Connected to MongoDB");
        } catch (Exception e) {
            System.out.println("Error connecting to MongoDB");
        }
    }

    public void insertOne(Document document) {
        collection.insertOne(document);
        System.out.println("Inserted document: " + document.toJson());
    }

    public void insertMany(List<Document> documents) {
        collection.insertMany(documents);
    }

    // find one document that matches key-value pair
    // if no document is found, return null
    public Document findOne(String key, String value) {
        Document document = collection.find(eq(key, value)).first();
        if (document == null) {
            System.out.println("No document found with " + key + " = " + value);
            return null;
        }
        System.out.println("Found document: " + document.toJson());
        return document;
    }

    // find one document that matches key-value pair
    // if no document is found, return null
    public Document findOne(String keyUsername, String valueUsername, String keyPassword, String valuePassword) {
        Document document = collection.find(and(eq(keyUsername, valueUsername), eq(keyPassword, valuePassword))).first();
        if (document == null) {
            System.out.println("No document found with " + keyUsername + " = " + valueUsername + 
                               " and " + keyPassword + " = " + valuePassword);
            return null;
        }
        System.out.println("Found document: " + document.toJson());
        return document;
    }

    // update one document that matches key-value pair
    public void updateOne(String key, String value, Document document) {
        collection.updateOne(eq(key, value), new Document("$set", document));
        System.out.println("Updated document: " + document.toJson());
    }

    // delete one document that matches key-value pair
    public void deleteOne(String key, String value) {
        collection.deleteOne(eq(key, value));
        System.out.println("Deleted document with " + key + " = " + value);
    }

    public void close() {
        mongoClient.close();
        db = null;
        collection = null;
        
    }

    
}
