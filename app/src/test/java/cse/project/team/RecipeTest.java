/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package cse.project.team;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class RecipeTest {

    @Test
    void RecipeFields() {
        Recipe r = new Recipe("ham and cheese",
                  "put onto pan",
                  "lunch",
                  "grilled cheese but with ham");
        assertEquals(r.getName(), "grilled cheese but with ham");
        assertEquals(r.getIngredients(), "ham and cheese");
        assertEquals(r.getCategory(), "lunch");
        assertEquals(r.getInstructions(), "put onto pan");
    }
}