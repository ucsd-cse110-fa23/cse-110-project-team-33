package cse.gradle;

import cse.gradle.Server.APIs.MockGPT;
import cse.gradle.Server.APIs.MockWhisper;
import cse.gradle.Server.APIs.ChatGPTApi;
import cse.gradle.Server.APIs.WhisperApi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.NoSuchFileException;

public class APITests {

    @Test
    public void testChatGPTGenerateResponse() {
        // Create an instance of the MockGPT for testing
        ChatGPTApi chatGPTApi = new MockGPT();

        try {
            // Test the generateResponse method with mock data
            String[] mockResponse = chatGPTApi.generateResponse("Dinner", "Pasta, Sauce, Cheese");

            // Add assertions to verify the correctness of the response
            assertNotNull(mockResponse);
            assertEquals(4, mockResponse.length);
            assertEquals("Mock Title", mockResponse[0]);
            assertEquals("Dinner", mockResponse[1]);
            assertEquals("Pasta, Sauce, Cheese", mockResponse[2]);
            assertEquals("Mock Instructions", mockResponse[3]);
        } catch (Exception e) {
            // Handle exceptions or fail the test if an unexpected exception occurs
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testWhisperGenerateResponse() {
        // Create an instance of the MockWhisper for testing
        WhisperApi whisperApi = new MockWhisper();

        try {
            // Test the generateResponse method with mock data
            String mockTranscription = whisperApi.generateResponse("src/test/java/cse/gradle/APITests.java");

            // Add assertions to verify the correctness of the response
            assertNotNull(mockTranscription);
            assertEquals("Mock Transcription Result", mockTranscription);
        } catch (Exception e) {
            // Handle exceptions or fail the test if an unexpected exception occurs
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testWhisperGenerateResponseWithInvalidFile() {
        // Create an instance of the MockWhisper for testing
        WhisperApi whisperApi = new MockWhisper();

        try {
            // Test the generateResponse method with an invalid file path
            assertThrows(NoSuchFileException.class,
                    () -> whisperApi.generateResponse("nonexistentFilePath"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

}
