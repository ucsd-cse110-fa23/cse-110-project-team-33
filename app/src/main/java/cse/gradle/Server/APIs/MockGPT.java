package cse.gradle.Server.APIs;

public class MockGPT implements ChatGPTApi {
    public String[] generateResponse(String mealType, String ingredients) throws Exception {
        String[] response = new String[4];
        
        response[0] = "Mock Title";
        response[1] = mealType;
        response[2] = ingredients;
        response[3] = "Mock Instructions";

        return response;
    }
}
