package cse.gradle;

import java.util.concurrent.TimeUnit;

public class MockRecipeGenerator extends RecipeGenerator {
    public Recipe generateNewRecipe(String mealTypeString, String ingredientsString, 
                                    String instructionsString, String titleString) {
        System.out.println("mealtype: " + mealTypeString);
        System.out.println("ingredients: " + ingredientsString);
        System.out.println("instructions: " + instructionsString);
        System.out.println("title: " + titleString);
        Recipe returnRecipe = new Recipe(ingredientsString, instructionsString, mealTypeString, titleString);
        return returnRecipe;
    }
}
