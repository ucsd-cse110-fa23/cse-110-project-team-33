package cse.gradle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class MockAudioRecorder extends AudioRecorder {

    private boolean isRecordingStarted = false;

    @Override
    public void startRecording(String fileName) {
        if (!isRecordingStarted) {
            isRecordingStarted = true;
            // Simulate the start of recording without actual audio capture
            simulateRecording(fileName);
        }
    }

    @Override
    public void stopRecording() {
        if (isRecordingStarted) {
            // Simulate the stop of recording without actual audio capture
            isRecordingStarted = false;
        }
    }

    // Simulate the recording process without actual audio capture
    private void simulateRecording(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName));
            audioInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
