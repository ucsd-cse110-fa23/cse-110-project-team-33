/*
 * This Java source file was generated by the Gradle 'init' task.
 * 
 * 
 * Functionality: Includes Unit and BDD Scenario Testing for User Story #9: User Account Creation
 */
package cse.gradle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;


class Feature9Tests {
    /* --------------------------------- UNIT TESTS --------------------------------- */

    @Test 
    void createNewUserInvalidUserName() {
        Model mockModel = new MockModel();
        User Joe = new User("test_user", "password");
        String response = mockModel.performRegisterRequest(Joe.getUsername(), Joe.getPassword());
        
        assert(response.contains("Error"));
    }
    /* --------------------------------- BDD TESTS --------------------------------- */
    @Test 
    void createNewUserAndEdit() {
        MockRecipeGenerator mock = new MockRecipeGenerator();
        Recipe r1 = mock.generateNewRecipe("breakfast", "eggs, bacon", "cook for 10 minutes", "American breakfast");
        Recipe r2 = mock.generateNewRecipe("lunch", "salmon, salad", "cook for 20 minutes", "Healthy Lunch");        
        ArrayList<Recipe> rList = new ArrayList<Recipe>();
        rList.add(r1);
        rList.add(r2);
        
        User user = new User("Barry", "123");
        user.setRecipeList(rList);
        assertEquals(2, user.getRecipeList().size());
        
        ArrayList<Recipe> rList2 = new ArrayList<Recipe>();
        rList2.add(r1);
        user.setRecipeList(rList2);
        assertEquals(1, user.getRecipeList().size());        
    }
}
