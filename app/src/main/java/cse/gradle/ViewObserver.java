package cse.gradle;

import cse.gradle.View.AppFramePopUp;
import cse.gradle.View.NewRecipePane;
import cse.gradle.View.RecipeList;
import cse.gradle.View.UserCreateAccount;
import cse.gradle.View.UserLogin;
import javafx.scene.Scene;

public interface ViewObserver {
    public void setRecipeCreationListeners(NewRecipePane recipePane, View appScenes, Scene cancelScene);
    public void setAccountWindowListeners(UserCreateAccount createPane, View appScenes);
    public void setLoginWindowListeners(UserLogin userPane, View appScenes);
    public void setRecipePopUpListeners(View appScenes, AppFramePopUp recipePopUp, RecipeList recipeList, Recipe recipe);
    public void setRecipeListListeners(RecipeList recipeList, View appScenes);
}
