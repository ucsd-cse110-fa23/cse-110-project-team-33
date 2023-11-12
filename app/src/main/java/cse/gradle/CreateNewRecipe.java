package cse.gradle;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateNewRecipe {
    CreateNewRecipe(){
    }

    public static Recipe generateNewRecipe(){
        String mealtypeTranscript = "";
        String ingredientsTranscript = "";

        String ingredientsString = "";
        String titleString = "";
        String instructionsString = "";
        String mealTypeString = "";
        
        String currentFile = "C:/Users/puppy/Documents/Classes/Fall_2023/CSE_110/Project/cse-110-project-team-33/app/src/main/java/cse/gradle/CreateNewRecipe.java";
        //String mealTypeFile = "C:\\Users\\puppy\\Documents\\Classes\\Fall_2023\\CSE_110\\Project\\cse-110-project-team-33\\app\\mealtype.wav";
        String ingredientsFile = "C:/Users/puppy/Documents/Classes/Fall_2023/CSE_110/Project/cse-110-project-team-33/app/ingredients.wav";

        Path currentPath = Paths.get(currentFile);
        //Path mealTypePath = Paths.get(mealTypeFile);
        Path ingredientsPath = Paths.get(ingredientsFile);

        //Path relativeMealPath = currentPath.relativize(mealTypePath);
        Path relativeIngredientsPath = currentPath.relativize(ingredientsPath);

        /*
        try{
        mealtypeTranscript = Model.useWhisper(relativeMealPath.toString());
        } catch(Exception e){
            System.out.println(e);
        }
         */

        try{
        ingredientsTranscript = Model.useWhisper("C:\\Users\\puppy\\Documents\\Classes\\Fall_2023\\CSE_110\\Project\\cse-110-project-team-33\\app\\ingredients.wav");
        } catch(Exception e){
            System.out.println(e);
        }
        
        
        try{
            System.out.println("Ingredients:");
            ingredientsString = Model.useChatGPT(100, ("Parse out just the ingredients from the following phrases and list them separated by commas. Do not say anything else: " + ingredientsTranscript));
            System.out.println("Instructions:");
            instructionsString = Model.useChatGPT(100, ("Give brief instructions to make a recipe from the following ingredients. Only list the instructions, nothing else: " + ingredientsString));
            System.out.println("Title:");
            titleString =  Model.useChatGPT(100, ("Give a name to the following recipe. Only list the name, nothing else: " + instructionsString));
        } catch(Exception e){
            System.out.println(e);
        }
        Recipe returnRecipe = new Recipe(ingredientsString, instructionsString, mealTypeString, titleString);
        
        return returnRecipe;
    }
}
