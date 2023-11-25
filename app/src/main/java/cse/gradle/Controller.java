package cse.gradle;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Controller {
    public static void saveRecipe(AppFramePopUp popUp, Recipe recipe, RecipeList rList) {
        /*
         * private TextField nameField;
         * private TextField categoryField;
         * private TextField ingredientsField;
         * private TextField instructionsField;
         */
        recipe.setName(popUp.getNameField().getText());
        recipe.setCategory(popUp.getCategoryField().getText());
        recipe.setIngredients(popUp.getIngredientsField().getText());
        recipe.setInstructions(popUp.getInstructionsField().getText());

        rList.refresh();

        Model model = new Model();
        String getResponse = model.performRequest("GET", recipe.getId().toString(), null);
        if (getResponse.contains("No recipe found for id ")) {
            model.performRequest("POST", null, recipe);
        } else {
            model.performRequest("PUT", recipe.getId().toString(), recipe);
        }
    }

    public static void deleteRecipe(AppFramePopUp popUp, Recipe recipe, RecipeList rList) {
        Model model = new Model();
        String getResponse = model.performRequest("DELETE", recipe.getId().toString(), null);
        if (!getResponse.contains("No recipe found for id ")) {
            model.performRequest("DELETE", recipe.getId().toString(), null);
            rList.removeButton(recipe);
            rList.refresh();
            Stage current = (Stage) popUp.getScene().getWindow();
            current.close();
            // this.recipeList.refresh();
        }
    }

    static void setListeners(NewRecipePane recipePane, View appScenes, Scene cancelScene) {

        recipePane.getRecordMealTypeButton().setOnAction(e -> {
            if (!recipePane.getRecordingInProgress()) {
                // System.out.println("Pressed RECORD");
                appScenes.getAudioRecorder().startRecording("mealType.wav");
                recipePane.setRecordingInProgress(true);
            }
        });

        recipePane.getStopRecordMealType().setOnAction(e -> {
            if (recipePane.getRecordingInProgress()) {
                appScenes.getAudioRecorder().stopRecording();
                // If nextScene and cancelScene are the same, make a new recipe and add it to
                // RecipeList
                // if(this.nextScene == this.cancelScene){
                // Recipe newRecipe =
                // appScenes.getRecipeListRoot().addButton(newRecipe);
                // }

                System.out.println("stopped recording");
                recipePane.setRecordingInProgress(false);

            }
            // this.appScenes.displayScene(this.nextScene);
            // this.appScenes.getRecipeListRoot().addButton(CreateNewRecipe.generateNewRecipe());
            // this.appScenes.getRecipeListRoot().addButton(new Recipe("a","b","c","d"));
            // this.appScenes.displayScene(this.cancelScene);
        });

        recipePane.getRecordIngredientsButton().setOnAction(e -> {
            if (!recipePane.getRecordingInProgress()) {
                // System.out.println("Pressed RECORD");
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
            // this.appScenes.displayScene(this.nextScene);
            // this.appScenes.getRecipeListRoot().addButton(CreateNewRecipe.generateNewRecipe());
            // this.appScenes.getRecipeListRoot().addButton(new Recipe("a","b","c","d"));
            // this.appScenes.displayScene(this.cancelScene);
        });

        recipePane.getGenerateRecipeButton().setOnAction(e -> {
            // this.appScenes.displayScene(this.nextScene);
            // this.appScenes.getRecipeListRoot().addButton(new
            // RecipeGenerator().generateNewRecipe());
            Recipe newRecipe = new RecipeGenerator().generateNewRecipe();
            appScenes.getRecipeListRoot().addButton(0, newRecipe);
            appScenes.getRecipeListRoot().getRecipes().add(0, newRecipe);
            // this.appScenes.getRecipeListRoot().addButton(new Recipe("a","b","c","d"));
            appScenes.displayScene(cancelScene);
        });

        // Display cancelScene when backButton is pushed
        recipePane.getBackButton().setOnAction(e -> {
            // System.out.println("Cancel pressed");
            appScenes.displayScene(cancelScene);
        });
    }
}
