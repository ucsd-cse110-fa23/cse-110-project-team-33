/*
* 
 * Functionality: Includes Unit and BDD Scenario Testing for All Features 
 * that require using the HTTP Server
 */

package cse.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Server.LocalDatabase;
import cse.gradle.Server.Server;
import java.util.List;

public class Feature8Tests {

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        Server server = new Server();
        System.out.println("Server started before running tests.");
    }

    /*
     * --------------------------------- UNIT TESTS ---------------------------------
     */
    @Test
    void fullCRUDTest() throws JsonMappingException, JsonProcessingException {

        // Create recipe and use model to POST it
        Recipe originalRecipe = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        Model model = new Model();

        // Check that the response is correct
        String response = model.performRequest("POST", null, originalRecipe);
        String expectedResponse = "Posted entry: " +  originalRecipe.toString();
        assertEquals(expectedResponse, response);

        // Check that the recipe was added to the database with GET
        String getResponse = model.performRequest("GET", originalRecipe.getId().toString(), null);
        Recipe getRecipe = Recipe.parseRecipeFromString(getResponse);
        assertEquals(Recipe.equals(originalRecipe, getRecipe), true);

        // Check that we can update the recipe with PUT
        Recipe updatedRecipe = new Recipe("potatoes", "boil the potatoes", "brunch", "mashed potatoes");
        updatedRecipe.setId(originalRecipe.getId());
        
        String putResponse = model.performRequest("PUT", originalRecipe.getId().toString(), updatedRecipe);
        String expectedPutResponse = "Updated entry for id " + originalRecipe.getId().toString() + ". New recipe: " + updatedRecipe.toString();
        assertEquals(expectedPutResponse, putResponse);

        // Check that the recipe was updated in the database GET
        getResponse = model.performRequest("GET", originalRecipe.getId().toString(), null);
        getRecipe = Recipe.parseRecipeFromString(getResponse);
        assertEquals(Recipe.equals(updatedRecipe, getRecipe), true);


        // Delete the recipe from the database
        String deleteResponse = model.performRequest("DELETE", originalRecipe.getId().toString(), null);
        String expectedDeleteResponse = "Deleted entry for id " + originalRecipe.getId().toString();

        // Check that the response is correct
        assertEquals(expectedDeleteResponse, deleteResponse);
        
    }

    @Test
    void updateEditDeleteCSVTest() {
        
        // Create recipe and use model to POST it, then check the csv file to see if it was added properly
        Recipe originalRecipe = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        Model model = new Model();
        String response = model.performRequest("POST", null, originalRecipe);
        List<Recipe> readRecipes = LocalDatabase.readLocal();
        assertEquals(Recipe.equals(originalRecipe, readRecipes.get(readRecipes.size() - 1)), true);

        // update recipe and put request it, then check the csv file to see if it was updated properly
        originalRecipe.setIngredients("tomaotes");
        response = model.performRequest("PUT", originalRecipe.getId().toString(), originalRecipe);
        readRecipes = LocalDatabase.readLocal();
        assertEquals(Recipe.equals(originalRecipe, readRecipes.get(readRecipes.size() - 1)), true);
        
        // delete recipe and delete request it, then check the csv file to see if it was deleted properly
        response = model.performRequest("DELETE", originalRecipe.getId().toString(), null);
        readRecipes = LocalDatabase.readLocal();
        assertEquals(readRecipes.contains(originalRecipe), false);

    }

    /* --------------------------------- BDD TESTS --------------------------------- */
    @Test
    void getAllTest() throws JsonMappingException, JsonProcessingException {
        Recipe recipe1 = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        Recipe recipe2 = new Recipe("cheese", "boil the cheese", "breakfast", "boiled cheese");
        ArrayList<Recipe> rList = new ArrayList<Recipe>();
        rList.add(recipe1);
        rList.add(recipe2);

        Model model = new Model();
        for (int i = 0; i < rList.size(); i++) {
            model.performRequest("POST", null, rList.get(i));
        }

        String response = model.performRequest("GET", null, null);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Recipe> rList2 = (ArrayList<Recipe>)objectMapper.readValue(response, new TypeReference<List<Recipe>>() {});
        for (int i = 0; i < rList2.size(); i++) {
            assertEquals(true, Recipe.equals(rList.get(i), rList2.get(i)));
            model.performRequest("DELETE", rList.get(i).getId().toString(), null);
        }   
    }

}
