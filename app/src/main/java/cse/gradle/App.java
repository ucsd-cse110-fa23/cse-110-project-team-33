package cse.gradle;

import java.util.ArrayList;
import java.util.List;

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
        Server server = new Server();

        // initialize relevant classes
        Recipe recipe = new Recipe("1","2","3","4");
        Recipe recipe2 = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        System.out.println(recipe2);
        ArrayList<Recipe> arrayList = new ArrayList<Recipe>();
        
        arrayList.add(recipe);
        arrayList.add(recipe2);
        RecipeList recipeList = new RecipeList(arrayList);
        AppFramePopUp appFramePopUp = new AppFramePopUp(recipeList, recipe);


        // DisplayRecipe.DisplayRecipe(appFramePopUp);

        // Setting the Layout of the Window- Should contain a Header, Footer and the TaskList
        RecipeList root = recipeList;

        // Set the title of the app
        primaryStage.setTitle("PantryPal");
        // Create scene of mentioned size with the border pane
        primaryStage.setScene(new Scene(root, 500, 600));
        // Make window non-resizable
        primaryStage.setResizable(false);
        // Show the app
        primaryStage.show();

        // Update the database right before the app closes
        primaryStage.setOnCloseRequest(e -> {
            // SAVE COMMENTED CODE FOR WHEN WE IMPLEMENT TIER 3 DATABASE
            // updateDatabase(recipeList.getRecipes());

            LocalDatabase.saveListToLocal(recipeList.getRecipes());
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
