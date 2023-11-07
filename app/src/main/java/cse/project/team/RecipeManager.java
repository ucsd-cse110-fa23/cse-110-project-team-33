package cse.project.team;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;

public class RecipeManager {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://mtan:Mang0.t0fu%21@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase database = mongoClient.getDatabase("recipe_db");
        MongoCollection<Document> collection = database.getCollection("recipes");

        // Step 1: Read Recipe Data
        try {
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current directory: " + currentDir);
            BufferedReader reader = new BufferedReader(new FileReader("app/src/main/resources/dummy-recipes.csv"));

            // Skip the header row
            String header = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                String recipeName = data[0];
                String category = data[1];
                String ingredients = data[2];
                String instructions = data[3];

                Document recipe = new Document("name", recipeName)
                        .append("category", category)
                        .append("ingredients", ingredients)
                        .append("instructions", instructions);

                collection.insertOne(recipe);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Step 2: Print Recipe Count
        long recipeCount = collection.countDocuments();
        System.out.println("Total number of recipes: " + recipeCount);

        // Step 3: Read Operation
        Document savorySpinachRecipe = collection.find(Filters.eq("name", "Savory Spinach Delight")).first();
        if (savorySpinachRecipe != null) {
            // double hours = savorySpinachRecipe.getDouble("hoursRequired");
            // System.out.println("Hours required for Savory Spinach Delight: " + hours);
            String category = savorySpinachRecipe.getString("category");
            System.out.println("Savory Spinach Delight category: " + category);
        }

        // Step 4: Update Operation
        // collection.updateOne(Filters.eq("name", "Savory Spinach Delight"), Updates.set("hoursRequired", 4.5));
        collection.updateOne(Filters.eq("name", "Savory Spinach Delight"), Updates.set("category", "Breakfast"));

        // Step 5: Print Updated Hours
        savorySpinachRecipe = collection.find(Filters.eq("name", "Savory Spinach Delight")).first();
        if (savorySpinachRecipe != null) {
            // double updatedHours = savorySpinachRecipe.getDouble("hoursRequired");
            // System.out.println("Updated hours for Savory Spinach Delight: " + updatedHours);
            String category = savorySpinachRecipe.getString("category");
            System.out.println("Savory Spinach Delight category: " + category);
        }

        // Step 6: Delete Operation
        collection.deleteOne(Filters.eq("name", "Spicy Shrimp Tacos"));

        // Step 7: Print Recipe Count
        recipeCount = collection.countDocuments();
        System.out.println("Total number of recipes after deletion: " + recipeCount);

        mongoClient.close(); // close the connection
    }
}
