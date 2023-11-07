package cse.project.team;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;


class Feature1Test {
    @Test void assertTest() {
        // Recipe recipe = new Recipe("1","2","3","4");
        // Recipe recipe2 = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        // ArrayList<Recipe> arrayList = new ArrayList<Recipe>();
        
        // arrayList.add(recipe);
        // arrayList.add(recipe2);
        RecipeList rl = new RecipeList();
        assertEquals(rl.getRecipeList().isEmpty(), true);
    }
}
