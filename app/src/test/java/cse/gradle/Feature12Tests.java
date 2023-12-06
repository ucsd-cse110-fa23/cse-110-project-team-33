/*
 * This Java source file was generated by the Gradle 'init' task.
 * 
 * 
 * Functionality: Includes Unit and BDD Scenario Testing for User Story #_: ___________
 */
package cse.gradle;

import org.junit.jupiter.api.Test;

import cse.gradle.Server.Server;
import cse.gradle.Server.APIs.MockGPT;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;


class Feature12Tests {
    @Test 
    void testRegenerateRecipe() throws Exception {
        MockGPT chGpt = new MockGPT();
        String[] response = chGpt.generateResponse("Lunch","meat, potatoes");
        assertEquals("meat, potatoes", response[2]);
        response = chGpt.generateResponse("Breakfast","bread");
        assertEquals("bread", response[2]);
    }
}
