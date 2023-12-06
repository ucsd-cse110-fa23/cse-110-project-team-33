package cse.gradle;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;

public class Feature16Tests extends HTTPServerTests {

    @Test
    public void sortByDateMethodTest() throws InterruptedException {
        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes = new ArrayList<Recipe>();
        
        recipes.add(new Recipe("eggs, bacon", "cook for 10 minutes", "breakfast", "American breakfast", new Date()));
        Thread.sleep(1000);
        recipes.add(new Recipe("salmon, salad", "cook for 20 minutes", "lunch", "Healthy Lunch", new Date()));
        Thread.sleep(1000);
        recipes.add(new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes", new Date()));

        Recipe.sortByDate(recipes, false);

        // Check that the recipes are sorted oldest to newest
        assert(recipes.get(0).getName().equals("American breakfast"));
        assert(recipes.get(1).getName().equals("Healthy Lunch"));
        assert(recipes.get(2).getName().equals("boiled potatoes"));

        Recipe.sortByDate(recipes, true);

        // Check that the recipes are sorted newest to oldest
        assert(recipes.get(0).getName().equals("boiled potatoes"));
        assert(recipes.get(1).getName().equals("Healthy Lunch"));
        assert(recipes.get(2).getName().equals("American breakfast"));
    }

    @Test
    public void getRecipeListOldToNewTest() throws InterruptedException {

        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes = new ArrayList<Recipe>();
        
        recipes.add(new Recipe("eggs, bacon", "cook for 10 minutes", "breakfast", "American breakfast", new Date()));
        Thread.sleep(1000);
        recipes.add(new Recipe("salmon, salad", "cook for 20 minutes", "lunch", "Healthy Lunch", new Date()));
        Thread.sleep(1000);
        recipes.add(new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes", new Date()));

        // Create a new Mock Model that accesses test_user in the database
        Model model = new MockModel();

        // Add the recipes to the database
        for (Recipe recipe : recipes) {
            model.postRecipe(recipe);
        }

        // Get all the recipes from the database sorted oldest to newest
        String getAllResponse = model.getRecipeList("oldest-newest", "");

        // Delete the recipes from the database
        for (Recipe recipe : recipes) {
            model.deleteRecipe(recipe.getId().toString());
        }

        // Parse the getAllResponse into a List<Recipe>
        List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(getAllResponse);

        System.out.println("recipeArrayList: " + recipeArrayList);
        
        // Check that the recieved recipes are sorted alphabetically A-Z (not case sensitive)
        assertEquals(3, recipeArrayList.size());
        assert(recipes.get(0).getName().equals("American breakfast"));
        assert(recipes.get(1).getName().equals("Healthy Lunch"));
        assert(recipes.get(2).getName().equals("boiled potatoes"));

    }
}