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
        Recipe rcp = null;

        ObjectMapper objectMapper = new ObjectMapper();
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

        // Update the database right before the app closes
        primaryStage.setOnCloseRequest(e -> {
            // SAVE COMMENTED CODE FOR WHEN WE IMPLEMENT TIER 3 DATABASE
            // updateDatabase(recipeList.getRecipes());
        });

        System.out.println("Hello, World!");
    }

    // SAVE COMMENTED CODE FOR WHEN WE IMPLEMENT TIER 3 DATABASE
    // // TODO: Part of Task 5d
    // @Override
    // public List<Recipe> readDatabase() {
    //     return new ArrayList<Recipe>();
    // }

    // SAVE COMMENTED CODE FOR WHEN WE IMPLEMENT TIER 3 DATABASE
    // // Part of Task 7a
    // @Override
    // public void updateDatabase(List<Recipe> recipes) {
    //     Model model = new Model();
    //     for (Recipe recipe : recipes) {
    //         String response = model.performRequest("PUT", null, recipe);
    //         System.out.println("PUT " + recipe.getName());
    //     }
    // }

    // SAVE COMMENTED CODE FOR WHEN WE IMPLEMENT TIER 3 DATABASE
    // // Part of Task 7a
    // @Override
    // public void deleteFromDatabase(Recipe recipe) {
    //     Model model = new Model();
    //     String response = model.performRequest("DELETE", null, recipe);
    //     System.out.println("DELETE " + recipe.getName());
    // }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
