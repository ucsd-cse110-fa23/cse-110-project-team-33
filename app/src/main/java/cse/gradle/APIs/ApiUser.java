package cse.gradle.APIs;

public class ApiUser {
    private final ChatGPTApi chatApi;

    public ApiUser(ChatGPTApi chatApi) {
        this.chatApi = chatApi;
    }

    public void useApi() {
        // Use chatApi to generate a chat response
        try {
            String[] messages = {"Hello", "How can you help?"};
            String response = chatApi.generateChatResponse(messages);
            System.out.println("API Response: " + response);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
