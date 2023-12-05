package cse.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Feature17Tests extends HTTPServerTests {
    @Test
    public void filterByMealType() {
        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes = new ArrayList<Recipe>();
        
        recipes.add(new Recipe("eggs, bacon", "cook for 10 minutes", "Breakfast", "American breakfast"));
        recipes.add(new Recipe("salmon, salad", "cook for 20 minutes", "Lunch", "Healthy Lunch"));
        recipes.add(new Recipe("potatoes", "boil the potatoes", "Dinner", "boiled potatoes"));

        List<Recipe> recipes1 = new ArrayList<>(recipes);
        Recipe.filterByMealType(recipes1, "Breakfast");

        // Check that only the breakfast meal is shown
        assertEquals(recipes1.size(), 1);
        assert(recipes1.get(0).getCategory().equals("Breakfast"));

        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes2 = new ArrayList<Recipe>(recipes);

        Recipe.filterByMealType(recipes2, "Lunch");

        // Check that only the lunch meal is shown
        assertEquals(recipes2.size(), 1);
        assert(recipes2.get(0).getCategory().equals("Lunch"));

        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes3 = new ArrayList<Recipe>(recipes);

        Recipe.filterByMealType(recipes3, "Dinner");

        // Check that only the dinner meal is shown
        assertEquals(recipes3.size(), 1);
        assert(recipes3.get(0).getCategory().equals("Dinner"));
    }

    @Test
    public void filterWithoutMealType() {
        // Create a new List<Recipe> with 3 recipes
        List<Recipe> recipes = new ArrayList<Recipe>();
        
        recipes.add(new Recipe("eggs, bacon", "cook for 10 minutes", "Breakfast", "American breakfast"));
        recipes.add(new Recipe("potatoes", "boil the potatoes", "Dinner", "boiled potatoes"));
        recipes.add(new Recipe("salmon, salad", "cook for 20 minutes", "Lunch", "Healthy Lunch"));

        List<Recipe> recipes1 = new ArrayList<>(recipes);
        Recipe.filterByMealType(recipes1, "");

        // Check that only the breakfast meal is shown
        assertEquals(recipes1.size(), 3);
        assert(recipes.get(0).getName().equals("American breakfast"));
        assert(recipes.get(1).getName().equals("boiled potatoes"));
        assert(recipes.get(2).getName().equals("Healthy Lunch"));
    }
}
