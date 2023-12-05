package cse.gradle;

import java.util.UUID;

import org.bson.Document;

import java.util.ArrayList;

import java.util.List;

public class User {
    private UUID userId;
    private String username;
    private String password;
    private List<Recipe> recipeList;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.recipeList = new ArrayList<Recipe>();
        this.userId = UUID.randomUUID();
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

    // toDocument method for saving to database
    public Document toDocument() {
        Document doc = new Document();
        doc.append("username", username);
        doc.append("password", password);
        doc.append("userId", userId.toString());
        doc.append("recipeList", recipeList);
        return doc;
    }
}
