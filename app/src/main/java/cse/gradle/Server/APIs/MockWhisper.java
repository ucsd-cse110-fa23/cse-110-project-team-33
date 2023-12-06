package cse.gradle.Server.APIs;

import java.io.File;
import java.nio.file.NoSuchFileException;

public class MockWhisper implements WhisperApi {
    @Override
    public String generateResponse(String filePath) throws Exception {

        File file = new File(filePath);
        if (!file.exists()) {
            throw new NoSuchFileException(filePath);
        }

        return "Mock Transcription Result";
    }
    
}
