package cse.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Server.Server;

public class ScalableForMultiPlatformTest {

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        Server server = new Server();
        System.out.println("Server started before running tests.");
    }

    /*
     * --------------------------------- UNIT TESTS ---------------------------------
     */
    @Test
    void postRequestTest() throws JsonMappingException, JsonProcessingException {

        // Create recipie and use model to post it
        Recipe originalRecipe = new Recipe("potatoes", "boil the potatoes", "brunch", "boiled potatoes");
        Model model = new Model();

        // Check that the response is correct
        String response = model.performRequest("POST", null, originalRecipe);
        String expectedResponse = "Posted entry: " +  originalRecipe.toString();
        assertEquals(expectedResponse, response);

        // Check that the recipe was added to the database
        String getResponse = model.performRequest("GET", originalRecipe.getId().toString(), null);

        System.out.println("getResponse: " + getResponse);
        // Convert the response to a Recipe object
        Recipe getRecipe = Recipe.parseRecipeFromString(getResponse);
        // Check that the recipe is the same as the original
        assertEquals(Recipe.equals(originalRecipe, getRecipe), true);


        // Delete the recipe from the database
        String deleteResponse = model.performRequest("DELETE", originalRecipe.getId().toString(), null);
        String expectedDeleteResponse = "Deleted entry for id " + originalRecipe.getId().toString();

        // Check that the response is correct
        assertEquals(expectedDeleteResponse, deleteResponse);

        
    }
    /*
     * --------------------------------- BDD TESTS ---------------------------------
     */
}
