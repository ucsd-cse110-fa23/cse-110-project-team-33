package cse.gradle;

public class RecipeGenerator {
    
    // relative paths may not work
    private final String MEAL_TYPE_PATH = "mealType.wav";
    private final String INGREDIENTS_PATH = "ingredients.wav";

    private String[] response;

    public RecipeGenerator() {

    }

    public Recipe generateNewRecipe() {
        String mealTypeTranscript = "meal type was never transcribed";
        String ingredientsTranscript = "ingredients was never transcribed";
        
        try {
            // Call performAudioTranscriptionRequest once for both audio files
            String[] transcriptions = Model.performAudioTranscriptionRequest(MEAL_TYPE_PATH, INGREDIENTS_PATH);
            mealTypeTranscript = transcriptions[0];
            ingredientsTranscript = transcriptions[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response = Model.performRecipeGenerationRequest(mealTypeTranscript, ingredientsTranscript);
        
        System.out.println("Title:\n" + response[0]);
        System.out.println("Meal Type:\n" + response[1]);
        System.out.println("Ingredients:\n" + response[2]);
        System.out.println("Instructions:\n" + response[3]);
        Recipe returnRecipe = new Recipe(response[2], response[3], response[1], response[0]);
        
        return returnRecipe;
    }

}
