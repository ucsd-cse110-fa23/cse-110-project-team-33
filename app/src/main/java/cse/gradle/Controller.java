package cse.gradle;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {

    public static void createUser(String username, String password) {
        Model model = new Model();
        String postResponse = model.registerUser(username, password);
    }

    // Handles the saving of a recipe in the database caused by the UI save button being pressed
    public static void saveRecipe(AppFramePopUp popUp, UUID userId, Recipe recipe) {
        recipe.setName(popUp.getNameField().getText());
        recipe.setCategory(popUp.getCategoryField().getText());
        recipe.setIngredients(popUp.getIngredientsField().getText());
        recipe.setInstructions(popUp.getInstructionsField().getText());

        Model model = new Model();
        String postResponse = model.performRequest("PUT", userId.toString(), recipe);
    }

    // Handles the deletion of a recipe in the database caused by the UI delete button being pressed
    public static void deleteRecipe(AppFramePopUp popUp, Recipe recipe, RecipeList rList) {
        // saveRecipe(popUp, recipe, rList);
        Model model = new Model();
        Recipe rcp = null;
        String getResponse = model.performRequest("DELETE", recipe.getId().toString(), rcp);
        if (!getResponse.contains("No recipe found for id ")) {
            model.performRequest("DELETE", recipe.getId().toString(), rcp);
            rList.removeButton(recipe);
            rList.refresh();
            Stage current = (Stage) popUp.getScene().getWindow();
            current.close();
        }
    }

    // Sets the listensers for all the buttons within the recipe creation window
    static void setListeners(NewRecipePane recipePane, View appScenes, Scene cancelScene) {

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
            appScenes.displayScene(cancelScene);
        });

        // Display cancelScene when backButton is pushed
        recipePane.getBackButton().setOnAction(e -> {
            appScenes.displayScene(cancelScene);
        });
    }

    // Sets the listensers for all the buttons within the recipe creation window
    static void setListeners(UserCreateAccount createPane, View appScenes) {

        createPane.getCreateButton().setOnAction(e -> {
            String username = createPane.getUsernameField().getText().toString();
            String password = createPane.getPasswordField().getText().toString();
            createUser(username, password);
            appScenes.displayRecipeListScene();
        });    

        // Display cancelScene when backButton is pushed
        createPane.getBackButton().setOnAction(e -> {
            appScenes.displayUserLoginConstructor();
        });
    }

    // Sets the listensers for all the buttons within the recipe creation window
    static void setListeners(UserLogin userPane, View appScenes) {

        userPane.getCreateButton().setOnAction(e -> {
            appScenes.displayUserAccountSceneConstructor();
        });    

        // Display cancelScene when backButton is pushed
        userPane.getLoginButton().setOnAction(e -> {
            appScenes.displayRecipeListScene();
        });
    }
}
