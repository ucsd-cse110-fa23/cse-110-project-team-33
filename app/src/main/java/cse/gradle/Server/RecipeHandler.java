package cse.gradle.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Recipe;

import java.io.*;
import java.util.*;

import org.bson.Document;
import org.json.JSONArray;

// remake to recipe handler which requires a user id as a part of the http request
public class RecipeHandler implements HttpHandler {

    /*
     * Handles HTTP requests by calling the appropriate method
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();
        try {

            // Check if user id is in the request
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                throw new Exception("No user id in request");
            }

            // Get user id from request
            String userId = query.substring(query.indexOf("=") + 1);


            if (method.equals("GET")) {
                response = handleGetAll(httpExchange, userId);
            } else if (method.equals("POST")) {
                response = handlePost(httpExchange, userId);
            } else if (method.equals("PUT")) {
                response = handlePut(httpExchange, userId);
            } else if (method.equals("DELETE")) {
                response = handleDelete(httpExchange, userId);
            } else {
                throw new Exception("Unsupported HTTP method: " + method);
            }

            // Set the response character encoding to UTF-8
            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

            // Convert the response string to bytes with UTF-8 encoding
            byte[] responseBytes = response.getBytes("UTF-8");

            // Send the response
            httpExchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(responseBytes);
            }


        } catch (Exception e) {
            System.out.println("An erroneous request");
            response = e.toString();
            e.printStackTrace();
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream outStream = httpExchange.getResponseBody();
        outStream.write(response.getBytes());
        outStream.close();
    }

    // /*
    //  * Handles GET requests by returning the recipe associated with the id
    //  */
    // private String handleGet(HttpExchange httpExchange) throws IOException {
    //     String query = httpExchange.getRequestURI().getQuery();
    //     String response = "Invalid GET request";
    //     if (query != null) {
    //         // // correct uri for users_db?
    //         // MongoDB mongoDB = new MongoDB("mongodb+srv://trevor:cse110@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority", "users_db", "users");
    //         // mongoDB.connect();
    //         String id = query.substring(query.indexOf("=") + 1);
    //         Recipe recipe = data.get(id);
    //         if (recipe != null) {
    //             ObjectMapper objectMapper = new ObjectMapper();
    //             response = objectMapper.writeValueAsString(recipe);
    //             System.out.println("Queried for " + id + " and found " + recipe);
    //         } else {
    //             response = "No recipe found for id " + id;
    //         }
    //     }
    //     return response;
    // }

    /*
     * Handles GET requests by returning the recipe associated with the id
     */
    private String handleGetAll(HttpExchange httpExchange, String userId) throws IOException {
        // Search for the current user's recipe list
        MongoDB mongoDB = new MongoDB("mongodb+srv://trevor:cse110@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority", "user_db", "users");
        mongoDB.connect();
        Document user = mongoDB.findOne("userId", userId);
        List<Document> recipeList = (List<Document>) user.get("recipeList");    

        JSONArray jsonArray = new JSONArray(recipeList);

        return jsonArray.toString();
    }

    /*
     * Handles POST requests by adding the recipe, which is contained in the query
     */
    private String handlePost(HttpExchange httpExchange, String userId) throws IOException {
        InputStream inStream = httpExchange.getRequestBody();
        Scanner scanner = new Scanner(inStream);
        StringBuilder postData = new StringBuilder();

        while (scanner.hasNextLine()) {
            postData.append(scanner.nextLine());
        }

        System.out.println("Received JSON data: " + postData);

        // Parse JSON using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(postData.toString());

        // Extract individual fields from the JSON
        String ingredients = jsonNode.has("ingredients") ? jsonNode.get("ingredients").asText() : "";
        String instructions = jsonNode.has("instructions") ? jsonNode.get("instructions").asText() : "";
        String category = jsonNode.has("category") ? jsonNode.get("category").asText() : "";
        String name = jsonNode.has("name") ? jsonNode.get("name").asText() : "";
        UUID id = jsonNode.has("id") ? UUID.fromString(jsonNode.get("id").asText()) : UUID.randomUUID();

        // Create a recipe object
        Recipe recipe = new Recipe(ingredients, instructions, category, name);

        // Add id to the recipe
        recipe.setId(id);

        MongoDB mongoDB = new MongoDB("mongodb+srv://trevor:cse110@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority", "user_db", "users");
        mongoDB.connect();


        // get existing recipe list
        Document user = mongoDB.findOne("userId", userId);
        List<Document> recipeList = (List<Document>) user.get("recipeList"); 
        
        // append new recipe to recipe list
        recipeList.add(recipe.toDocument());
        
        Document updatedUser = new Document("$set", new Document("recipeList", recipeList));

        mongoDB.collection.updateOne(user, updatedUser);
        

        // Response
        String response = "Posted entry: " + recipe.toString();

        System.out.println(response);

        // Close scanner
        scanner.close();

        return response;
    }

    /*
     * Handles PUT requests by updating the recipe associated with the id
     */
    private String handlePut(HttpExchange httpExchange, String userId) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String response = "Invalid PUT request";

        if (query != null) {
            String id = query.substring(query.indexOf("=") + 1);
            Recipe existingRecipe = data.get(id);

            if (existingRecipe != null) {
                // Read the JSON data from the request body
                InputStream inStream = httpExchange.getRequestBody();
                Scanner scanner = new Scanner(inStream);
                StringBuilder putData = new StringBuilder();

                while (scanner.hasNextLine()) {
                    putData.append(scanner.nextLine());
                }

                System.out.println("Received JSON data for PUT request: " + putData);

                // Parse JSON using Jackson
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(putData.toString());

                // Extract individual fields from the JSON
                String newIngredients = jsonNode.has("ingredients") ? jsonNode.get("ingredients").asText()
                        : existingRecipe.getIngredients();
                String newInstructions = jsonNode.has("instructions") ? jsonNode.get("instructions").asText()
                        : existingRecipe.getInstructions();
                String newCategory = jsonNode.has("category") ? jsonNode.get("category").asText()
                        : existingRecipe.getCategory();
                String newName = jsonNode.has("name") ? jsonNode.get("name").asText() : existingRecipe.getName();
                // id is not updated in the case of a PUT request

                // Update the existing recipe
                existingRecipe.setIngredients(newIngredients);
                existingRecipe.setInstructions(newInstructions);
                existingRecipe.setCategory(newCategory);
                existingRecipe.setName(newName);

                // Delete CSV and rewrite it from data
                FileWriter writer = new FileWriter("src/main/java/cse/gradle/Server/recipes.csv", false);

                // Pull recipes from data and put them into the local CSV file
                List<Recipe> recipes = new ArrayList<>(data.values());
                LocalDatabase.saveListToLocal(recipes);

                writer.close();

                // Response
                response = "Updated entry for id " + id + ". New recipe: " + existingRecipe.toString();

                System.out.println(response);

                // Close scanner
                scanner.close();
            } else {
                response = "No recipe found for id " + id;
            }
        }
        return response;
    }

    /*
     * Handles DELETE requests by deleting the recipe associated with the id
     */
    private String handleDelete(HttpExchange httpExchange, String userId) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String response = "Invalid DELETE request";
        if (query != null) {
            String id = query.substring(query.indexOf("=") + 1);
            Recipe recipe = data.get(id);
            if (recipe != null) {
                data.remove(id);
                response = "Deleted entry for id " + id;
                System.out.println(response);

                // Delete CSV and rewrite it from data
                FileWriter writer = new FileWriter("src/main/java/cse/gradle/Server/recipes.csv", false);

                // Pull recipes from data and put them into the local CSV file
                List<Recipe> recipes = new ArrayList<>(data.values());
                LocalDatabase.saveListToLocal(recipes);

                writer.close();
            } else {
                response = "No recipe found for id " + id;
            }
        }

        return response;
    }

}
