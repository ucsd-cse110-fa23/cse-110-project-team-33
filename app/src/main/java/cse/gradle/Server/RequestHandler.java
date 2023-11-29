package cse.gradle.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Recipe;
import cse.gradle.User;
import cse.gradle.Server.MongoDB;

import java.io.*;
import java.util.*;

public class RequestHandler implements HttpHandler {

    // private final String uri = "mongodb+srv://mtan:U0h5GjAbFXT68Ki1@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority";
    // private final MongoDatabase users_db;
    // private final MongoCollection<Document> users;

    // public RequestHandler() {
    //     try (MongoClient mongoClient = MongoClients.create(uri)) {
    //         this.users_db = mongoClient.getDatabase("user_db");
    //         this.users = users_db.getCollection("users");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

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
        String username = jsonNode.has("username") ? jsonNode.get("user").asText() : "";
        String password = jsonNode.has("password") ? jsonNode.get("password").asText() : "";
        List<Recipe> recipeList = jsonNode.has("recipeList") ? jsonNode.get("recipeList") : new List<Recipe>();
        UUID userId = jsonNode.has("userId") ? UUID.fromString(jsonNode.get("userId")) : UUID.randomUUID();

        // Create a user object
        User user = new User(username, password);

        // Add user to MongoDB JSON
        MongoDB mongoDB = new MongoDB("mongodb+srv://trevor:cse110@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority", "users_db", "users");
        mongoDB.connect();
        users.insertOne(user.toDocument());

        // Response
        String response = "Posted entry: \n" + user.toString();
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
