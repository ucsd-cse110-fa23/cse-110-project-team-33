package cse.gradle;

import java.util.UUID;

import org.bson.Document;

import java.util.List;

public class User {
    private UUID userId;
    private String username;
    private String password;
    private List<Recipe> recipeList;

    public User(String username, String password, List<Recipe> recipeList) {
        this.username = username;
        this.password = password;
        this.recipeList = recipeList;
        this.userId = UUID.randomUUID();
    }

    // toDocument method for saving to or querying database
    public Document toDocument() {
        /**
         * TO DO (see commented out code for reference)
         */
        Document doc = new Document();
        // doc.append("ingredients", ingredients);
        // doc.append("instructions", instructions);
        // doc.append("category", category);
        // doc.append("name", name);
        // doc.append("id", id.toString());
        return doc;
    }

    // parse method for populating a recipe from a database document
    public static Recipe parseRecipeFromDocument(Document result) {
        Recipe recipe = new Recipe();
        recipe.setIngredients(result.getString("ingredients"));
        recipe.setInstructions(result.getString("instructions"));
        recipe.setCategory(result.getString("category"));
        recipe.setName(result.getString("name"));
        recipe.setId(UUID.fromString(result.getString("id")));
        return recipe;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public String toString() {
        return "Username: " + username + "\nPassword: " + password + "\nUserID: " + userId.toString();
    }
}
