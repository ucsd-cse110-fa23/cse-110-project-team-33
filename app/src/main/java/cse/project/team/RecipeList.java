package cse.project.team;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;

public class RecipeList extends BorderPane{
    private List<Recipe> recipes;
    private List<Button> buttons;
    private VBox vBox;

    public RecipeList() {
        recipes = new ArrayList<Recipe>();
        buttons = new ArrayList<Button>();
        vBox = new VBox();
        vBox.setPrefSize(500, 20);
        // scrollPane unnecessary for empty list
        // ScrollPane scrollPane = new ScrollPane(vBox);
        // this.setCenter(scrollPane);
        // scrollPane.setFitToHeight(true);
        // scrollPane.setFitToWidth(true);
        // //scrollPane.setPannable(true);
        // scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }

    public RecipeList(List<Recipe> rList) {
        recipes = new ArrayList<Recipe>(rList);
        buttons = new ArrayList<Button>();
        vBox = new VBox();
        vBox.setPrefSize(500, 20);

        for(int i = 0; i < recipes.size(); i++){
            //For each recipe, add new button with title of recipe
            Recipe r = recipes.get(i);
            addButton(r);
        }

        ScrollPane scrollPane = new ScrollPane(vBox);
        this.setCenter(scrollPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        //scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }
    
    public List<Recipe> getRecipeList() {
        return recipes;
    }

    private void addButton(Recipe r){
        String rName = r.getName();
        Button b = new Button(rName);
            buttons.add(b);
            b.setPrefSize(500, 20);
            b.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets style of button
            /* 
            b.setOnAction(e ->{
                DisplayRecipe.DisplayRecipe(new AppFramePopUp(this, r));
            });
            */
            addListener(b, r);
            //add button to vBox
            vBox.getChildren().add(b);
    }

    private void addListener(Button b, Recipe r){
        b.setOnAction(e ->{
                DisplayRecipe.DisplayRecipe(new AppFramePopUp(this, r));
            });
    }

    public void refresh() {
        for (int i = 0; i < recipes.size(); i++) {
            //this.recipes.set(i, this.recipes.get(i));
            this.buttons.get(i).setText(recipes.get(i).getName());
        }

    }
}