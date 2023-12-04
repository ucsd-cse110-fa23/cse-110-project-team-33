package cse.gradle;

public class RecipeGenerator {
    private String mealTypePath = "";
    private String ingredientsPath = "";
    private String[] response;

    public RecipeGenerator() {
        // relative paths may not work
        mealTypePath = "mealType.wav";
        ingredientsPath = "ingredients.wav";
    }

    public Recipe generateNewRecipe() {
        String mealtypeTranscript = "meal type was never transcribed";
        String ingredientsTranscript = "ingredients was never transcribed";

        try {
            mealtypeTranscript = Model.useWhisper(mealTypePath);
        } catch(Exception e){
            e.printStackTrace();
        }
                 
        try {
            ingredientsTranscript = Model.useWhisper(ingredientsPath);
        } catch(Exception e){
            e.printStackTrace();
        }
        
        response = Model.performRecipeGenerationRequest(mealtypeTranscript, ingredientsTranscript);
        
        System.out.println("Title:\n" + response[0]);
        System.out.println("Meal Type:\n" + response[1]);
        System.out.println("Ingredients:\n" + response[2]);
        System.out.println("Instructions:\n" + response[3]);
        Recipe returnRecipe = new Recipe(response[2], response[3], response[1], response[0]);
        
        return returnRecipe;
    }
}
