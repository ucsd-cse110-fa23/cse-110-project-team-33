package cse.gradle;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javafx.application.Application;
import javafx.stage.Stage;

import cse.gradle.Server.Server;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // start the server
        Server.startServer();

        // initialize relevant classes
        ArrayList<Recipe> arrayList = new ArrayList<Recipe>();

        try {
            // TODO: Move this code to run after the user logs in
            // ArrayList<Recipe> rList = (ArrayList<Recipe>)objectMapper.readValue(response, new TypeReference<List<Recipe>>() {});
            // arrayList = new ArrayList<Recipe>(rList);
        } catch (Exception e) {
            System.out.println("Error reading JSON from server on startup");
        }
        
        // Create a Model object
        Model model = new Model("http://localhost:8100");

        // Create a View object to handle the UI and pass it the controller for button listeners
        View appScenes = new View(primaryStage, arrayList);

        // Create a controller object to mediate between the view and the model
        Controller controller = new Controller(model, appScenes);

        // Set the title of the app
        primaryStage.setTitle("PantryPal");
        // Create scene of mentioned size with the border pane
        primaryStage.setScene(appScenes.getLoginScene());
        // Make window non-resizable
        primaryStage.setResizable(true);
        // Show the app
        primaryStage.show();

        System.out.println("Hello, World!");
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
