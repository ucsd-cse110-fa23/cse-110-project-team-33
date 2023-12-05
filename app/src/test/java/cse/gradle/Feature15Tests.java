package cse.gradle;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

public class Feature15Tests extends HTTPServerTests {

    @Test
    public void sortByNameMethodTest() {
        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes = new ArrayList<Recipe>();
        
        recipes.add(new Recipe("eggs, bacon", "cook for 10 minutes", "breakfast", "American breakfast"));
        recipes.add(new Recipe("salmon, salad", "cook for 20 minutes", "lunch", "Healthy Lunch"));
        recipes.add(new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes"));

        Recipe.sortByName(recipes, false);

        // Check that the recipes are sorted alphabetically A-Z (not case sensitive)
        assert(recipes.get(0).getName().equals("American breakfast"));
        assert(recipes.get(1).getName().equals("boiled potatoes"));
        assert(recipes.get(2).getName().equals("Healthy Lunch"));

        Recipe.sortByName(recipes, true);

        // Check that the recipes are sorted alphabetically Z-A (not case sensitive)
        assert(recipes.get(0).getName().equals("Healthy Lunch"));
        assert(recipes.get(1).getName().equals("boiled potatoes"));
        assert(recipes.get(2).getName().equals("American breakfast"));
    }

    @Test
    public void getRecipeListAToZTest() {

        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes = new ArrayList<Recipe>();
        
        recipes.add(new Recipe("eggs, bacon", "cook for 10 minutes", "breakfast", "American breakfast"));
        recipes.add(new Recipe("salmon, salad", "cook for 20 minutes", "lunch", "Healthy Lunch"));
        recipes.add(new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes"));


        // Create a new Mock Model that accesses test_user in the database
        Model model = new MockModel();

        // Add the recipes to the database
        for (Recipe recipe : recipes) {
            model.postRecipe(recipe);
        }

        // Get all the recipes from the database sorted alphabetically A-Z (not case sensitive)
        String getAllResponse = model.getRecipeList("a-z", "");

        // Delete the recipes from the database
        for (Recipe recipe : recipes) {
            model.deleteRecipe(recipe.getId().toString());
        }

        // Parse the getAllResponse into a List<Recipe>
        List<Recipe> recipeArrayList = Recipe.parseRecipeListFromString(getAllResponse);

        System.out.println("recipeArrayList: " + recipeArrayList);
        
        // Check that the recieved recipes are sorted alphabetically A-Z (not case sensitive)
        assertEquals(3, recipeArrayList.size());
        assert(recipeArrayList.get(0).getName().equals("American breakfast"));
        // System.out.println(recipeArrayList.get(1).getName());
        assert(recipeArrayList.get(1).getName().equals("boiled potatoes"));
        assert(recipeArrayList.get(2).getName().equals("Healthy Lunch"));

    }
}