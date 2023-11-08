import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class AppFramePopUp extends BorderPane{
    private Recipe recipe;
    private RecipeList recipeList;
    
    private Button saveButton;
    private HBox buttonsBox;

    private VBox vBox;
    private TextField nameField;
    private TextField categoryField;
    private TextField ingredientsField;
    private TextField instructionsField;

    // empty constructor
    // initialize pop up window here
    public AppFramePopUp(RecipeList rList) {
        this.recipe = new Recipe();
        this.recipeList = rList;

        createFrame();
    }

    // constructor with arguments
    // initialize pop up window here
    public AppFramePopUp(RecipeList rList, Recipe recipe) {
        this.recipe = recipe;
        this.recipeList = rList;

        createFrame();
    }

    private void createFrame(){
        nameField = new TextField(recipe.getName());
        categoryField = new TextField(recipe.getCategory());
        ingredientsField = new TextField(recipe.getIngredients());
        instructionsField = new TextField(recipe.getInstructions());
        
        vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefSize(500, 20); // sets size 
        vBox.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 0; -fx-font-weight: bold;"); // sets background color 

        nameField.setPrefSize(500, 20); // set size of text field
        nameField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of texfield
        nameField.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the text field

        Label nameLabel = new Label();
        nameLabel.setText("Recipe name: "); // create index label
        nameLabel.setPrefSize(300, 20); // set size of Index label
        nameLabel.setTextAlignment(TextAlignment.LEFT); // Set alignment of index label
        nameLabel.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the task

        vBox.getChildren().add(nameLabel); // add index label to task
        vBox.getChildren().add(nameField); // add textlabel 


        categoryField.setPrefSize(500, 20); // set size of text field
        categoryField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of texfield
        categoryField.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the text field

        Label categoryLabel = new Label();
        categoryLabel.setText("Meal type: "); // create index label
        categoryLabel.setPrefSize(300, 20); // set size of Index label
        categoryLabel.setTextAlignment(TextAlignment.LEFT); // Set alignment of index label
        categoryLabel.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the task

        vBox.getChildren().add(categoryLabel); // add index label to task
        vBox.getChildren().add(categoryField); // add textlabel 


        ingredientsField.setPrefSize(500, 20); // set size of text field
        ingredientsField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of texfield
        ingredientsField.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the text field

        Label ingredientsLabel = new Label();
        ingredientsLabel.setText("Ingredients: "); // create index label
        ingredientsLabel.setPrefSize(300, 20); // set size of Index label
        ingredientsLabel.setTextAlignment(TextAlignment.LEFT); // Set alignment of index label
        ingredientsLabel.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the task

        vBox.getChildren().add(ingredientsLabel); // add index label to task
        vBox.getChildren().add(ingredientsField); // add textlabel 


        instructionsField.setPrefSize(500, 20); // set size of text field
        instructionsField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of texfield
        instructionsField.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the text field

        Label instructionsLabel = new Label();
        instructionsLabel.setText("Instructions: "); // create index label
        instructionsLabel.setPrefSize(300, 20); // set size of Index label
        instructionsLabel.setTextAlignment(TextAlignment.LEFT); // Set alignment of index label
        instructionsLabel.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the task

        vBox.getChildren().add(instructionsLabel); // add index label to task
        vBox.getChildren().add(instructionsField); // add textlabel 



        this.setCenter(vBox);



        //Creating buttons
        buttonsBox = new HBox();

        saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-style: italic; -fx-background-color: #FFFFFF;  -fx-font-weight: bold; -fx-font: 20 arial;");

        buttonsBox.getChildren().add(saveButton);
        buttonsBox.setAlignment(Pos.CENTER);

        this.setBottom(buttonsBox);

        saveButton.setOnAction(e -> {
            saveRecipe();
        });
    }

    public void saveRecipe() {
        /*
        private TextField nameField;
        private TextField categoryField;
        private TextField ingredientsField;
        private TextField instructionsField;
        */
        this.recipe.setName(nameField.getText());
        this.recipe.setCategory(categoryField.getText());
        this.recipe.setIngredients(ingredientsField.getText());
        this.recipe.setInstructions(instructionsField.getText());
        
        this.recipeList.refresh();
    }

}
