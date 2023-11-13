package cse.gradle.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Recipe;

import java.io.*;
import java.util.*;

public class RequestHandler implements HttpHandler {

    private final Map<String, Recipe> data;

    public RequestHandler(Map<String, Recipe> data) {
        this.data = data;
    }

    /*
     * Handles HTTP requests by calling the appropriate method
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();
        try {
            if (method.equals("GET")) {
                String query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    response = handleGetAll(httpExchange);
                } else if (query.contains("=")) {
                    response = handleGet(httpExchange);
                } else {
                    throw new Exception("Unsupported HTTP method: " + method);
                }
            } else if (method.equals("POST")) {
                response = handlePost(httpExchange);
            } else if (method.equals("PUT")) {
                response = handlePut(httpExchange);
            } else if (method.equals("DELETE")) {
                response = handleDelete(httpExchange);
            } else {
                throw new Exception("Unsupported HTTP method: " + method);
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
    private String handleGet(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        String response = "Invalid GET request";
        if (query != null) {
            String id = query.substring(query.indexOf("=") + 1);
            Recipe recipe = data.get(id);
            if (recipe != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                response = objectMapper.writeValueAsString(recipe);
                System.out.println("Queried for " + id + " and found " + recipe);
            } else {
                response = "No recipe found for id " + id;
            }
        }
        return response;
    }

    /*
     * Handles GET requests by returning the recipe associated with the id
     */
    private String handleGetAll(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();        
        List<Recipe> recipes = new ArrayList<>(LocalDatabase.readLocal());
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(recipes); 
        return response;
    }

    /*
     * Handles POST requests by adding the recipe, which is contained in the query
     */
    private String handlePost(HttpExchange httpExchange) throws IOException {
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

        // Add recipe to the data
        data.put(id.toString(), recipe);

        // Add recipe to CSV
        LocalDatabase.saveRecipeToLocal(recipe);

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
    private String handlePut(HttpExchange httpExchange) throws IOException {
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
    private String handleDelete(HttpExchange httpExchange) throws IOException {
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
