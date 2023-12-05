/*
* 
 * Functionality: Includes Unit and BDD Scenario Testing for All Features 
 * that require using the HTTP Server
 */

package cse.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bson.Document;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Server.MongoDB;
import java.util.List;

public class Feature8Tests extends HTTPServerTests{

    /*
     * --------------------------------- UNIT TESTS ---------------------------------
     */
    @Test
    void fullCRUDTest() throws JsonMappingException, JsonProcessingException {

        // Create recipe and use model to POST it
        Recipe originalRecipe = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");

         // MockModel is a mock of Model that accesses a test user in the database without requiring a login
        Model model = new MockModel();

        // Check that the response is correct
        String response = model.postRecipe(originalRecipe);
        String expectedResponse = "Posted entry: " +  originalRecipe.toString();
        assertEquals(expectedResponse, response);

        // Check that the recipe was added to the database with GET
        String getResponse = model.getRecipe(originalRecipe.getId().toString());
        Recipe getRecipe = Recipe.parseRecipeFromString(getResponse);
        assertEquals(Recipe.equals(originalRecipe, getRecipe), true);

        // Check that we can update the recipe with PUT
        Recipe updatedRecipe = new Recipe("potatoes", "boil the potatoes", "brunch", "mashed potatoes");
        updatedRecipe.setId(originalRecipe.getId());
        
        String putResponse = model.putRecipe(originalRecipe.getId().toString(), updatedRecipe);
        String expectedPutResponse = "Updated entry: " + originalRecipe.toString() + " with: " + updatedRecipe.toString();
        assertEquals(expectedPutResponse, putResponse);

        // Check that the recipe was updated in the database GET
        getResponse = model.getRecipe(originalRecipe.getId().toString());
        getRecipe = Recipe.parseRecipeFromString(getResponse);
        assertEquals(Recipe.equals(updatedRecipe, getRecipe), true);


        // Delete the recipe from the database
        String deleteResponse = model.deleteRecipe(originalRecipe.getId().toString());
        String expectedDeleteResponse = "Deleted entry: " + updatedRecipe.toString();

        // Check that the response is correct
        assertEquals(expectedDeleteResponse, deleteResponse);
    }


    @Test
    void getAllTest() throws JsonMappingException, JsonProcessingException {
        Recipe recipe1 = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        Recipe recipe2 = new Recipe("cheese", "boil the cheese", "breakfast", "boiled cheese");
        ArrayList<Recipe> rList = new ArrayList<Recipe>();      // original list of recipes                 
        rList.add(recipe1);
        rList.add(recipe2);

        Model model = new MockModel();
        for (int i = 0; i < rList.size(); i++) {
            model.postRecipe(rList.get(i));                     // posting list of recipes 
        }

        String response = model.getRecipeList();
        List<Recipe> rList2 = Recipe.parseRecipeListFromString(response);   // fetching recipes from database

        for (int i = 0; i < rList2.size(); i++) {
            System.out.println("rList.get(i) " + rList.get(i));
            System.out.println("rList2.get(i) " + rList2.get(i));
            model.deleteRecipe(rList.get(i).getId().toString());
            assertEquals(true, Recipe.equals(rList.get(i), rList2.get(i)));
        }   
    }

    @Test
    void mongoDBQueryTest() {
        // Note: recipes_db is for testing purposes only, the real database will be users_db 
        // where each user has a list of recipes
        MongoDB mongoDB = new MongoDB("mongodb+srv://trevor:cse110@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority", "recipe_db", "recipes");
        mongoDB.connect();

        // mongoDB.collection.insertOne(new Document().append("test", "test"));

        // insert a recipe
        Recipe recipe = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        mongoDB.insertOne(recipe.toDocument());

        // find the recipe
        Document result = mongoDB.findOne("id", recipe.getId().toString());
        Recipe resultRecipe = Recipe.parseRecipeFromDocument(result);
        System.out.println("resultRecipe: " + resultRecipe);
        System.out.println("originalRecipe: " + recipe);
        assertEquals(Recipe.equals(recipe, resultRecipe), true);

        // update the recipe
        recipe.setCategory("breakfast");
        mongoDB.updateOne("id", recipe.getId().toString(), recipe.toDocument());

        // find the updated recipe
        result = mongoDB.findOne("id", recipe.getId().toString());
        resultRecipe = Recipe.parseRecipeFromDocument(result);
        assertEquals(Recipe.equals(recipe, resultRecipe), true);

        // delete the recipe
        mongoDB.deleteOne("id", recipe.getId().toString());

        // find the deleted recipe
        result = mongoDB.findOne("id", recipe.getId().toString());
        assertEquals(result, null);
    }

}
