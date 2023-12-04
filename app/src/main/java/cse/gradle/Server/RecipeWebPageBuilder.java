package cse.gradle.Server;

import cse.gradle.Recipe;

public class RecipeWebPageBuilder implements WebPageBuilder {

    Recipe recipe = null;
    StringBuilder webpage;

    public RecipeWebPageBuilder(Recipe recipe) {
        this.recipe = recipe;
        this.webpage = new StringBuilder();
    }

    private void appendStyles() {
        webpage.append("<style>\n");
        
        webpage.append("body {\n");
        webpage.append("    font-family: 'Arial', sans-serif;\n");
        webpage.append("    background-color: #f4f4f4;\n");
        webpage.append("    margin: 20px;\n");
        webpage.append("}\n");
        webpage.append("h1, h2 {\n");
        webpage.append("    color: #333;\n");
        webpage.append("}\n");
        webpage.append("p {\n");
        webpage.append("    color: #666;\n");
        webpage.append("}\n");
        webpage.append("ol {\n");
        webpage.append("    list-style-type: decimal;\n");
        webpage.append("    color: #777;\n");
        webpage.append("}\n");

        webpage.append("</style>\n");

    }

    private String buildRecipeDisplay() {
        webpage
            .append("<html>\n")
            .append("<head>\n")
            .append("<title>" + recipe.getName() + "</title>\n");

        // Append styles
        appendStyles();

        webpage
            .append("</head>\n")
            .append("<body>\n");

        // Display the recipe name as the title of the webpage
        webpage.append("<h1>" + recipe.getName() + "</h1>\n");

        // Display the recipe category
        webpage.append("<h2>Category</h2>\n")
            .append("<p>" + recipe.getCategory() + "</p>\n");
        
        // Display the recipe ingredients
        webpage.append("<h2>Ingredients</h2>\n")
            .append("<p>" + recipe.getIngredients() + "</p>\n");

        // Display the recipe instructions
        webpage.append("<h2>Instructions</h2>\n");
        String[] instructions = recipe.getInstructions().split("\\d+\\."); 
        webpage.append("<ol>\n");
        for (int i = 1; i < instructions.length; i++) {
            webpage.append("<li>" + instructions[i] + "</li>\n");
        }

        // Close remaining tags
        webpage.append("</ol>\n")
            .append("</body>\n")
            .append("</html>\n");

        return webpage.toString();
    }


    public String getWebpage() {
        return buildRecipeDisplay();
    }
}