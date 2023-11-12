package cse.gradle;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateNewRecipe {
    CreateNewRecipe(){
    }

    public Recipe generateNewRecipe(){
        String mealtypeTranscript = "";
        String ingredientsTranscript = "";
        
        String currentFile = "C:\\Users\\puppy\\Documents\\Classes\\Fall_2023\\CSE_110\\Project\\cse-110-project-team-33\\app\\src\\main\\java\\cse\\gradle\\CreateNewRecipe.java";
        String mealTypeFile = "C:\\Users\\puppy\\Documents\\Classes\\Fall_2023\\CSE_110\\Project\\cse-110-project-team-33\\app\\mealtype.wav";
        String ingredientsFile = "C:\\Users\\puppy\\Documents\\Classes\\Fall_2023\\CSE_110\\Project\\cse-110-project-team-33\\app\\ingredients.wav";

        Path currentPath = Paths.get(currentFile);
        Path mealTypePath = Paths.get(mealTypeFile);
        Path ingredientsPath = Paths.get(ingredientsFile);

        Path relativeMealPath = currentPath.relativize(mealTypePath);
        Path relativeIngredientsPath = currentPath.relativize(ingredientsPath);

        try{
        mealtypeTranscript = Model.useWhisper(relativeMealPath.toString());
        } catch(Exception e){
            System.out.println(e);
        }

        try{
        ingredientsTranscript = Model.useWhisper(relativeIngredientsPath.toString());
        } catch(Exception e){
            System.out.println(e);
        }
        
        return null;
    }
}
