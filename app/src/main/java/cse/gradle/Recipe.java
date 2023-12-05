package cse.gradle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Recipe {
    private String ingredients;
    private String instructions;
    private String category;
    private String name;
    private Date date;
    private UUID id;

    // empty constructor
    public Recipe() {
        this.ingredients = "";
        this.instructions = "";
        this.category = "";
        this.name = "";
        this.date = new Date(); //date becomes date object is created
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
        this.date = new Date();
        this.id = UUID.randomUUID();
    }

    // constructor with String and Date arguments
    public Recipe(String ingredients,
                  String instructions,
                  String category,
                  String name,
                  Date dateCreated) {
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.category = category;
        this.name = name;
        this.date = dateCreated;
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
        this.date = new Date();
        this.id = id;
    }

    // constructor with String and UUID arguments and Date argument
    public Recipe(String ingredients,
                  String instructions,
                  String category,
                  String name,
                  Date dateCreated,
                  UUID id) {
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.category = category;
        this.name = name;
        this.date = dateCreated;
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

    public Date getDate(){
        return date;
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

    public void setDate(Date newDate){
        this.date = newDate;
    }

    public void setId(UUID newId) {
        id = newId;
    }

    // toString method for saving to file (csv)
    public String toString() {
        return ingredients + "," + instructions + "," + category + "," + name + "," + date + "," + id;
    }

    // toDocument method for saving to database
    public Document toDocument() {
        Document doc = new Document();
        doc.append("ingredients", ingredients);
        doc.append("instructions", instructions);
        doc.append("category", category);
        doc.append("name", name);
        doc.append("date", date.toString());
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
    public static Recipe parseRecipeFromDocument(Document recipe) {

        String recipeString = recipe.toJson().toString();
        return parseRecipeFromString(recipeString);
    }

    // static parse method for populating a List<Recipe> from a JSON string
    public static List<Recipe> parseRecipeListFromString(String json) {
        ArrayList<Recipe> recipeArrayList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            if(jsonNode.isArray()){
                for(final JsonNode objNode : jsonNode){
                    recipeArrayList.add(parseRecipeFromString(objNode.toString()));
                }
            }
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return recipeArrayList;
    }

    /*
     * This method removes all newlines 
     * from the ingredients, category, and name
     * and trims the white space around each field
     */
    public static void clean(Recipe recipe) {
        recipe.setIngredients(recipe.getIngredients().replaceAll("\\r|\\n", "").trim());
        recipe.setCategory(recipe.getCategory().replaceAll("\\r|\\n", "").trim());
        recipe.setName(recipe.getName().replaceAll("\\r|\\n", "").trim()); 
    }

    // static parse method for populating a recipe from a JSON string
    public static Recipe parseRecipeFromString(String json) {
        try {
            // Parse JSON using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            Date tempDate = new Date();
            // DateFormat format = DateFormat.getDateInstance();
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            // Extract individual fields from the JSON
            String ingredients = jsonNode.has("ingredients") ? jsonNode.get("ingredients").asText() : "";
            String instructions = jsonNode.has("instructions") ? jsonNode.get("instructions").asText() : "";
            String category = jsonNode.has("category") ? jsonNode.get("category").asText() : "";
            String name = jsonNode.has("name") ? jsonNode.get("name").asText() : "";
            Date dateCreated = tempDate;
            try {
                dateCreated = jsonNode.has("date") ? format.parse(jsonNode.get("date").asText()) : tempDate;
            } catch (Exception e) {
                System.out.println(e);
            }
            
            UUID id = jsonNode.has("id") ? UUID.fromString(jsonNode.get("id").asText()) : UUID.randomUUID();


            // Create a recipe object
            Recipe recipe = new Recipe(ingredients, instructions, category, name);

            recipe.setDate(dateCreated);
            System.out.println("dateCreated: " + dateCreated);
            // set id
            recipe.setId(id);

            Recipe.clean(recipe);
            
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
               r1.getDate().toString().equals(r2.getDate().toString()) &&
               r1.getId().equals(r2.getId());
    }

    // static method for sorting a list of recipes alphabetically by name (case insensitive)
    public static void sortByName(List<Recipe> recipes, boolean ascending) {
        if (ascending) {
            recipes.sort((r1, r2) -> r2.getName().compareToIgnoreCase(r1.getName()));
        }
        else {
            recipes.sort((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));
        }
        // print recipe names for debugging
        for (Recipe recipe : recipes) {
            System.out.println(recipe.getName());
        }
    }


    public static void sortByDate(List<Recipe> recipes, boolean ascending) {
        if (ascending) {
            recipes.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        }
        else {
            recipes.sort((r1, r2) -> r1.getDate().compareTo(r2.getDate()));
        }
        // print recipe names for debugging
        for (Recipe recipe : recipes) {
            System.out.println(recipe.getName());
            System.out.println("Date: " + recipe.getDate());
        }
    }
 
    // static method for filtering a list of recipes by a meal type (Breakfast, Lunch, Dinner)
    public static void filterByMealType(List<Recipe> recipeList, String mealType) {
        System.out.println("filter is: " + mealType);
        recipeList.removeIf(recipe -> (!recipe.getCategory().toLowerCase().contains(mealType.toLowerCase())));
    }
}
