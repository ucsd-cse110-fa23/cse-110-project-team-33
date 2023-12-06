package cse.gradle;

import java.util.List;

import cse.gradle.View.RecipeList;

public class MockView extends View {
    public MockView(List<Recipe> rList) {
        super(rList);
    }

    public List<Recipe> getRecipeList() {
        return this.getListOfRecipes();
    }
}
