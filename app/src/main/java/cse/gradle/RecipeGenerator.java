package cse.gradle;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class RecipeGenerator {
    private String mealTypePath;
    private String ingredientsPath;

    public RecipeGenerator() {
        // relative paths may not work
        mealTypePath = "mealType.wav";
        ingredientsPath = "ingredients.wav";
    }

    public Recipe generateNewRecipe() {
        String mealtypeTranscript = "";
        String ingredientsTranscript = "";
        String ingredientsString = "";
        String titleString = "";
        String instructionsString = "";
        String mealTypeString = "";

        try {
            mealtypeTranscript = Model.useWhisper(mealTypePath);
        } catch(Exception e){
            System.out.println(e);
        }
                 
        try {
            ingredientsTranscript = Model.useWhisper(ingredientsPath);
        } catch(Exception e){
            System.out.println(e);
        }
        
        try {
            System.out.println("Title:");
            titleString = Model.useChatGPT(100, ("Give a 3 to 5 word name for a " + mealTypeString + " recipe using the following ingredients: " + ingredientsTranscript + ". Output nothing but the recipe name."));
            System.out.println("Instructions:");
            instructionsString = Model.useChatGPT(100, ("Give only instructions to make a recipe for a " + mealTypeString + " meal using only the following ingredients: " + ingredientsTranscript + ". Base it on this recipe name: " + titleString + ". Make this concise and within 100 words"));
            //TimeUnit.SECONDS.sleep(12);

            
        } catch(Exception e) {
            System.out.println(e);
            System.out.println("mealtype: " + mealtypeTranscript);
            System.out.println("ingredients: " + ingredientsTranscript);
            System.out.println("instructions: " + instructionsString);
            System.out.println("title: " + titleString);
            Recipe returnRecipe = new Recipe(ingredientsTranscript, instructionsString, mealtypeTranscript, titleString);
            
            return returnRecipe;
        }
        System.out.println("mealtype: " + mealtypeTranscript);
        System.out.println("ingredients: " + ingredientsString);
        System.out.println("instructions: " + instructionsString);
        System.out.println("title: " + titleString);
        Recipe returnRecipe = new Recipe(ingredientsTranscript, instructionsString, mealtypeTranscript, titleString);
        
        return returnRecipe;
    }
}
