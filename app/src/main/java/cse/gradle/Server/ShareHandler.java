package cse.gradle.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import org.bson.Document;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import cse.gradle.Recipe;

public class ShareHandler implements HttpHandler {
    
        MongoDB usersDB;
    
        public ShareHandler(MongoDB mongoDB) {
            this.usersDB = mongoDB;
            usersDB.connect();
        }
    
        // Handles a GET request for a webpage to share a recipe
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "Request Received";
            String method = httpExchange.getRequestMethod();


            URI uri = httpExchange.getRequestURI();
            System.out.println("URI: " + uri);
            String query = uri.getRawQuery();
            System.out.println("Query: " + query);
            String userId = query.substring(query.indexOf("=") + 1, query.indexOf("&"));
            String recipeId = query.substring(query.lastIndexOf("=") + 1);

            try {
                if (method.equals("GET")) {
                    response = handleGet(httpExchange, userId, recipeId);
                } else {
                    throw new Exception("Unsupported HTTP method: " + method);
                }
    
                // Set the response character encoding to UTF-8 and content to html
                httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
    
                // Convert the response string to bytes with UTF-8 encoding
                byte[] responseBytes = response.getBytes("UTF-8");
    
                // Send the response
                httpExchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                httpExchange.sendResponseHeaders(500, response.length());
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }

        }

   /*
    * Handles GET requests by returning the recipe associated with the id in an HTML webpage
    */
    private String handleGet(HttpExchange httpExchange, String userId, String recipeId) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        WebPageBuilder webpageBuilder = new ErrorWebPageBuilder("Invalid GET request");

        if (query != null) {

            try {
                // Search for the current user's recipe list
                Document user = usersDB.findOne("userId", userId);

                // Throw error if no user was found
                if (user == null) {
                    throw new Exception("No user found for id " + userId);
                }

                List<Document> recipeList = user.getList("recipeList", Document.class);

                // Throw error recipe list was found
                if (recipeList == null) {
                    throw new Exception("No recipe list found for user " + userId);
                }

                Document recipe = null;

                // Otherwise find the recipe with the given id
                for (Document r : recipeList) {
                    if (r.get("id").equals(recipeId)) {
                        recipe = r;
                        break;
                    }
                }

                // Throw error if no recipe was found
                if (recipe == null) {
                    throw new Exception("No recipe found for id " + recipeId);
                }            

                // Convert the recipe to a Recipe object
                Recipe recipeObject = Recipe.parseRecipeFromDocument(recipe);

                // Build recipe display webpage
                webpageBuilder = new RecipeWebPageBuilder(recipeObject);

            } catch (Exception e) {
               // Print error message and construct error webpage
                System.out.println("Error: " + e.toString());
                webpageBuilder = new ErrorWebPageBuilder(e.toString());
            }
        }

        // Build the webpage and return it
        return webpageBuilder.getWebpage();
    }
}

