package cse.gradle.Server.APIs;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.nio.file.Files;


import org.json.JSONObject;

public class DallEApiClient implements DallEApi {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/images/generations"; 
    private static final String API_KEY =
      "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";
    private static final String MODEL = "dall-e-2";

    public String generateChatResponse(String prompt) throws Exception {
        //return "";
        // Set request parameters
        //String prompt = "Sackboy from Little Big Planet";
        int n = 1;

        if(prompt == null){
            prompt = "";
        }

        System.out.println("DALLE 2 PROMPT: " + prompt);
        // Create a request body which you will pass into request object
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("prompt", prompt);
        requestBody.put("n", n);
        requestBody.put("size", "256x256");


        // Create the HTTP client
        HttpClient client = HttpClient.newHttpClient();


        // Create the request object
        HttpRequest request = HttpRequest
            .newBuilder()
            .uri(URI.create(API_ENDPOINT))
            .header("Content-Type", "application/json")
            .header("Authorization", String.format("Bearer %s", API_KEY))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();


        // Send the request and receive the response
        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );


        // Process the response
        String responseBody = response.body();


        JSONObject responseJson = new JSONObject(responseBody);

        // TODO: Process the response

        String generatedImageURL = responseJson.getJSONArray("data").getJSONObject(0).getString("url");
        
        System.out.println("DALL-E Response:");
        System.out.println(generatedImageURL);


        // Download the Generated Image to Current Directory
        try(
            InputStream in = new URI(generatedImageURL).toURL().openStream()
        )
        {
            Files.copy(in, Paths.get("image.jpg"));
        }

        return generatedImageURL;
    }
}
