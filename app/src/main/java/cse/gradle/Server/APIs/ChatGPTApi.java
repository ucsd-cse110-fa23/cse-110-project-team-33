package cse.gradle.Server.APIs;

public interface ChatGPTApi {
    String[] generateResponse(String mealType, String ingredients) throws Exception;
}
