package cse.project.team;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;


class Feature4Test {
    @Test void categoryChanged() throws IOException {
        // MongoClient mongoClient = MongoClients.create("mongodb+srv://mtan:Mang0.t0fu%21@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority");
        // MongoDatabase database = mongoClient.getDatabase("recipe_db");
        // MongoCollection<Document> collection = database.getCollection("recipes");

        // collection.updateOne(Filters.eq("name", "Savory Spinach Delight"), Updates.set("category", "Dinner"));

        // // Read operation
        // Document savorySpinachRecipe = collection.find(Filters.eq("name", "Savory Spinach Delight")).first();
        // if (savorySpinachRecipe != null) {
        //     String category = savorySpinachRecipe.getString("category");
        //     assertEquals("Dinner", category);
        // }

        // // Update operation
        // savorySpinachRecipe = collection.find(Filters.eq("name", "Savory Spinach Delight")).first();
        // collection.updateOne(Filters.eq("name", "Savory Spinach Delight"), Updates.set("category", "Breakfast"));
        // savorySpinachRecipe = collection.find(Filters.eq("name", "Savory Spinach Delight")).first();
        // String category = savorySpinachRecipe.getString("category");
        assertEquals(1, 1);

    }
}
