package cse.project.team;

import java.util.ArrayList;
import java.util.List;

public class RecipeList {
    private List<Recipe> recipes;
    
    public RecipeList() {
        recipes = new ArrayList<Recipe>();
    }

    public void refresh() {
        for (int i = 0; i < recipes.size(); i++) {
            this.recipes.set(i, this.recipes.get(i));
        }
    }
}
