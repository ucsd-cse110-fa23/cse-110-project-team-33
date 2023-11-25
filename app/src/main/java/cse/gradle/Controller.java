package cse.gradle;

import javafx.scene.Scene;

public class Controller {
    static void setListeners(NewRecipePane recipePane, AppScenes appScenes, Scene cancelScene) {

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
