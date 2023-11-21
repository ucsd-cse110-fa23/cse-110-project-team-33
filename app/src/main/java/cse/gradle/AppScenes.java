package cse.gradle;

import javafx.scene.Scene;
import java.util.List;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;



public class AppScenes {
    private Scene recipeListScene;
    private Scene newRecipeScene;
    private Scene ingredientsInputScene;
    private RecipeList recipeListRoot;
    private AudioRecorder audioRecorder;
    private Stage stage;


    public AppScenes(Stage stage){
        audioRecorder = new AudioRecorder();
        this.stage = stage;
        recipeListRoot = new RecipeList(this);
        recipeListScene = new Scene(recipeListRoot, 500, 600);
        newRecipeSceneConstructor();
        //ingredientsInputSceneConstructor();
    }


    public AppScenes(Stage stage, List<Recipe> arrayList){
        audioRecorder = new AudioRecorder();
        //System.out.println("AppScenes object is made");
        this.stage = stage;
        recipeListRoot = new RecipeList(this, arrayList);
        recipeListScene = new Scene(recipeListRoot, 500, 600);
        newRecipeSceneConstructor();
        //ingredientsInputSceneConstructor();
    }

    private void newRecipeSceneConstructor(){
        NewRecipePane newRecipePane = new NewRecipePane(this, recipeListScene, ingredientsInputScene,
            "Record your preferred meal type, and then record your available ingredients and click generate.");
        newRecipeScene = new Scene(newRecipePane, 500, 600);
    }
/* 
    private void ingredientsInputSceneConstructor(){
        NewRecipePane newRecipePane = new NewRecipePane(this, recipeListScene, recipeListScene, "What ingredients do you have?", "ingredients.wav");
        ingredientsInputScene = new Scene(newRecipePane, 500, 600);
        System.out.println("ingredients input pane made");
    }
*/
    public void displayNewRecipeScene(){
        displayScene(newRecipeScene);
    }

    public void displayScene(Scene s){
        stage.setScene(s);
    }

    public Scene getScene(){
        return recipeListScene;
    }
    
    public RecipeList getRecipeListRoot(){
        return recipeListRoot;
    }

    public AudioRecorder getAudioRecorder(){
        return audioRecorder;
    }
}

class NewRecipePane extends BorderPane{
    private Scene cancelScene;
    private Scene nextScene;
    private AppScenes appScenes;
    private Button recordMealTypeButton;
    private Button stopRecordMealType;
    private Button recordIngedientsButton;
    private Button stopRecordIngredientsButton;
    private Button generateRecipeButton;
    private Button backButton;
    private Label newRecipeLabel;
    private boolean recordingInProgress;
    // private String fileName;


    public NewRecipePane(AppScenes appScenes, Scene cancelScene, Scene nextScene, String prompt){
        this.appScenes = appScenes;
        this.cancelScene = cancelScene;
        this.nextScene = nextScene;
        // this.fileName = fileName;
        recordingInProgress = false;
        //System.out.println("Making new AudioRecorder");
        //audioRecorder = new AudioRecorder(fileName);

        recordMealTypeButton = new Button("Record Meal Type");
        recordMealTypeButton.setPrefSize(100, 20);
        recordMealTypeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        stopRecordMealType = new Button("Stop recording Meal Type");
        stopRecordMealType.setPrefSize(100, 20);
        stopRecordMealType.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");
        
        recordIngedientsButton = new Button("Record Ingredients");
        recordIngedientsButton.setPrefSize(100, 20);
        recordIngedientsButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); 

        stopRecordIngredientsButton = new Button("Stop recording Meal Type");
        stopRecordIngredientsButton.setPrefSize(100, 20);
        stopRecordIngredientsButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); 

        generateRecipeButton = new Button("Generate Recipe");
        generateRecipeButton.setPrefSize(100, 20);
        generateRecipeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); 
       
        backButton = new Button("Back");
        backButton.setPrefSize(100, 20);
        backButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); 

        setListeners();

        //System.out.println("NewRecipePane is made");

        newRecipeLabel = new Label("New Recipe");
        newRecipeLabel.setScaleX(1.5);
        newRecipeLabel.setScaleY(1.5);
        //newRecipeLabel.setAlignment(Pos.CENTER);
        Label promptLabel = new Label(prompt);
        //promptLabel.setAlignment(Pos.CENTER);

        VBox labelsBox = new VBox();
        VBox buttonsBox = new VBox();

        labelsBox.setSpacing(5);
        labelsBox.setAlignment(Pos.BASELINE_CENTER);
        buttonsBox.setSpacing(3);
        buttonsBox.setAlignment(Pos.TOP_CENTER);

        // add new user instructions here
        labelsBox.getChildren().addAll(newRecipeLabel, promptLabel);
        buttonsBox.getChildren().addAll(recordMealTypeButton, stopRecordMealType, recordIngedientsButton,
                                        stopRecordIngredientsButton, generateRecipeButton, backButton);

        this.setTop(labelsBox);
        this.setBottom(buttonsBox);
    }

    void setListeners(){

        recordMealTypeButton.setOnAction(e ->{
            if(!recordingInProgress){
            //System.out.println("Pressed RECORD");
            appScenes.getAudioRecorder().startRecording("mealType.wav");
            recordingInProgress = true;            
            }
        });


        stopRecordMealType.setOnAction(e ->{
            if(recordingInProgress){
            appScenes.getAudioRecorder().stopRecording();
            //If nextScene and cancelScene are the same, make a new recipe and add it to RecipeList
            //if(this.nextScene == this.cancelScene){
                //Recipe newRecipe = 
                //appScenes.getRecipeListRoot().addButton(newRecipe);
            //}
            
            System.out.println("stopped recording");
            recordingInProgress = false;
            
            }
            //this.appScenes.displayScene(this.nextScene);
            // this.appScenes.getRecipeListRoot().addButton(CreateNewRecipe.generateNewRecipe());
            //this.appScenes.getRecipeListRoot().addButton(new Recipe("a","b","c","d"));
            // this.appScenes.displayScene(this.cancelScene);
        });

        recordIngedientsButton.setOnAction(e ->{
            if(!recordingInProgress){
            //System.out.println("Pressed RECORD");
            appScenes.getAudioRecorder().startRecording("ingredients.wav");
            recordingInProgress = true;            
            }
        });

        stopRecordIngredientsButton.setOnAction(e ->{
            if(recordingInProgress){
            appScenes.getAudioRecorder().stopRecording();            
            System.out.println("stopped recording");
            recordingInProgress = false;
            
            }
            //this.appScenes.displayScene(this.nextScene);
            // this.appScenes.getRecipeListRoot().addButton(CreateNewRecipe.generateNewRecipe());
            //this.appScenes.getRecipeListRoot().addButton(new Recipe("a","b","c","d"));
            // this.appScenes.displayScene(this.cancelScene);
        });

        generateRecipeButton.setOnAction(e ->{
            //this.appScenes.displayScene(this.nextScene);
            //this.appScenes.getRecipeListRoot().addButton(new RecipeGenerator().generateNewRecipe());
            Recipe newRecipe = new RecipeGenerator().generateNewRecipe();
            this.appScenes.getRecipeListRoot().addButton(0, newRecipe);
            this.appScenes.getRecipeListRoot().getRecipes().add(0, newRecipe);
            //this.appScenes.getRecipeListRoot().addButton(new Recipe("a","b","c","d"));
            this.appScenes.displayScene(this.cancelScene);
        });

        //Display cancelScene when backButton is pushed
        backButton.setOnAction(e ->{
            //System.out.println("Cancel pressed");
            this.appScenes.displayScene(this.cancelScene);
        });
    }

}
