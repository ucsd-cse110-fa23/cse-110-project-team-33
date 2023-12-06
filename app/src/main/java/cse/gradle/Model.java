package cse.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

import cse.gradle.Server.APIs.ChatGPTApiClient;
import cse.gradle.Server.APIs.WhisperApiClient;

import java.net.URI;
import java.net.URISyntaxException;

public class Model {
    protected String userId;
    protected String urlString;

    public Model(String urlString) {
        this.userId = null;
        this.urlString = urlString;
    }

    public String getShareLink(String recipeId) {
        return urlString + "/share?userId=" + userId + "&recipeId=" + recipeId;
    }

    /*
     * Performs an HTTP request to the server
     * For GET requests, uid should be the id of the recipe to retrieve
     * For POST requests, uid should be null and recipe should be the recipe to add
     * For PUT requests, uid should be the id of the recipe to update and recipe
     * should be the updated recipe
     * For DELETE requests, uid should be the id of the recipe to delete and recipe
     * should be null
     */
    private String performRecipeRequest(String method, String recipeId, Recipe recipe) {
        // Check if userId is null, if so, return an error
        if (userId == null) {
            return "Error: Must login before performing requests";
        }
        try {
            // Builds a URL string in the format
            // http://localhost:8100/recipe?userId=123&recipeId=456
            String recipeRequestURL = urlString + "/recipe?userId=" + userId + "&recipeId=";
            if (recipeId != null) {
                recipeRequestURL += recipeId;
            }

            URL url = new URI(recipeRequestURL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            if (method.equals("POST") || method.equals("PUT")) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonRecipe = objectMapper.writeValueAsString(recipe);

                int startIndex = jsonRecipe.indexOf("\"date\"", 0);
                startIndex += 7;
                int endIndex = jsonRecipe.indexOf(",", startIndex);
                String dateString = jsonRecipe.substring(startIndex, endIndex);

                Date tempDate = new Date();
                tempDate.setTime(Long.parseLong(dateString));

                jsonRecipe = jsonRecipe.replaceFirst(dateString, "\"" + tempDate.toString() + "\"");

                System.out.println("tempDate: " + tempDate.toString());
                System.out.println("jsonRecipe: " + jsonRecipe);

                try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream())) {
                    out.write(jsonRecipe);
                }
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("Connection refused")) {
                return "Error: Server down";
            }
            return "Error: " + ex.getMessage();
        }
    }

    // Calls the performRecipeRequest method with the GET method
    public String getRecipeList(String sortOption, String filterOption) {
        try {

            // Builds a URL string in the format
            // http://localhost:8100/recipe?userId=123&sort=a-z&filter=Breakfast
            String recipeRequestURL = urlString + "/recipe?userId=" + userId + "&sort=" + sortOption + "&filter="
                    + filterOption;

            URL url = new URI(recipeRequestURL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: " + ex.getMessage();
        }
    }

    // Overloaded getRecipeList method that defaults to no sort or filter option
    // TODO: Replace with newest to oldest sort option when implemented
    public String getRecipeList() {
        return getRecipeList("", "");
    }

    // Calls the performRecipeRequest method with and an id for the recipe we want
    public String getRecipe(String recipeId) {
        return performRecipeRequest("GET", recipeId, null);
    }

    // Calls the performRecipeRequest method with the POST method and a recipe to
    // add
    public String postRecipe(Recipe recipe) {
        return performRecipeRequest("POST", null, recipe);
    }

    // Calls the performRecipeRequest method with the PUT method and a recipe to
    // update
    public String putRecipe(String recipeId, Recipe recipe) {
        return performRecipeRequest("PUT", recipeId, recipe);
    }

    // Calls the performRecipeRequest method with the DELETE method and an id for
    // the recipe to delete
    public String deleteRecipe(String recipeId) {
        return performRecipeRequest("DELETE", recipeId, null);
    }

    // make a request to login a user, must be called before other requests besides
    // register
    public String performLoginRequest(String username, String password) {
        try {
            String urlString = "http://localhost:8100/login";
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String jsonUser = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream())) {
                out.write(jsonUser);
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                // Set the Model's userId to the one returned by the server if the login was
                // successful
                if (!response.toString().contains("Error")) {
                    this.userId = response.toString();
                }

                return response.toString();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("Connection refused")) {
                return "Error: Server down";
            }
            return "Error logging in: " + ex.getMessage();
        }
    }

    // make a request to register a new user
    public String performRegisterRequest(String username, String password) {
        try {
            String urlString = "http://localhost:8100/register";
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String jsonUser = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream())) {
                out.write(jsonUser);
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                // Set the Model's userId to the one returned by the server if the registration
                // was successful
                if (!response.toString().contains("Error")) {
                    this.userId = response.toString();
                }
                return response.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("Connection refused")) {
                return "Error: Server down";
            }
            return "Error: " + ex.getMessage();
        }
    }

    // TODO: Move this logic to execute on server (in GenerateRecipeHandler's
    // handlePost())
    // IN FUTURE IMPLEMENTATION, this method:
    // SENDS meal type and ingredients audio files in a single request,
    // RECEIVES String transcript for each audio file
    public static String[] performAudioTranscriptionRequest(String mealTypeFile, String ingredientsFile)
            throws IOException, URISyntaxException {

        String[] response = new String[2];

        WhisperApiClient whisperApi = new WhisperApiClient();
        response[0] = whisperApi.generateResponse(mealTypeFile);
        response[1] = whisperApi.generateResponse(ingredientsFile);

        return response;
    }

    // TODO: Move this logic to execute on server (in generateRecipeHandler's
    // handlePost())
    // IN FUTURE IMPLEMENTATION, this method:
    // SENDS 2 String audio transcriptions (meal type and ingredients),
    // RECEIVES JSONified recipe
    public static String[] performRecipeGenerationRequest(String mealType, String ingredients) {

        String[] response = new String[4];

        try {
            ChatGPTApiClient chatGPTApi = new ChatGPTApiClient();
            response = chatGPTApi.generateResponse(mealType, ingredients);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public String performFileWriteRequest(String audioFile) throws MalformedURLException, IOException {
        final String PUT_URL = "http://localhost:8100/generate?audioFile=" + audioFile;
        final File uploadFile = new File(audioFile);

        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        String charset = "UTF-8";
        URL url = new URL(PUT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);) {
            writer.append("--" + boundary).append(CRLF);
            writer.append(
                    "Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + uploadFile.getName() + "\"")
                    .append(CRLF);
            writer.append("Content-Length: " + uploadFile.length()).append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(uploadFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(uploadFile, output);
            output.flush();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
                return "Error: " + ex.getMessage();
            }
        }
    }

    // sets request method to POST -> reads in the JSON recipe received from the
    // server -> parses JSON recipe into Recipe object and returns the Recipe to
    // Controller
    public Recipe performRecipeGenerateRequest() throws URISyntaxException, IOException {
        final String POST_URL = "http://localhost:8100/generate";
        URL url = new URI(POST_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String generatedRecipe = "";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            generatedRecipe = response.toString();
            System.out.println(generatedRecipe);
        } catch (Exception exception) {
            exception.printStackTrace();
            if (exception.getMessage().contains("Connection refused")) {
                System.out.println("Error: Server down");
            }
            System.out.println("Error: " + exception.getMessage());
        }

        System.out.println("GENERATED JSON RECIPE: " + generatedRecipe);
        System.out.println("PARSED RECIPE: " + Recipe.parseRecipeFromString(generatedRecipe));

        return Recipe.parseRecipeFromString(generatedRecipe);
    }
}