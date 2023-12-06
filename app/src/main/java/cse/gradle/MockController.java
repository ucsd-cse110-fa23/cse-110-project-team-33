package cse.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cse.gradle.View.UserLogin;

public class MockController {
    public boolean loginUser(String username, String password) throws IOException {
        // Checks if the automic login file exists; if it does, and username matches the
        // one in file, do automatic login
        if ((new File("src/main/java/cse/gradle/local/login.txt")).exists()) {
            File loginFile = new File("src/main/java/cse/gradle/local/login.txt");
            BufferedReader reader = new BufferedReader(
                    new FileReader(loginFile));
            if (username.equals(reader.readLine())) {
                reader.close();
                // String postResponse = model.performLoginRequest(loginFile);
                // if (postResponse.equals("Error: Server down")) {
                //     appScenes.displayServerDownConstructor();
                //     return false;
                // }
                // reader.close();
                return true;
            }
            reader.close();
        }
        return false;
    }

}
