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
    private RecipeList recipeListRoot;
    private Stage stage;


    public AppScenes(Stage stage){
        this.stage = stage;
        recipeListRoot = new RecipeList(this);
        recipeListScene = new Scene(recipeListRoot, 500, 600);
        newRecipeSceneConstructor();
    }


    public AppScenes(Stage stage, List<Recipe> arrayList){
        this.stage = stage;
        recipeListRoot = new RecipeList(this, arrayList);
        recipeListScene = new Scene(recipeListRoot, 500, 600);
        newRecipeSceneConstructor();
    }

    private void newRecipeSceneConstructor(){
        NewRecipePane newRecipePane = new NewRecipePane(this, recipeListScene, "Please input a meal type.");
        newRecipeScene = new Scene(newRecipePane, 500, 600);
    }

    public void displayNewRecipeScene(){
        displayScene(newRecipeScene);
    }

    public void displayScene(Scene s){
        stage.setScene(s);
    }

    public Scene getScene(){
        return recipeListScene;
    }
}

class NewRecipePane extends BorderPane{
    private Scene cancelScene;
    private AppScenes appScenes;
    private Button cancelButton;
    private Button recordButton;
    private Label newRecipeLabel;

    public NewRecipePane(AppScenes appScenes, Scene cancelScene, String prompt){
        this.appScenes = appScenes;
        this.cancelScene = cancelScene;

        recordButton = new Button("Record");
        recordButton.setPrefSize(100, 20);
        recordButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        cancelButton = new Button("Cancel");
        cancelButton.setPrefSize(100, 20);
        cancelButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); 

        //Display cancelScene when cancelButton is pushed
        cancelButton.setOnAction(e ->{
            this.appScenes.displayScene(this.cancelScene);
        });

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

        labelsBox.getChildren().addAll(newRecipeLabel, promptLabel);
        buttonsBox.getChildren().addAll(recordButton, cancelButton);

        this.setTop(labelsBox);
        this.setBottom(buttonsBox);
    }

}