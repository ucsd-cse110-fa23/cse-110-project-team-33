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
        Document document = collection.find(and(eq(keyUsername, valueUsername), eq(keyPassword, valuePassword)))
                .first();
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

    /*
     * Pushes a new document to a list in a document
     * 
     * @param key: the key of the document to update
     * 
     * @param value: the value of the document to update
     * 
     * @param listName: the name of the list in the document to push to
     * 
     * @param newDocument: the document to push to the list
     */
    public void pushToDocumentList(String key, String value, String listName, Document newDocument) {
        try {
            // Create the update document
            Document updateDocument = new Document("$push", new Document(listName, newDocument));

            // Update the user with the new document
            collection.updateOne(eq(key, value), updateDocument);
            System.out.println("Updated user's " + collectionName + " with a new document.");
        } catch (Exception e) {
            System.out.println("Error updating " + collectionName + ": " + e.getMessage());
        }
    }

    public void updateDocumentList(String key, String value, String listName, List<Document> newDocuments) {
        try {
            // Create the update document
            Document updateDocument = new Document("$set", new Document(listName, newDocuments));

            // Update the user with the new document
            collection.updateOne(eq(key, value), updateDocument);
            System.out.println("Updated user's " + collectionName + " with a new document.");
        } catch (Exception e) {
            System.out.println("Error updating " + collectionName + ": " + e.getMessage());
        }
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
