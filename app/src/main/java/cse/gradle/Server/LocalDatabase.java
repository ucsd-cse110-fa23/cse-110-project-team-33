package cse.gradle.Server;

import java.util.UUID;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cse.gradle.Recipe;

public class LocalDatabase {

    public static List<Recipe> readLocal() {
        if ((new File("src/main/java/cse/gradle/Server/recipes.csv")).exists()) {
            try {
                BufferedReader reader = new BufferedReader(
                        new FileReader("src/main/java/cse/gradle/Server/recipes.csv"));
                reader.readLine();

                ArrayList<Recipe> recipeList = new ArrayList<>();
                String loadedRecipe;
                while ((loadedRecipe = reader.readLine()) != null) {
                    String[] recipeElems = loadedRecipe.split(";");
                    Recipe recipe = new Recipe(recipeElems[0], recipeElems[1], recipeElems[2], recipeElems[3]);
                    recipeList.add(recipe);
                }
                reader.close();
                return recipeList;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: " + e.getMessage());
                return new ArrayList<Recipe>();
            }
        } else {
            return new ArrayList<Recipe>();
        }
    }

    public static void saveListToLocal(List<Recipe> recipes) {
        try {
            FileWriter writer = new FileWriter("src/main/java/cse/gradle/Server/recipes.csv");
            writer.write("Ingredients" + "," + "Instructions" + "," + "Category" + "," + "Name" + "," + "Id" + "\n");
            for (int i = 0; i < recipes.size(); i++) {
                String recipeIngredients = recipes.get(i).getIngredients();
                String recipeInstructions = recipes.get(i).getInstructions();
                String recipeCategory = recipes.get(i).getCategory();
                String recipeName = recipes.get(i).getName();
                UUID Id = recipes.get(i).getId();

                writer.write(recipeIngredients + "," + recipeInstructions + "," + recipeCategory + "," + recipeName
                        + "," + Id + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRecipeToLocal(Recipe recipe) {
        if ((new File("src/main/java/cse/gradle/Server/recipes.csv")).exists()) {
            try {
                FileWriter writer = new FileWriter("src/main/java/cse/gradle/Server/recipes.csv", true);
                writer.write(recipe.getIngredients() + "," + recipe.getInstructions() + "," + recipe.getCategory() + ","
                        + recipe.getName() + "," + recipe.getId() + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter writer = new FileWriter("src/main/java/cse/gradle/Server/recipes.csv", true);
                writer.write(
                        "Ingredients" + "," + "Instructions" + "," + "Category" + "," + "Name" + "," + "Id" + "\n");
                writer.write(recipe.getIngredients() + "," + recipe.getInstructions() + "," + recipe.getCategory() + ","
                        + recipe.getName() + "," + recipe.getId() + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteRecipeFromLocal(Recipe recipeFromServer) {
        try {
            // create array with every recipe except one to delete
            BufferedReader reader = new BufferedReader(
                        new FileReader("src/main/java/cse/gradle/Server/recipes.csv"));
            String localRecipeString;
            ArrayList<String> newCSVFile = new ArrayList<String>();
            while ((localRecipeString = reader.readLine()) != null) {
                if (localRecipeString.equals(recipeFromServer.toString())) {
                    continue;
                }
                newCSVFile.add(localRecipeString);
            }
            reader.close();

            // remake CSV file with array
            FileWriter writer = new FileWriter("src/main/java/cse/gradle/Server/recipes.csv");
            for (int i = 0; i < newCSVFile.size(); i++) {
                writer.write(newCSVFile.get(i) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
