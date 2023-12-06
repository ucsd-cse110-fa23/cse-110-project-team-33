/*
 * This Java source file was generated by the Gradle 'init' task.
 * 
 * 
 * Functionality: Includes Unit and BDD Scenario Testing for Feature #14: Meal Type Tags
 */
package cse.gradle;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import cse.gradle.Server.MongoDB;
import cse.gradle.Server.Server;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


class Feature14Tests extends HTTPServerTests {
    /* --------------------------------- UNIT TESTS --------------------------------- */
    @Test
    void editingTypeTags() {
        Recipe r1 = new Recipe("eggs, bacon", "cook for 10 minutes", "breakfast", "American breakfast");
        Recipe r2 = new Recipe("salmon, salad", "cook for 20 minutes", "lunch", "Healthy Lunch");
        Recipe r3 = new Recipe("potatoes", "boil the potatoes", "dinner", "boiled potatoes");
        List<Recipe> rList = new ArrayList<>();
        rList.add(r1);
        rList.add(r2);
        rList.add(r3);

        assertEquals(rList.get(0).getCategory(), "breakfast");
        assertEquals(rList.get(1).getCategory(), "lunch");
        assertEquals(rList.get(2).getCategory(), "dinner");
    }
    

    /* --------------------------------- BDD TESTS --------------------------------- */
    @Test
    void manipulatingTypeTagsInView() {
        Recipe r1 = new Recipe("eggs, bacon", "cook for 10 minutes", "breakfast", "American breakfast");
        Recipe r2 = new Recipe("salmon, salad", "cook for 20 minutes", "lunch", "Healthy Lunch");
        Recipe r3 = new Recipe("potatoes", "boil the potatoes", "dinner", "boiled potatoes");
        List<Recipe> rList = new ArrayList<>();
        rList.add(r1);
        rList.add(r2);
        rList.add(r3);
        
        MockView view = new MockView(rList);
        List<Recipe> rList1 = view.getListOfRecipes();
        
        assertEquals(rList1.get(0).getCategory(), "breakfast");
        assertEquals(rList1.get(1).getCategory(), "lunch");
        assertEquals(rList1.get(2).getCategory(), "dinner");
    }
}