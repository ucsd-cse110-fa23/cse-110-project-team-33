package cse.gradle.Server.APIs;

public class MockWhisper implements WhisperApi {
    @Override
    public String generateResponse(String filePath) throws Exception {
        return "response";
    }
    
}
