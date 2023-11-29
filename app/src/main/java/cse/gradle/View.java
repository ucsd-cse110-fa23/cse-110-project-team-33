package cse.gradle;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class View {
    private Scene recipeListScene;
    private Scene newRecipeScene;
    private Scene ingredientsInputScene;
    private Scene userAcccountScene;
    private Scene mainLoginScene;
    private RecipeList recipeListRoot;
    private AudioRecorder audioRecorder;
    private Stage stage;

    public View(Stage stage) {
        audioRecorder = new AudioRecorder();
        this.stage = stage;
        recipeListRoot = new RecipeList(this);
        UserLoginConstructor();
        UserAccountSceneConstructor();
        newRecipeSceneConstructor();
    }

    public View(Stage stage, List<Recipe> arrayList) {
        audioRecorder = new AudioRecorder();
        this.stage = stage;
        recipeListRoot = new RecipeList(this, arrayList);
        newRecipeListSceneConstructor();
        UserLoginConstructor();
        UserAccountSceneConstructor();
        newRecipeSceneConstructor();
    }

    private void newRecipeListSceneConstructor(){
        recipeListScene = new Scene(recipeListRoot, 500, 600);
    }

    public void displayRecipeListScene() {
        displayScene(recipeListScene);
    }

    private void newRecipeSceneConstructor() {
        NewRecipePane newRecipePane = new NewRecipePane(this, recipeListScene, ingredientsInputScene,
                "Record your preferred meal type, and then record your available ingredients and click generate.");
        newRecipeScene = new Scene(newRecipePane, 500, 600);
    }

    public void displayNewRecipeScene() {
        displayScene(newRecipeScene);
    }

    public void UserAccountSceneConstructor(){
        UserCreateAccount userCreateAccount = new UserCreateAccount(this);
        userAcccountScene = new Scene(userCreateAccount, 500, 600);
    }

    public void displayUserAccountSceneConstructor(){
        displayScene(userAcccountScene);
    }

     public void UserLoginConstructor(){
        UserLogin userLoginAccount = new UserLogin(this);
        mainLoginScene = new Scene(userLoginAccount, 500, 600);
    }

    public void displayUserLoginConstructor(){
        displayScene(mainLoginScene);
    }

    public void displayScene(Scene s) {
        stage.setScene(s);
    }

    public Scene getScene() {
        return mainLoginScene;
    }

    public RecipeList getRecipeListRoot() {
        return recipeListRoot;
    }

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }
}

class NewRecipePane extends BorderPane {
    private Scene cancelScene;
    private Scene nextScene;
    private View appScenes;
    private Button recordMealTypeButton;
    private Button stopRecordMealType;
    private Button recordIngredientsButton;
    private Button stopRecordIngredientsButton;
    private Button generateRecipeButton;
    private Button backButton;
    private Label newRecipeLabel;
    private boolean recordingInProgress;

    public NewRecipePane(View appScenes, Scene cancelScene, Scene nextScene, String prompt) {
        this.appScenes = appScenes;
        this.cancelScene = cancelScene;
        this.nextScene = nextScene;
        recordingInProgress = false;

        recordMealTypeButton = new Button("Record Meal Type");
        recordMealTypeButton.setPrefSize(100, 20);
        recordMealTypeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        stopRecordMealType = new Button("Stop recording Meal Type");
        stopRecordMealType.setPrefSize(100, 20);
        stopRecordMealType.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        recordIngredientsButton = new Button("Record Ingredients");
        recordIngredientsButton.setPrefSize(100, 20);
        recordIngredientsButton
                .setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        stopRecordIngredientsButton = new Button("Stop recording Meal Type");
        stopRecordIngredientsButton.setPrefSize(100, 20);
        stopRecordIngredientsButton
                .setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        generateRecipeButton = new Button("Generate Recipe");
        generateRecipeButton.setPrefSize(100, 20);
        generateRecipeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        backButton = new Button("Back");
        backButton.setPrefSize(100, 20);
        backButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        Controller.setListeners(this, appScenes, cancelScene);

        newRecipeLabel = new Label("New Recipe");
        newRecipeLabel.setScaleX(1.5);
        newRecipeLabel.setScaleY(1.5);
        Label promptLabel = new Label(prompt);

        VBox labelsBox = new VBox();
        VBox buttonsBox = new VBox();

        labelsBox.setSpacing(5);
        labelsBox.setAlignment(Pos.BASELINE_CENTER);
        buttonsBox.setSpacing(3);
        buttonsBox.setAlignment(Pos.TOP_CENTER);

        // add new user instructions here
        labelsBox.getChildren().addAll(newRecipeLabel, promptLabel);
        buttonsBox.getChildren().addAll(recordMealTypeButton, stopRecordMealType, recordIngredientsButton,
                stopRecordIngredientsButton, generateRecipeButton, backButton);

        this.setTop(labelsBox);
        this.setBottom(buttonsBox);
    }

    public Button getRecordMealTypeButton() {
        return recordMealTypeButton;
    }

    public Button getStopRecordMealType() {
        return stopRecordMealType;
    }

    public Button getRecordIngredientsButton() {
        return recordIngredientsButton;
    }

    public Button getStopRecordIngredientsButton() {
        return stopRecordIngredientsButton;
    }

    public Button getGenerateRecipeButton() {
        return generateRecipeButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public boolean getRecordingInProgress() {
        return recordingInProgress;
    }

    public void setRecordingInProgress(boolean recordingInProgress) {
        this.recordingInProgress = recordingInProgress;
    }

}

class AppFramePopUp extends BorderPane {
    private Recipe recipe;
    private RecipeList recipeList;

    private Button saveButton;
    private Button deleteButton;
    private HBox buttonsBox;

    private VBox vBox;
    private TextField nameField;
    private TextField categoryField;
    private TextField ingredientsField;
    private TextArea instructionsField;

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

    private void createFrame() {
        nameField = new TextField(recipe.getName());
        categoryField = new TextField(recipe.getCategory());
        ingredientsField = new TextField(recipe.getIngredients());
        instructionsField = new TextArea(recipe.getInstructions());

        vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefSize(500, 20); // sets size
        vBox.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 0; -fx-font-weight: bold;"); // sets background
                                                                                                     // color

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
        categoryField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of
                                                                                       // texfield
        categoryField.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the text field

        Label categoryLabel = new Label();
        categoryLabel.setText("Meal type: "); // create index label
        categoryLabel.setPrefSize(300, 20); // set size of Index label
        categoryLabel.setTextAlignment(TextAlignment.LEFT); // Set alignment of index label
        categoryLabel.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the task

        vBox.getChildren().add(categoryLabel); // add index label to task
        vBox.getChildren().add(categoryField); // add textlabel

        ingredientsField.setPrefSize(500, 20); // set size of text field
        ingredientsField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of
                                                                                          // texfield
        ingredientsField.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the text field

        Label ingredientsLabel = new Label();
        ingredientsLabel.setText("Ingredients: "); // create index label
        ingredientsLabel.setPrefSize(300, 20); // set size of Index label
        ingredientsLabel.setTextAlignment(TextAlignment.LEFT); // Set alignment of index label
        ingredientsLabel.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the task

        vBox.getChildren().add(ingredientsLabel); // add index label to task
        vBox.getChildren().add(ingredientsField); // add textlabel

        instructionsField.setPrefSize(500, 100); // set size of text field
        instructionsField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of
                                                                                           // texfield
        instructionsField.setWrapText(true);
        ScrollPane scrollPane = new ScrollPane(instructionsField);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        Label instructionsLabel = new Label();
        instructionsLabel.setText("Instructions: "); // create index label
        instructionsLabel.setPrefSize(300, 20); // set size of Index label
        instructionsLabel.setTextAlignment(TextAlignment.LEFT); // Set alignment of index label
        instructionsLabel.setPadding(new Insets(10, 0, 10, 0)); // adds some padding to the task

        vBox.getChildren().add(instructionsLabel); // add index label to task
        vBox.getChildren().add(scrollPane); // add textlabel

        this.setCenter(vBox);

        // Creating buttons
        buttonsBox = new HBox();

        saveButton = new Button("Save");
        saveButton.setStyle(
                "-fx-font-style: italic; -fx-background-color: #FFFFFF;  -fx-font-weight: bold; -fx-font: 20 arial;");

        deleteButton = new Button("Delete");
        deleteButton.setStyle(
                "-fx-font-style: italic; -fx-background-color: #FFFFFF;  -fx-font-weight: bold; -fx-font: 20 arial;");

        buttonsBox.getChildren().add(saveButton);
        buttonsBox.getChildren().add(deleteButton);
        buttonsBox.setAlignment(Pos.CENTER);

        this.setBottom(buttonsBox);

        saveButton.setOnAction(e -> {
            Controller.saveRecipe(this, recipe, recipeList);
        });

        deleteButton.setOnAction(e -> {
            Controller.deleteRecipe(this, recipe, recipeList);
        });
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getCategoryField() {
        return categoryField;
    }

    public TextField getIngredientsField() {
        return ingredientsField;
    }

    public TextArea getInstructionsField() {
        return instructionsField;
    }
}

class RecipeList extends BorderPane {
    private View appScenes;
    private List<Recipe> recipes;
    private List<Button> buttons;
    private VBox vBox;

    private Button newRecipeButton;
    private HBox newRecipeButtonBox;

    public RecipeList(View appScenes) {
        this.appScenes = appScenes;

        recipes = new ArrayList<Recipe>();
        buttons = new ArrayList<Button>();
        vBox = new VBox();
        vBox.setPrefSize(500, 20);
        vBox.setSpacing(2);
        // vBox
        ScrollPane scrollPane = new ScrollPane(vBox);
        this.setCenter(scrollPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

        // make "New Recipe" button
        newRecipeButton = new Button("New Recipe");
        newRecipeButton.setPrefSize(100, 20);
        newRecipeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets
                                                                                                                    // style
                                                                                                                    // of
                                                                                                                    // button
        newRecipeButton.setOnAction(e -> {

            this.appScenes.displayNewRecipeScene();
            System.out.println("New Recipe pressed");
        });
        newRecipeButtonBox = new HBox();
        newRecipeButtonBox.setAlignment(Pos.CENTER);
        newRecipeButtonBox.getChildren().add(newRecipeButton);
        this.setBottom(newRecipeButtonBox);
    }

    public RecipeList(View appScenes, List<Recipe> rList) {
        this.appScenes = appScenes;

        recipes = new ArrayList<Recipe>(rList);

        buttons = new ArrayList<Button>();
        vBox = new VBox();
        vBox.setPrefSize(500, 20);
        vBox.setSpacing(2);

        for (int i = 0; i < recipes.size(); i++) {
            // For each recipe, add new button with title of recipe
            Recipe r = recipes.get(i);
            addButton(r);
        }

        ScrollPane scrollPane = new ScrollPane(vBox);
        this.setCenter(scrollPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

        // make "New Recipe" button
        newRecipeButton = new Button("New Recipe");
        newRecipeButton.setPrefSize(100, 20);
        newRecipeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets
                                                                                                                    // style
                                                                                                                    // of
                                                                                                                    // button
        newRecipeButton.setOnAction(e -> {
            this.appScenes.displayNewRecipeScene();
        });
        newRecipeButtonBox = new HBox();
        newRecipeButtonBox.setAlignment(Pos.CENTER);
        newRecipeButtonBox.getChildren().add(newRecipeButton);
        this.setBottom(newRecipeButtonBox);
    }

    public RecipeList(List<Recipe> list) {
        recipes = list;
    }

    // adds button to the end of button array
    public void addButton(Recipe r) {
        Button b = new Button(r.getName());
        System.out.println("new recipe name: " + r.getName());
        buttons.add(b);
        b.setPrefSize(500, 20);
        b.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets style of
                                                                                                      // button
        b.setOnAction(e -> {
            DisplayRecipe.display(new AppFramePopUp(this, r));
        });
        // add button to vBox
        vBox.getChildren().add(b);
    }

    // overloaded method for adding a button at a specific index
    public void addButton(int index, Recipe r) {
        Button b = new Button(r.getName());
        System.out.println("new recipe name: " + r.getName());
        buttons.add(index, b);
        b.setPrefSize(500, 20);
        b.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets style of
                                                                                                      // button
        b.setOnAction(e -> {
            DisplayRecipe.display(new AppFramePopUp(this, r));
        });
        // add button to vBox
        vBox.getChildren().add(index, b);
    }

    public void removeButton(Recipe r) {
        this.refresh();
        for (Button button : buttons) {
            if (button.getText().equals(r.getName())) {
                vBox.getChildren().remove(button);
                buttons.remove(button);
                break;
            }
        }
    }

    public void refresh() {
        for (int i = 0; i < recipes.size(); i++) {
            // this.recipes.set(i, this.recipes.get(i));
            this.buttons.get(i).setText(recipes.get(i).getName());
        }

    }

    public List<Recipe> getRecipes() {
        return recipes;
    }
}

class UserCreateAccount extends BorderPane {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button CreateButton;
    private Button BackButton;

    public UserCreateAccount(View appScenes){
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 24;");

        usernameField = new TextField();
        passwordField = new PasswordField();

        usernameField.setPromptText("Choose Username");
        usernameField.setStyle("-fx-pref-width: 50; -fx-font-size: 14;");

        passwordField.setPromptText("Choose Password");
        passwordField.setStyle("-fx-pref-width: 50; -fx-font-size: 14;");


        CreateButton = new Button("Create");
        CreateButton.setPrefSize(100, 20);
        CreateButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        BackButton = new Button("Back");
        BackButton.setPrefSize(100, 20);
        BackButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(CreateButton, BackButton);

        Controller.setListeners(this, appScenes);
        vbox.getChildren().addAll(usernameField, passwordField);

        VBox vbox2 = new VBox();
        vbox2.setAlignment(Pos.TOP_CENTER);
        vbox2.getChildren().addAll(title);
        
        this.setCenter(vbox);
        this.setBottom(hbox);
        this.setTop(vbox2);
    }


    public TextField getUsernameField(){
        return usernameField;
    }

    public PasswordField getPasswordField(){
        return passwordField;
    }

    public Button getCreateButton(){
        return CreateButton;
    }

    public Button getBackButton(){
        return BackButton;
    }
}

class UserLogin extends BorderPane {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button CreateButton;
    private Button LoginButton;

    public UserLogin(View appScenes){
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 24;");

        usernameField = new TextField();
        passwordField = new PasswordField();

        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-pref-width: 50; -fx-font-size: 14;");

        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-pref-width: 50; -fx-font-size: 14;");


        CreateButton = new Button("Create Account");
        CreateButton.setPrefSize(200, 20);
        CreateButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        LoginButton = new Button("Login");
        LoginButton.setPrefSize(200, 20);
        LoginButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(CreateButton, LoginButton);

        Controller.setListeners(this, appScenes);
        vbox.getChildren().addAll(usernameField, passwordField);

        VBox vbox2 = new VBox();
        vbox2.setAlignment(Pos.TOP_CENTER);
        vbox2.getChildren().addAll(title);
        
        this.setCenter(vbox);
        this.setBottom(hbox);
        this.setTop(vbox2);
    }


    public TextField getUsernameField(){
        return usernameField;
    }

    public PasswordField getPasswordField(){
        return passwordField;
    }

    public Button getCreateButton(){
        return CreateButton;
    }

    public Button getLoginButton(){
        return LoginButton;
    }
}