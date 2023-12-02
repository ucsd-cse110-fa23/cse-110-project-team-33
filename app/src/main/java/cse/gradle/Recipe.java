package cse.gradle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Recipe {
    private String ingredients;
    private String instructions;
    private String category;
    private String name;
    private UUID id;

    // static parse method for populating a List<Recipe> from a JSON string
    public static List<Recipe> parseRecipeListFromString(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<Recipe> recipeArrayList = (ArrayList<Recipe>)objectMapper.readValue(json, new TypeReference<List<Recipe>>() {});

            return recipeArrayList;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // static parse method for populating a recipe from a JSON string
    public static Recipe parseRecipeFromString(String json) {
        try {
            // Parse JSON using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            // Extract individual fields from the JSON
            String ingredients = jsonNode.has("ingredients") ? jsonNode.get("ingredients").asText() : "";
            String instructions = jsonNode.has("instructions") ? jsonNode.get("instructions").asText() : "";
            String category = jsonNode.has("category") ? jsonNode.get("category").asText() : "";
            String name = jsonNode.has("name") ? jsonNode.get("name").asText() : "";
            UUID id = jsonNode.has("id") ? UUID.fromString(jsonNode.get("id").asText()) : UUID.randomUUID();

            // Create a recipe object
            Recipe recipe = new Recipe(ingredients, instructions, category, name);
            // set id
            recipe.setId(id);

            return recipe;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // static equals method for comparing two recipes
    public static boolean equals(Recipe r1, Recipe r2) {
        return r1.getIngredients().equals(r2.getIngredients()) &&
               r1.getInstructions().equals(r2.getInstructions()) &&
               r1.getCategory().equals(r2.getCategory()) &&
               r1.getName().equals(r2.getName()) &&
               r1.getId().equals(r2.getId());
    }

    // empty constructor
    public Recipe() {
        this.ingredients = "";
        this.instructions = "";
        this.category = "";
        this.name = "";
        this.id = UUID.randomUUID();
    }
    
    // constructor with String arguments
    public Recipe(String ingredients,
                  String instructions,
                  String category,
                  String name) {
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.category = category;
        this.name = name;
        this.id = UUID.randomUUID();
    }

    // constructor with String and UUID arguments
    public Recipe(String ingredients,
                  String instructions,
                  String category,
                  String name,
                  UUID id) {
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.category = category;
        this.name = name;
        this.id = id;
    }

    // getters
    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
    public UUID getId() {
        return id;
    }

    // setters
    public void setIngredients(String newIngredients) {
        ingredients = newIngredients;
    }

    public void setInstructions(String newInstructions) {
        instructions = newInstructions;
    }

    public void setCategory(String newCategory) {
        category = newCategory;
    }

    public void setName(String newName) {
        name = newName;
    }
    public void setId(UUID newId) {
        id = newId;
    }

    // toString method for saving to file (csv)
    public String toString() {
        return ingredients + "," + instructions + "," + category + "," + name + "," + id;
    }

    // toDocument method for saving to database
    public Document toDocument() {
        Document doc = new Document();
        doc.append("ingredients", ingredients);
        doc.append("instructions", instructions);
        doc.append("category", category);
        doc.append("name", name);
        doc.append("id", id.toString());
        return doc;
    }

    // parse method for populating a recipe from a csv line
    public void populateRecipeFromString(String csvLine) {
        String[] splitLine = csvLine.split(",");
        ingredients = splitLine[0];
        instructions = splitLine[1];
        category = splitLine[2];
        name = splitLine[3];
        id = UUID.fromString(splitLine[4]);
    }

    // parse method for populating a recipe from a database document
    public static Recipe parseRecipeFromDocument(Document result) {

        Recipe recipe = new Recipe();

        try {
            recipe.setIngredients(result.getString("ingredients"));
            recipe.setInstructions(result.getString("instructions"));
            recipe.setCategory(result.getString("category"));
            recipe.setName(result.getString("name"));
            recipe.setId(UUID.fromString(result.getString("id")));
        } catch (Exception e) {
            e.printStackTrace();
        }
      
        return recipe;
    }
}
