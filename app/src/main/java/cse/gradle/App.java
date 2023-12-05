package cse.gradle;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Server.Server;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import cse.gradle.Database;
import cse.gradle.Server.LocalDatabase;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // start the server
        Server.startServer();

        // initialize relevant classes
        ArrayList<Recipe> arrayList = new ArrayList<Recipe>();

        // Create a Model object
        Model model = new Model("http://localhost:8100");

        // Create a controller object to mediate between the view and the model
        Controller controller = new Controller(model);

        // Create a View object to handle the UI and pass it the controller for button listeners
        View appScenes = new View(primaryStage, arrayList, controller);

        // Set the title of the app
        primaryStage.setTitle("PantryPal");
        // Create scene of mentioned size with the border pane
        primaryStage.setScene(appScenes.getScene());
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
