package cse.project.team;

public class Recipe {
    private String ingredients;
    private String instructions;
    private String category;
    private String name;

    // empty constructor
    public Recipe() {
        this.ingredients = "";
        this.instructions = "";
        this.category = "";
        this.name = "";
    }
    
    // constructor with arguments
    public Recipe(String ingredients,
                  String instructions,
                  String category,
                  String name) {
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.category = category;
        this.name = name;
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
}
