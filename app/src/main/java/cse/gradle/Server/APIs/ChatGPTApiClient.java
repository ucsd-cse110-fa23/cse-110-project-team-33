package cse.gradle.Server.APIs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import cse.gradle.Model;

public class ChatGPTApiClient implements ChatGPTApi {

    private static final String API_ENDPOINT_GPT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL_GPT = "text-davinci-003";

    private static final int MAX_TOKENS = 100;

    public ChatGPTApiClient() {
        
    }

    public String[] generateResponse(String mealType, String ingredients) 
            throws IOException, InterruptedException, IllegalArgumentException {

        String[] response = new String[4];

        String title = generateTitle(mealType, ingredients);
        String instructions = generateInstructions(mealType, ingredients, title);

        response[0] = title;
        response[1] = mealType;
        response[2] = ingredients;
        response[3] = instructions;

        return response;
    }

    public String generateTitle(String mealType, String ingredients)
            throws IOException, InterruptedException, IllegalArgumentException {

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL_GPT);
        requestBody.put("prompt", "Give a 3 to 5 word name for a " + mealType + " recipe using the following ingredients: " + ingredients + ". Output nothing but the recipe name.");
        requestBody.put("max_tokens", MAX_TOKENS);
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
        String title = choices.getJSONObject(0).getString("text");
        
        return title;
    }

    public String generateInstructions(String mealType, String ingredients, String title)
            throws IOException, InterruptedException, IllegalArgumentException {
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL_GPT);
        requestBody.put("prompt", "Give only instructions to make a recipe for a " + mealType + " meal using only the following ingredients: " + ingredients + ". Base it on this recipe name: " + title + ". Make this concise and within 100 words");
        requestBody.put("max_tokens", MAX_TOKENS);
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
        String instructions = choices.getJSONObject(0).getString("text");

        return instructions;
    }
}
