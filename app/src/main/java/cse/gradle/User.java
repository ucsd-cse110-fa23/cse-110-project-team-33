package cse.gradle;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class User {
    private UUID id;
    private String username;
    private List<Recipe> recipeList;

    public User(String username, List<Recipe>recipeList) {
        this.username = username;
        this.recipeList = recipeList;
        this.id = UUID.randomUUID();
    }

    public String getUsername() {
        return username;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public UUID getId() {
        return id;
    }
}
