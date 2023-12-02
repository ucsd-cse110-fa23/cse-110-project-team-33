package cse.gradle.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Recipe;

import java.io.*;
import java.util.*;

import org.bson.Document;
import org.checkerframework.checker.units.qual.m;
import org.json.JSONArray;

// remake to recipe handler which requires a user id as a part of the http request
public class RecipeHandler implements HttpHandler {

    MongoDB usersDB;

    public RecipeHandler(MongoDB mongoDB) {
        this.usersDB = mongoDB;
        usersDB.connect();
    }

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

            // Get user id from request, first query is user id, second is recipe id (if
            // applicable)
            // Example: http://localhost:8100/?userId=123&recipeId=456
            String userId = query.substring(query.indexOf("=") + 1, query.indexOf("&"));
            String recipeId = query.substring(query.lastIndexOf("=") + 1);

            if (method.equals("GET")) {
                if (recipeId.equals("")) {
                    response = handleGetAll(httpExchange, userId);
                } else {
                    response = handleGet(httpExchange, userId, recipeId);
                }
            } else if (method.equals("POST")) {
                response = handlePost(httpExchange, userId);
            } else if (method.equals("PUT")) {
                response = handlePut(httpExchange, userId, recipeId);
            } else if (method.equals("DELETE")) {
                response = handleDelete(httpExchange, userId, recipeId);
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

    /*
    * Handles GET requests by returning the recipe associated with the id
    */
    private String handleGet(HttpExchange httpExchange, String userId, String recipeId) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String response = "Invalid GET request";
        if (query != null) {
            // Search for the current user's recipe list
            Document user = usersDB.findOne("userId", userId);
            List<Document> recipeList = user.getList("recipeList", Document.class);

            // Break if no recipe list was found
            if (recipeList == null) {
                response += "No recipe list found for user " + userId;
                return response;
            }

            // Otherwise find the recipe with the given id
            Document recipe = null;
            for (Document r : recipeList) {
                if (r.get("id").equals(recipeId)) {
                    recipe = r;
                    break;
                }
            }

            // Break if no recipe was found
            if (recipe == null) {
                response += "No recipe found for id " + recipeId;
                return response;
            }

            // Otherwise return the recipe
            response = recipe.toJson();
            
        }
        return response;
    }

    /*
     * Handles GET requests by returning the recipe associated with the id
     */
    private String handleGetAll(HttpExchange httpExchange, String userId) throws IOException {
        // Search for the current user's recipe list
        Document user = usersDB.findOne("userId", userId);
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

        // get existing recipe list
        Document user = usersDB.findOne("userId", userId);
        List<Document> recipeList = user.getList("recipeList", null);

        // append new recipe to recipe list
        recipeList.add(recipe.toDocument());

        // Update the user with the new recipe list
        Document updatedUser = new Document("$set", new Document("recipeList", recipeList));
        usersDB.updateOne("userId", userId, updatedUser);

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
    private String handlePut(HttpExchange httpExchange, String userId, String recipeId) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String response = "Invalid PUT request";

        if (query != null) {

            // get existing recipe list
            Document user = usersDB.findOne("userId", userId);
            List<Document> recipeList = user.getList("recipeList", Document.class);

            // Break if no recipe list was found
            if (recipeList == null) {
                response += "No recipe list found for user " + userId;
                return response;
            }

            // find the recipe to update
            Document existingRecipeDocument = null;
            for (Document recipe : recipeList) {
                if (recipe.get("id").equals(recipeId)) {
                    existingRecipeDocument = recipe;
                    break;
                }
            }

            // Break if no recipe was found
            if (existingRecipeDocument == null) {
                response += "No recipe found for id " + recipeId;
                return response;
            }

            // Create a recipe object from the existing recipe
            Recipe existingRecipe = Recipe.parseRecipeFromDocument(existingRecipeDocument);

            // Extract fields from the JSON and update the existing recipe
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
            String ingredients = jsonNode.has("ingredients") ? jsonNode.get("ingredients").asText()
                    : existingRecipe.getIngredients();
            String instructions = jsonNode.has("instructions") ? jsonNode.get("instructions").asText() : existingRecipe
                    .getInstructions();
            String category = jsonNode.has("category") ? jsonNode.get("category").asText() : existingRecipe
                    .getCategory();
            String name = jsonNode.has("name") ? jsonNode.get("name").asText() : existingRecipe.getName();

            // Update the existing recipe
            existingRecipe.setIngredients(ingredients);
            existingRecipe.setInstructions(instructions);
            existingRecipe.setCategory(category);
            existingRecipe.setName(name);

            // Update the recipe list
            recipeList.remove(existingRecipeDocument);
            recipeList.add(existingRecipe.toDocument());

            // Update the user
            Document updatedUser = new Document("$set", new Document("recipeList", recipeList));
            usersDB.collection.updateOne(user, updatedUser);

            // Response
            response = "Updated entry: " + existingRecipe.toString();

            // Close scanner
            scanner.close();
        }

        return response;
    }

    /*
     * Handles DELETE requests by deleting the recipe associated with the id. The
     * recipe id is
     * expected to be in the query string.
     */
    private String handleDelete(HttpExchange httpExchange, String userId, String recipeId) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String response = "Invalid DELETE request";

        if (query != null) {

            // Use MongoDB to find the user and existing recipe with the given ids
            MongoDB mongoDB = new MongoDB("mongodb+srv://trevor:cse110@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority", "users", response);
            mongoDB.connect();

            // get existing recipe list
            Document user = mongoDB.findOne("userId", userId);
            List<Document> recipeList = user.getList("recipeList", Document.class);

            // Break if no recipe list was found
            if (recipeList == null) {
                response += "No recipe list found for user " + userId;
                return response;
            }

            // find the recipe to delete
            Document existingRecipeDocument = null;
            for (Document recipe : recipeList) {
                if (recipe.get("id").equals(recipeId)) {
                    existingRecipeDocument = recipe;
                    break;
                }
            }

            // Break if no recipe was found
            if (existingRecipeDocument == null) {
                response += "No recipe found for id " + query;
                return response;
            }

            // Delete the recipe
            recipeList.remove(existingRecipeDocument);

            // Update the user with the new recipe list 
            Document updatedUser = new Document("$set", new Document("recipeList", recipeList));
            mongoDB.updateOne("userId", userId, updatedUser);

            // Response
            response = "Deleted entry: " + existingRecipeDocument.toString();
        }

        return response;
    }

}
