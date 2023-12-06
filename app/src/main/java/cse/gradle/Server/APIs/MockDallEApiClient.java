package cse.gradle.Server.APIs;

public class MockDallEApiClient extends DallEApiClient {
    @Override
    public String generateChatResponse(String prompt) {
        // BEGIN: Mock generateChatResponse
        // Check if the prompt is valid
        if (prompt == null || prompt.isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }

        // Return a preset image URL provided by wikipedia
        return "https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg?20070224000419";
        // END: Mock generateChatResponse
    }
}
