package cse.gradle;

import java.util.List;

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
        if (postResponse.equals("Error: Server down")) {
            appScenes.displayServerDownConstructor();
        }
    }

    public boolean loginUser(String username, String password, View appScenes) {
        String postResponse = model.performLoginRequest(username, password);
        if (postResponse.equals("Error: Server down")) {
            appScenes.displayServerDownConstructor();
            return false;
        } else if (postResponse.contains("Incorrect password")) {
            appScenes.displayIncorrectPassword();
            return false;
        }
        return true;
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

    // Handles the saving of a recipe in the database caused by the UI save button
    // being pressed
    public void saveRecipe(AppFramePopUp popUp, View appScenes, Recipe recipe, RecipeList rList) {

        try {
            recipe.setName(popUp.getNameField().getText());
            recipe.setCategory(popUp.getCategoryField().getText());
            recipe.setIngredients(popUp.getIngredientsField().getText());
            recipe.setInstructions(popUp.getInstructionsField().getText());

            // Update the recipe in the database
            String response = model.putRecipe(recipe.getId().toString(), recipe);
            System.out.println("save recipe put response: " + response);

            if (response.contains("No recipe found")) {
                // If the recipe was not found, create a new recipe in the database
                response = model.postRecipe(recipe);
                System.out.println("save recipe post response: " + response);
            }

            // Make sure the server isn't down
            if (response.equals("Error: Server down")) {
                throw new Exception(response);
            }

            String filterOption = rList.getMealTypeDropDown().getValue();
            String sortOption = rList.getSortDropDown().getValue();
            syncRecipeListWithModel(appScenes, sortOption, filterOption);
        } catch (Exception e) {
            if (e.getMessage().equals("Error: Server down"))
                appScenes.displayServerDownConstructor();
        }

    }

    // Handles the deletion of a recipe in the database caused by the UI delete
    // button being pressed
    public void deleteRecipe(AppFramePopUp popUp, View appScenes, Recipe recipe, RecipeList rList) {
        try {
            // saveRecipe(popUp, recipe, rList);
            Recipe rcp = null;
            String response = model.deleteRecipe(recipe.getId().toString());

            if (response.equals("Error: Server down")) {
                appScenes.displayServerDownConstructor();
                return;
            }

            String filterOption = rList.getMealTypeDropDown().getValue();
            String sortOption = rList.getSortDropDown().getValue();
            syncRecipeListWithModel(appScenes, sortOption, filterOption);

            Stage current = (Stage) popUp.getScene().getWindow();
            current.close();
        } catch (Exception e) {
            if (e.getMessage().equals("Error: Server down"))
                appScenes.displayServerDownConstructor();
        }

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
            Recipe newRecipe = null;
            try {
                model.performFileWriteRequest("mealType.wav");
                model.performFileWriteRequest("ingredients.wav");
                newRecipe = model.performRecipeGenerateRequest();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            // Save the new recipe to the database
            String postResponse = model.postRecipe(newRecipe);
            System.out.println(postResponse);

            // Update recipeList to reflect the state of the database
            // TODO: Refactor into a method so we can DRY
            String filterOption = appScenes.getRecipeListRoot().getMealTypeDropDown().getValue();
            String sortOption = appScenes.getRecipeListRoot().getSortDropDown().getValue();
            syncRecipeListWithModel(appScenes, sortOption, filterOption);
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
            // When create account, start with default sorted list
            syncRecipeListWithModel(appScenes, Constants.defaultSortOption, Constants.defaultMealType);
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

        // When the login button is pressed, make a request to the server to login the
        // user, then make a get all recipes request and display the recipe list scene
        userPane.getLoginButton().setOnAction(e -> {
            String username = userPane.getUsernameField().getText().toString();
            String password = userPane.getPasswordField().getText().toString();
            boolean loginResult = loginUser(username, password, appScenes);

            // Get all recipes from the database and display
            // When logging into account, start with default sorted list
            if (loginResult) {
                syncRecipeListWithModel(appScenes, Constants.defaultSortOption, Constants.defaultMealType);
            }
        });
    }

    void setListeners(RecipeList recipeList, View appScenes) {
        // button
        recipeList.getNewRecipeButton().setOnAction(e -> {
            appScenes.displayNewRecipeScene();
        });

        recipeList.getLogoutButton().setOnAction(e -> {
            model.userId = null; // erase the userId
            // set the dropdown back to default sort option
            recipeList.getSortDropDown().setValue(Constants.defaultSortOption);
            appScenes.UserLoginConstructor(); // recreate the login screen to clear the username and password fields
            appScenes.displayUserLoginConstructor();
        });

        recipeList.getSortDropDown().setOnAction(event -> {
            String filterOption = recipeList.getMealTypeDropDown().getValue();
            String sortOption = recipeList.getSortDropDown().getValue();
            syncRecipeListWithModel(appScenes, sortOption, filterOption);
        });

        recipeList.getMealTypeDropDown().setOnAction(event -> {
            String filterOption = recipeList.getMealTypeDropDown().getValue();
            String sortOption = recipeList.getSortDropDown().getValue();
            syncRecipeListWithModel(appScenes, sortOption, filterOption);
        });
    }

    private void syncRecipeListWithModel(View appScenes, String sortOption, String filterOption) {
        String getAllResponse = model.getRecipeList(sortOption, filterOption);

        // Make sure the server isn't down
        if (getAllResponse.equals("Error: Server down")) {
            appScenes.displayServerDownConstructor();
        }

        // Get all recipes from the database and display
        List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(getAllResponse);
        appScenes.updateRecipeListView(recipeArrayList);
        appScenes.displayRecipeListScene();
    }
}