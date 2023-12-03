package cse.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;

import java.io.*;
import java.net.*;
import org.json.*;

public class Model {
    protected String userId;
    protected String urlString;

    public Model(String urlString) {
        this.userId = null;
        this.urlString = urlString;
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
    public String performRecipeRequest(String method, String recipeId, Recipe recipe) {
        // Check if userId is null, if so, return an error
        if (userId == null) {
            return "Error: Must login before performing requests";
        }
        try {
            // Builds a URL string in the format http://localhost:8100/recipe?userId=123&recipeId=456
            String recipeRequestURL = urlString + "/recipe?userId=" + userId + "&recipeId=";
            if (recipeId != null) {
                recipeRequestURL += recipeId;
            }

            System.out.println("Sending request to " + recipeRequestURL);

            URL url = new URI(recipeRequestURL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            if (method.equals("POST") || method.equals("PUT")) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonRecipe = objectMapper.writeValueAsString(recipe);

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

    // make a request to login a user, must be called before other requests besides register
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
                // Set the Model's userId to the one returned by the server if the login was successful
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
                // Set the Model's userId to the one returned by the server if the registration was successful
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

    // Whisper stuff
    private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static final String TOKEN = "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";
    private static final String MODEL = "whisper-1";

    // Helper method to write a parameter to the output stream in multipart form
    // data format
    private static void writeParameterToOutputStream(
            OutputStream outputStream,
            String parameterName,
            String parameterValue,
            String boundary) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
                ("Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n").getBytes());
        outputStream.write((parameterValue + "\r\n").getBytes());
    }

    // Helper method to write a file to the output stream in multipart form data
    // format
    private static void writeFileToOutputStream(
            OutputStream outputStream,
            File file,
            String boundary) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
                ("Content-Disposition: form-data; name=\"file\"; filename=\"" +
                        file.getName() +
                        "\"\r\n").getBytes());
        outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
    }

    // Helper method to handle a successful response
    private static String handleSuccessResponse(HttpURLConnection connection)
            throws IOException, JSONException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject responseJson = new JSONObject(response.toString());

        String generatedText = responseJson.getString("text");

        // Print the transcription result
        System.out.println("Transcription Result: " + generatedText);
        return generatedText;
    }

    // Helper method to handle an error response
    private static void handleErrorResponse(HttpURLConnection connection)
            throws IOException, JSONException {
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()));
        String errorLine;
        StringBuilder errorResponse = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorResponse.append(errorLine);
        }
        errorReader.close();
        String errorResult = errorResponse.toString();
        System.out.println("Error Result: " + errorResult);

    }

    public static String useWhisper(String filePathString) throws IOException, URISyntaxException {
        String returnString = "";

        String filePath = filePathString;

        // Create file object from file path
        File file = new File(filePath);
        // System.out.println(filePath);

        // Set up HTTP connection
        URL url = new URI(API_ENDPOINT).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Set up request headers
        String boundary = "Boundary-" + System.currentTimeMillis();
        connection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Authorization", "Bearer " + TOKEN);

        // Set up output stream to write request body
        OutputStream outputStream = connection.getOutputStream();

        // Write model parameter to request body
        writeParameterToOutputStream(outputStream, "model", MODEL, boundary);

        // Write file parameter to request body
        writeFileToOutputStream(outputStream, file, boundary);

        // Write closing boundary to request body
        outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

        // Flush and close output stream
        outputStream.flush();
        outputStream.close();

        // Get response code
        int responseCode = connection.getResponseCode();

        // Check response code and handle response accordingly
        if (responseCode == HttpURLConnection.HTTP_OK) {
            returnString = handleSuccessResponse(connection);
        } else {
            handleErrorResponse(connection);
        }

        // Disconnect connection
        connection.disconnect();

        return returnString;
    }

    // ChatGPT stuff
    private static final String API_ENDPOINT_GPT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL_GPT = "text-davinci-003";

    public static String useChatGPT(int maxTokenCount, String promptString)
            throws IOException, InterruptedException, URISyntaxException {

        // Set request parameters
        int maxTokens = maxTokenCount;
        String prompt = promptString;

        // Create a request body which you will pass into request object
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL_GPT);
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", 1.0);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(API_ENDPOINT_GPT))
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", API_KEY))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();

        JSONObject responseJSON = new JSONObject(responseBody);

        JSONArray choices = responseJSON.getJSONArray("choices");
        String generatedText = choices.getJSONObject(0).getString("text");

        System.out.println(generatedText);
        return generatedText;

    }

}