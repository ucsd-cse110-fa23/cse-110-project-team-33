package cse.gradle;

import java.util.UUID;
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
        Document doc = new Document("_id", new ObjectId());
        newUser.append("userId", userId)
                .append("username", username)
                .append("password", password)
                .append("recipeList", recipeList);
        return doc;
    }
}
