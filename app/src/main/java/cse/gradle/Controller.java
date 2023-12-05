package cse.gradle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

public class Controller {

    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void createUser(String username, String password, View appScenes) {
        String postResponse = model.performRegisterRequest(username, password);
        if(postResponse.equals("Error: Server down")){
                appScenes.displayServerDownConstructor();
        }
    }

    public void loginUser(String username, String password, View appScenes) {
        String postResponse = model.performLoginRequest(username, password);
        if(postResponse.equals("Error: Server down")){
                appScenes.displayServerDownConstructor();
                return;
        }
        System.out.println("login response: " + postResponse);
    }


    // Handles the share button being pressed by the user
    public void shareRecipe(Recipe recipe) {

        // Get the share link from the model 
        String shareLink = model.getShareLink(recipe.getId().toString());

        // Copy the share link to the user's clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(shareLink);
        clipboard.setContent(content);

        System.out.println("Share link copied to clipboard: " + shareLink);
    }

    // Handles the saving of a recipe in the database caused by the UI save button being pressed
    public void saveRecipe(AppFramePopUp popUp, View appScenes, Recipe recipe, RecipeList rList) {

            recipe.setName(popUp.getNameField().getText());
            recipe.setCategory(popUp.getCategoryField().getText());
            recipe.setIngredients(popUp.getIngredientsField().getText());
            recipe.setInstructions(popUp.getInstructionsField().getText());

            // Update the recipe in the database
            String putResponse = model.performRecipeRequest("PUT", recipe.getId().toString(), recipe); 
            if(putResponse.equals("Error: Server down")){
                appScenes.displayServerDownConstructor();
                return;
            } 
            System.out.println("save recipe put response: " + putResponse);

            if (putResponse.contains("No recipe found")) {
                // If the recipe was not found, create a new recipe in the database
                String postResponse = model.performRecipeRequest("POST", null, recipe);
            }

            // Update recipeList to reflect the state of the database
            String getAllResponse = model.performRecipeRequest("GET", null, null);
            if(getAllResponse.equals("Error: Server down")){
                appScenes.displayServerDownConstructor();
                return;
            }
            List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(getAllResponse);
            appScenes.setRecipeListRoot(recipeArrayList);
            appScenes.displayRecipeListScene();

    }

    // Handles the deletion of a recipe in the database caused by the UI delete button being pressed
    public void deleteRecipe(AppFramePopUp popUp, View appScenes, Recipe recipe, RecipeList rList) {
        // saveRecipe(popUp, recipe, rList);
        Recipe rcp = null;
        String getResponse = model.performRecipeRequest("DELETE", recipe.getId().toString(), rcp);
        if(getResponse.equals("Error: Server down")){
            appScenes.displayServerDownConstructor();
            return;
        }

        // Update recipeList to reflect the state of the database
        getResponse = model.performRecipeRequest("GET", null, null);
        if(getResponse.equals("Error: Server down")){
            appScenes.displayServerDownConstructor();
            return;
        }

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
            createUser(username, password, appScenes);

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
            loginUser(username, password, appScenes);

            // Get all recipes from the database and display
            String response = model.performRecipeRequest("GET", null, null);
            List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(response);
            appScenes.setRecipeListRoot(recipeArrayList);
            appScenes.displayRecipeListScene();
        });
    }

    // TODO: Replace this method with an API call to the HTTP server using Model
    void handleRegenerateButton(AppFramePopUp popUp, View appScenes, Recipe recipe, RecipeList rList){
        Recipe newRecipe = new RecipeGenerator().regenerateRecipe(recipe);
        popUp.getNameField().setText(newRecipe.getName());
        popUp.getCategoryField().setText(newRecipe.getCategory());
        popUp.getIngredientsField().setText(newRecipe.getIngredients());
        popUp.getInstructionsField().setText(newRecipe.getInstructions());
        rList.refresh();
    }
}
