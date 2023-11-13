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

        // Path currentPath = Paths.get(currentFile);
        // //Path mealTypePath = Paths.get(mealTypeFile);
        // Path ingredientsPath = Paths.get(ingredientsFile);

        // //Path relativeMealPath = currentPath.relativize(mealTypePath);
        // Path relativeIngredientsPath = currentPath.relativize(ingredientsPath);

        /*
        try{
        mealtypeTranscript = Model.useWhisper(relativeMealPath.toString());
        } catch(Exception e){
            System.out.println(e);
        }
         */
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
            // System.out.println("Meal Type:");
            // mealTypeString = Model.useChatGPT(100, ("Parse out the meal type from the following phrases. Do not say anything else: " + mealtypeTranscript));
            
            
            // System.out.println("Ingredients:");
            // ingredientsString = Model.useChatGPT(100, ("Parse out just the ingredients from the following phrase and list them separated by commas, nothing else" + ingredientsTranscript));
            // TimeUnit.SECONDS.sleep(5);




            System.out.println("Instructions:");
            instructionsString = Model.useChatGPT(100, ("Give only instructions to make a recipe for a " + mealTypeString + " meal using only the following ingredients: " + ingredientsString + ". Make this concise and within 100 words"));
            TimeUnit.SECONDS.sleep(12);

            System.out.println("Title:");
            titleString = Model.useChatGPT(100, ("Give a 3-word name to the recipe the follow recipe you created: " + instructionsString));


            // System.out.println("Title refined:");
            // titleString = Model.useChatGPT(100, ("Give me a title for the following meal: " + titleString));
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
