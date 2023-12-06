package cse.gradle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.io.*;

import cse.gradle.View.AppFramePopUp;
import cse.gradle.View.NewRecipePane;
import cse.gradle.View.RecipeList;
import cse.gradle.View.UserCreateAccount;
import cse.gradle.View.UserLogin;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

public class Controller implements ModelObserver, ViewObserver {

    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        model.register(this);
        view.register(this);
    }

    public void createUser(String username, String password, View appScenes) {
        String postResponse = model.performRegisterRequest(username, password);
        if (postResponse.equals("Error: Server down")) {
            appScenes.displayServerDownConstructor();
        }
    }

    public boolean loginUser(String username, String password, View appScenes, UserLogin loginPage) throws IOException {
        // Checks if the automic login file exists; if it does, and username matches the
        // one in file, do automatic login
        if ((new File("src/main/java/cse/gradle/local/login.txt")).exists()) {
            File loginFile = new File("src/main/java/cse/gradle/local/login.txt");
            BufferedReader reader = new BufferedReader(
                    new FileReader(loginFile));
            if (username.equals(reader.readLine())) {
                reader.close();
                String postResponse = model.performLoginRequest(loginFile);
                if (postResponse.equals("Error: Server down")) {
                    appScenes.displayServerDownConstructor();
                    return false;
                }
                reader.close();
                return true;
            }
            reader.close();
            return loginUserWithButtonCheck(username, password, appScenes, loginPage);
        } else {
            return loginUserWithButtonCheck(username, password, appScenes, loginPage);
        }
    }

    // Logs-in the user and writes to login.txt file if the automatic login checkbox
    // is selected
    public boolean loginUserWithButtonCheck(String username, String password, View appScenes, UserLogin loginPage) {
        String postResponse = model.performLoginRequest(username, password);
        if ((loginPage.getAutoLoginButton().isSelected()) && (!postResponse.contains("Invalid"))) {
            try {
                FileWriter writer = new FileWriter("src/main/java/cse/gradle/local/login.txt");
                writer.write(username + "\n" + password);
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (postResponse.equals("Error: Server down")) {
            appScenes.displayServerDownConstructor();
            return false;
        } else if (postResponse.contains("Incorrect password")) {
            appScenes.displayIncorrectPassword();
            return false;
        } else if (postResponse.contains("does not exist")) {
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
    public void setRecipeCreationListeners(NewRecipePane recipePane, View appScenes, Scene cancelScene) {

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
            // TODO: replace with using Model
            Recipe newRecipe = new RecipeGenerator().generateNewRecipe();

            // Save the new recipe to the database
            String postResponse = model.postRecipe(newRecipe);

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
    public void setAccountWindowListeners(UserCreateAccount createPane, View appScenes) {

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
    public void setLoginWindowListeners(UserLogin loginPage, View appScenes) {

        loginPage.getCreateButton().setOnAction(e -> {
            appScenes.displayUserAccountSceneConstructor();
        });

        // When the login button is pressed, make a request to the server to login the
        // user
        // then make a get all recipes request and display the recipe list scene
        loginPage.getLoginButton().setOnAction(e -> {
            String username = loginPage.getUsernameField().getText().toString();
            String password = loginPage.getPasswordField().getText().toString();
            boolean loginResult;
            try {
                loginResult = loginUser(username, password, appScenes, loginPage);
                // Get all recipes from the database and display
                // When logging into account, start with default sorted list
                if (loginResult) {
                    syncRecipeListWithModel(appScenes, Constants.defaultSortOption, Constants.defaultMealType);
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
    }

    // Sets the listensers for all buttons within the AppFramePopUp window
    public void setRecipePopUpListeners(AppFramePopUp recipePopUp, RecipeList recipeList, Recipe recipe) {

        recipePopUp.getSaveButton().setOnAction(e -> {
            this.saveRecipe(recipePopUp, recipeList.appScenes, recipe, recipeList);
        });

        recipePopUp.getDeleteButton().setOnAction(e -> {
            this.deleteRecipe(recipePopUp, recipeList.appScenes, recipe, recipeList);
        });
        recipePopUp.getShareButton().setOnAction(e -> {
            this.shareRecipe(recipe);
        });
    }

    public void setRecipeListListeners(RecipeList recipeList, View appScenes) {
        // button
        recipeList.getNewRecipeButton().setOnAction(e -> {
            appScenes.displayNewRecipeScene();
            System.out.println("New Recipe pressed");
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

        // recipeList.getMealTypeChoiceBox().getSelectionModel().selectedIndexProperty().addListener(
        // (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
        // // store selected mealtype
        // recipeList.getFilterButton().setOnAction(e -> {
        // String chosenMeal = (recipeList.getMealTypes()[new_val.intValue()]);

        // });

        // });
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