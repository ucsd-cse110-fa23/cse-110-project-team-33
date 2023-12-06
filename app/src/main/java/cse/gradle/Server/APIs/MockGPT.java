package cse.gradle.Server.APIs;

public class MockGPT implements ChatGPTApi {
    public String[] generateResponse(String mealType, String ingredients) throws Exception {
        String[] response = new String[4];
        
        response[0] = "title";
        response[1] = mealType;
        response[2] = ingredients;
        response[3] = "instructions";

        return response;
    }
}
