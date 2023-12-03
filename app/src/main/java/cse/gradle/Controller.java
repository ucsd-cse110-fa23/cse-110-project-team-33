package cse.gradle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {

    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void createUser(String username, String password) {
        String postResponse = model.performRegisterRequest(username, password);
    }

    public void loginUser(String username, String password) {
        String postResponse = model.performLoginRequest(username, password);
    }

    // Handles the saving of a recipe in the database caused by the UI save button being pressed
    public void saveRecipe(AppFramePopUp popUp, View appScenes, Recipe recipe, RecipeList rList) {

            recipe.setName(popUp.getNameField().getText());
            recipe.setCategory(popUp.getCategoryField().getText());
            recipe.setIngredients(popUp.getIngredientsField().getText());
            recipe.setInstructions(popUp.getInstructionsField().getText());

            // Update the recipe in the database
            String putResponse = model.performRecipeRequest("PUT", recipe.getId().toString(), recipe);  

            if (putResponse.contains("No recipe found")) {
                // If the recipe was not found, create a new recipe in the database
                String postResponse = model.performRecipeRequest("POST", null, recipe);
            }

            // Update recipeList to reflect the state of the database
            String getAllResponse = model.performRecipeRequest("GET", null, null);
            List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(getAllResponse);
            appScenes.setRecipeListRoot(recipeArrayList);
            appScenes.displayRecipeListScene();

    }

    // Handles the deletion of a recipe in the database caused by the UI delete button being pressed
    public void deleteRecipe(AppFramePopUp popUp, View appScenes, Recipe recipe, RecipeList rList) {
        // saveRecipe(popUp, recipe, rList);
        Recipe rcp = null;
        String getResponse = model.performRecipeRequest("DELETE", recipe.getId().toString(), rcp);

        // Update recipeList to reflect the state of the database
        getResponse = model.performRecipeRequest("GET", null, null);
        List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(getResponse);
        appScenes.setRecipeListRoot(recipeArrayList);
        appScenes.displayRecipeListScene();
        
        Stage current = (Stage) popUp.getScene().getWindow();
        current.close();

    }

    // Sets the listensers for all the buttons within the recipe creation window
    void setListeners(NewRecipePane recipePane, View appScenes, Scene cancelScene) {

        recipePane.getRecordMealTypeButton().setOnAction(e -> {
            if (!recipePane.getRecordingInProgress()) {
                appScenes.getAudioRecorder().startRecording("mealType.wav");
                recipePane.setRecordingInProgress(true);
            }
        });

        recipePane.getStopRecordMealType().setOnAction(e -> {
            if (recipePane.getRecordingInProgress()) {
                appScenes.getAudioRecorder().stopRecording();
                System.out.println("stopped recording");
                recipePane.setRecordingInProgress(false);

            }
        });

        recipePane.getRecordIngredientsButton().setOnAction(e -> {
            if (!recipePane.getRecordingInProgress()) {
                appScenes.getAudioRecorder().startRecording("ingredients.wav");
                recipePane.setRecordingInProgress(true);
            }
        });

        recipePane.getStopRecordIngredientsButton().setOnAction(e -> {
            if (recipePane.getRecordingInProgress()) {
                appScenes.getAudioRecorder().stopRecording();
                System.out.println("stopped recording");
                recipePane.setRecordingInProgress(false);

            }
        });

        recipePane.getGenerateRecipeButton().setOnAction(e -> {
            Recipe newRecipe = new RecipeGenerator().generateNewRecipe();
            appScenes.getRecipeListRoot().getRecipes().add(0, newRecipe);
            appScenes.getRecipeListRoot().addButton(0, newRecipe);
            appScenes.getRecipeListRoot().refresh();
            appScenes.displayRecipeListScene();
        });

        // Display cancelScene when backButton is pushed
        recipePane.getBackButton().setOnAction(e -> {
            appScenes.displayRecipeListScene();
        });
    }

    // Sets the listensers for all the buttons within the account creation window
    void setListeners(UserCreateAccount createPane, View appScenes) {

        createPane.getCreateButton().setOnAction(e -> {
            String username = createPane.getUsernameField().getText().toString();
            String password = createPane.getPasswordField().getText().toString();
            createUser(username, password);

            // Get all recipes from the database and display
            String response = model.performRecipeRequest("GET", null, null);
            List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(response);
            appScenes.setRecipeListRoot(recipeArrayList);
            appScenes.displayRecipeListScene();
        });    

        // Display cancelScene when backButton is pushed
        createPane.getBackButton().setOnAction(e -> {
            appScenes.displayUserLoginConstructor();
        });
    }

    // Sets the listensers for all the buttons within the account login window
    void setListeners(UserLogin userPane, View appScenes) {

        userPane.getCreateButton().setOnAction(e -> {
            appScenes.displayUserAccountSceneConstructor();
        });    

        // When the login button is pressed, make a request to the server to login the user
        // then make a get all recipes request and display the recipe list scene
        userPane.getLoginButton().setOnAction(e -> {
            String username = userPane.getUsernameField().getText().toString();
            String password = userPane.getPasswordField().getText().toString();
            loginUser(username, password);

            // Get all recipes from the database and display
            String response = model.performRecipeRequest("GET", null, null);
            List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(response);
            appScenes.setRecipeListRoot(recipeArrayList);
            appScenes.displayRecipeListScene();
        });
    }
}
