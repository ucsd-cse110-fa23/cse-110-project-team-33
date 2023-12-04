package cse.gradle.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import cse.gradle.Recipe;

import java.io.*;
import java.net.URI;
import java.util.*;

import org.bson.Document;
import org.bson.conversions.Bson;
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

            // Get user id from request, first query is user id, second is recipe id (if applicable) or sort method (if applicable)
            // Example: http://localhost:8100/recipes?userId=1&recipeId=1 
            // Example: http://localhost:8100/recipes?userId=1&sort="a-z"
            // Example: http://localhost:8100/recipes?userId=1
            
            URI uri = httpExchange.getRequestURI();
            System.out.println("URI: " + uri);
            String query = uri.getRawQuery();
            System.out.println("Query: " + query);
            String userId = query.substring(query.indexOf("=") + 1, query.indexOf("&"));

            String recipeId = "";
            if (query.contains("recipeId")) {
                recipeId = query.substring(query.indexOf("recipeId=") + "recipeId=".length());
            }

            String sortOption = "";
            if (query.contains("sort")) {
                sortOption = query.substring(query.indexOf("sort=") + "sort=".length());
            }   

             // If there is no user id, return an error
             if (userId.equals("")) {
                 throw new Exception("No user id provided");
             }

            if (method.equals("GET")) {
                if (recipeId.equals("")) {
                    response = handleGetAll(httpExchange, userId, sortOption);
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

            // Break if no user was found
            if (user == null) {
                response += "No user found for id " + userId;
                return response;
            }

            List<Document> recipeList = user.getList("recipeList", Document.class);

            // Break if no recipe list was found
            if (recipeList == null) {
                response = "No recipe list found for user " + userId;
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
                response = "No recipe found for id " + recipeId;
                return response;
            }

            // Otherwise return the recipe
            response = recipe.toJson();
            
        }
        return response;
    }

    /*
     * Handles GET requests with no id by returning all recipes
     * Optional sort options: "a-z", "z-a", "newest-oldest", "oldest-newest"
     */
    private String handleGetAll(HttpExchange httpExchange, String userId, String sortOption) throws IOException {
        String response = "Invalid GET request";
        
        // Search for the current user's recipe list
        Document user = usersDB.findOne("userId", userId);

        // Break if no user was found
        if (user == null) {
            response += "No user found for id " + userId;
            return response;
        }

        List<Document> recipeDocumentList = user.getList("recipeList", Document.class);

        // Break if no recipe list was found
        if (recipeDocumentList == null) {
            response += "No recipe list found for user " + userId;
            return response;
        }

        // Convert the recipe list to a List<Recipe>
        List<Recipe> recipeList = Recipe.parseRecipeListFromString(new JSONArray(recipeDocumentList).toString());
  
        // TODO: If no sort option was provided, return the recipes in newest-oldest order by default
        // Sort the recipes if a sort option was provided
        if (!sortOption.equals("")) {
            if (sortOption.equals("a-z")) {
                Recipe.sortByName(recipeList, false);
            } else if (sortOption.equals("z-a")) {
                Recipe.sortByName(recipeList, true);
            }
        }

        // Convert recipeList back into a document list
        recipeDocumentList = new ArrayList<Document>();

        for (Recipe recipe : recipeList) {
            recipeDocumentList.add(recipe.toDocument());
        }

        JSONArray jsonArray = new JSONArray(recipeDocumentList);

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
        
        scanner.close();

        System.out.println("Received JSON data: " + postData);

        // Parse recipe from JSON
        Recipe recipe = Recipe.parseRecipeFromString(postData.toString());

        // Response
        String response = "Posted entry: " + recipe.toString();

        // Convert the recipe to a Document
        Document newRecipeDocument = recipe.toDocument();

        // Search for the user to make sure they exist
        Document user = usersDB.findOne("userId", userId);
        // Break if no user was found
        if (user == null) {
            return "No user found for id " + userId;
        }
        // Push the recipe to the user's recipe list
        usersDB.pushToDocumentList("userId", userId, "recipeList", newRecipeDocument);

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

            // Break if no user was found
            if (user == null) {
                return "No user found for id " + userId;
            }
        
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

            // Break if no recipe with matching id was found
            if (existingRecipeDocument == null) {
                response += "No recipe found for id " + recipeId;
                return response;
            }

            // Remove the existing recipe
            recipeList.remove(existingRecipeDocument);

            // Parse the new recipe from the request body
            InputStream inStream = httpExchange.getRequestBody();
            Scanner scanner = new Scanner(inStream);
            StringBuilder postData = new StringBuilder();
            while (scanner.hasNextLine()) {
                postData.append(scanner.nextLine());       
            }

            // Print JSON data
            System.out.println("Received JSON data: " + postData);

            // Parse recipe from JSON
            Recipe newRecipe = Recipe.parseRecipeFromString(postData.toString());

            // Convert the recipe to a Document
            Document newRecipeDocument = newRecipe.toDocument();

            // Add the new recipe to the recipe list
            recipeList.add(newRecipeDocument);

            // Update the recipe in the user's recipe list
            usersDB.updateDocumentList("userId", userId, "recipeList", recipeList);

            // Response
            response = "Updated entry: " + Recipe.parseRecipeFromDocument(existingRecipeDocument).toString() + " with: " + newRecipe.toString();

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


            // get existing recipe list
            Document user = usersDB.findOne("userId", userId);

            // Break if no user was found
            if (user == null) {
                return "No user found for id " + userId;
            }

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
            usersDB.updateDocumentList("userId", userId, "recipeList", recipeList);

            // Response
            response = "Deleted entry: " + Recipe.parseRecipeFromDocument(existingRecipeDocument).toString();
        }

        return response;
    }

}
