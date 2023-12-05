package cse.gradle;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.ServerAddress;

import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class View implements ViewSubject {
    private Scene recipeListScene;
    private Scene newRecipeScene;
    private Scene ingredientsInputScene;
    private Scene userAcccountScene;
    private Scene mainLoginScene;
    private Scene ServerDownScene;
    private RecipeList recipeListRoot;
    private List<Recipe> listOfRecipes;
    private AudioRecorder audioRecorder;
    private Stage stage;
    protected ArrayList<ViewObserver> obsList = new ArrayList<ViewObserver>();

    public View(Stage stage, Controller controller) {
        audioRecorder = new AudioRecorder();
        this.stage = stage;
        recipeListRoot = new RecipeList(this);
        UserLoginConstructor();
        UserAccountSceneConstructor();
        newRecipeSceneConstructor();
        ServerDownConstructor();
    }

    public View(Stage stage, List<Recipe> arrayList) {
        this.listOfRecipes = arrayList;
        audioRecorder = new AudioRecorder();
        this.stage = stage;
    }

    public void updateRecipeListView(List<Recipe> arrayList) {
        this.recipeListRoot.updateRecipeList(arrayList);
    }

    private void newRecipeListSceneConstructor() {
        this.recipeListScene = new Scene(recipeListRoot, 500, 600);
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

    public void UserAccountSceneConstructor() {
        UserCreateAccount userCreateAccount = new UserCreateAccount(this);
        userAcccountScene = new Scene(userCreateAccount, 500, 600);
    }

    public void displayUserAccountSceneConstructor() {
        displayScene(userAcccountScene);
    }

    public void UserLoginConstructor() {
        UserLogin userLoginAccount = new UserLogin(this);
        mainLoginScene = new Scene(userLoginAccount, 500, 600);
    }

    public void displayUserLoginConstructor() {
        displayScene(mainLoginScene);
    }

    public void ServerDownConstructor() {
        ServerDown server = new ServerDown(this);
        ServerDownScene = new Scene(server, 500, 600);
    }

    public void displayServerDownConstructor() {
        displayScene(ServerDownScene);
    }

    public void displayScene(Scene s) {
        stage.setScene(s);
    }

    public Scene getLoginScene() {
        return mainLoginScene;
    }

    public RecipeList getRecipeListRoot() {
        return recipeListRoot;
    }

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }

    @Override
    public void register(ViewObserver obs) {
        obsList.add(obs);
        
        // Reconstruct scenes every time a new observer is added
        recipeListRoot = new RecipeList(this, this.listOfRecipes);
        newRecipeListSceneConstructor();
        UserLoginConstructor();
        UserAccountSceneConstructor();
        newRecipeSceneConstructor();
        ServerDownConstructor();
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
            recordMealTypeButton
                    .setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

            stopRecordMealType = new Button("Stop recording Meal Type");
            stopRecordMealType.setPrefSize(100, 20);
            stopRecordMealType
                    .setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

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
            generateRecipeButton
                    .setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

            backButton = new Button("Back");
            backButton.setPrefSize(100, 20);
            backButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

            for (ViewObserver obs : obsList) {
                obs.setRecipeCreationListeners(this, appScenes, cancelScene);
            }

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

        private HBox toolBar;
        private Button shareButton;

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

            createToolBar();
            createFrame();

            for (ViewObserver obs : obsList) {
                obs.setRecipePopUpListeners(this, recipeList, recipe);
            }
        }

        private void createFrame() {
            nameField = new TextField(recipe.getName());
            categoryField = new TextField(recipe.getCategory());
            ingredientsField = new TextField(recipe.getIngredients());
            instructionsField = new TextArea(recipe.getInstructions());

            vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setPrefSize(500, 20); // sets size
            vBox.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 0; -fx-font-weight: bold;"); // sets
                                                                                                         // background
                                                                                                         // color

            nameField.setPrefSize(500, 20); // set size of text field
            nameField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color of
                                                                                       // texfield
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
            instructionsField.setStyle("-fx-background-color: #ADB6BA; -fx-border-width: 2;"); // set background color
                                                                                               // of
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

        }

        private void createToolBar() {
            toolBar = new HBox();
            toolBar.setPadding(new Insets(10));
            toolBar.setSpacing(10);
            toolBar.setAlignment(Pos.TOP_RIGHT);

            shareButton = new Button("Share");

            toolBar.getChildren().add(shareButton);
            this.setTop(toolBar);
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

        public Button getSaveButton() {
            return saveButton;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }

        public Button getShareButton() {
            return shareButton;
        }
    }

    class RecipeList extends BorderPane {
        public View appScenes;
        private List<Recipe> recipes;
        private List<Button> buttons;
        private VBox vBox;

        private Button newRecipeButton;
        private Button logoutButton;
        private ComboBox<String> sortDropDown; // drop down menu for sorting recipes
        private HBox newRecipeButtonBox;
        private HBox TitleBox;
        private HBox logoutButtonBox;
        private VBox topBox;

        public RecipeList(View appScenes) {
            this.appScenes = appScenes;

            recipes = new ArrayList<Recipe>();
            buttons = new ArrayList<Button>();
            vBox = new VBox();
            vBox.setPrefSize(500, 20);
            vBox.setSpacing(2);
            vBox.setAlignment(Pos.TOP_CENTER);

            Label title = new Label("Recipes");
            title.setStyle("-fx-font-size: 24;");
            title.setAlignment(Pos.CENTER);

            logoutButton = new Button("Logout");
            logoutButton.setAlignment(Pos.CENTER);

            TitleBox = new HBox();
            TitleBox.setAlignment(Pos.TOP_CENTER);
            TitleBox.getChildren().add(title);

            logoutButtonBox = new HBox();
            logoutButtonBox.setAlignment(Pos.TOP_RIGHT);
            logoutButtonBox.getChildren().add(logoutButton);

            // Create a ComboBox with sorting options
            sortDropDown = new ComboBox<>();
            // Add options from Constants.sortOptions
            sortDropDown.getItems().addAll(Constants.sortOptions);

            Label sortLabel = new Label("Sort by:");
            sortLabel.setAlignment(Pos.CENTER_LEFT);
            sortLabel.setPadding(new Insets(0, 10, 0, 0));

            HBox sortBox = new HBox();
            sortBox.setAlignment(Pos.CENTER_LEFT);
            sortBox.getChildren().addAll(sortLabel, sortDropDown);

            // ToolBar to hold the sortBox and logoutButtonBox
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            ToolBar toolBar = new ToolBar(sortBox, spacer, logoutButtonBox);
            toolBar.setPadding(new Insets(5, 10, 5, 10));

            topBox = new VBox();
            topBox.getChildren().addAll(TitleBox, toolBar);
            this.setTop(topBox);

            ScrollPane scrollPane = new ScrollPane(vBox);
            this.setCenter(scrollPane);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

            // make "New Recipe" button
            newRecipeButton = new Button("New Recipe");
            newRecipeButton.setPrefSize(100, 20);
            newRecipeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); 
            
            for (ViewObserver obs : obsList) {
                obs.setRecipeListListeners(this, appScenes);
            }

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
            vBox.setAlignment(Pos.TOP_CENTER);

            for (int i = 0; i < recipes.size(); i++) {
                // For each recipe, add new button with title of recipe
                Recipe r = recipes.get(i);
                addButton(r);
            }

            // Refresh the vbox after adding all the buttons
            refresh();

            Label title = new Label("Recipes");
            title.setStyle("-fx-font-size: 24;");
            title.setAlignment(Pos.CENTER);

            logoutButton = new Button("Logout");
            logoutButton.setAlignment(Pos.CENTER);

            TitleBox = new HBox();
            TitleBox.setAlignment(Pos.TOP_CENTER);
            TitleBox.getChildren().add(title);

            logoutButtonBox = new HBox();
            logoutButtonBox.setAlignment(Pos.TOP_RIGHT);
            logoutButtonBox.getChildren().add(logoutButton);

            // Create a ComboBox with sorting options
            sortDropDown = new ComboBox<>();
            sortDropDown.getItems().addAll(Constants.sortOptions);
            sortDropDown.setValue(Constants.defaultSortOption); // TODO: Change to "newest-oldest" when implemented

            Label sortLabel = new Label("Sort by:");
            sortLabel.setAlignment(Pos.CENTER_LEFT);
            sortLabel.setPadding(new Insets(0, 10, 0, 0));

            HBox sortBox = new HBox();
            sortBox.setAlignment(Pos.CENTER_LEFT);
            sortBox.getChildren().addAll(sortLabel, sortDropDown);

            // ToolBar to hold the sortBox and logoutButtonBox
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            ToolBar toolBar = new ToolBar(sortBox, spacer, logoutButtonBox);
            toolBar.setPadding(new Insets(5, 10, 5, 10));

            topBox = new VBox();
            topBox.getChildren().addAll(TitleBox, toolBar);
            this.setTop(topBox);

            ScrollPane scrollPane = new ScrollPane(vBox);
            this.setCenter(scrollPane);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

            // make "New Recipe" button
            newRecipeButton = new Button("New Recipe");
            newRecipeButton.setPrefSize(100, 20);
            newRecipeButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets

            newRecipeButtonBox = new HBox();
            newRecipeButtonBox.setAlignment(Pos.CENTER);
            newRecipeButtonBox.getChildren().add(newRecipeButton);
            this.setBottom(newRecipeButtonBox);

            // set listeners
            for (ViewObserver obs : obsList) {
                obs.setRecipeListListeners(this, appScenes);
            }
        }

        public void updateRecipeList(List<Recipe> rList) {
            this.recipes = rList;

            // prune recipe names
            for (int i = 0; i < recipes.size(); i++) {
                recipes.get(i).setName(recipes.get(i).getName().replace("\n", "").replace("\r", ""));
            }

            // Remove all buttons from vBox
            vBox.getChildren().removeAll(buttons);

            for (int i = 0; i < recipes.size(); i++) {
                // For each recipe, add new button with title of recipe
                Recipe r = recipes.get(i);
                addButton(r);
            }
        }

        public RecipeList(List<Recipe> list) {
            recipes = list;
        }

        // getters for button and dropdown
        public Button getNewRecipeButton() {
            return newRecipeButton;
        }

        public Button getLogoutButton() {
            return logoutButton;
        }

        public ComboBox<String> getSortDropDown() {
            return sortDropDown;
        }

        // adds button to the end of button array
        public void addButton(Recipe r) {
            Button b = new Button(r.getName());
            System.out.println("new recipe name: " + r.getName());
            buttons.add(b);
            b.setPrefSize(500, 20);
            b.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets style
                                                                                                          // of
                                                                                                          // button
            b.setOnAction(e -> {
                DisplayRecipe.display(new AppFramePopUp(this, r));
            });
            // add button to vBox
            vBox.getChildren().add(b);
            System.err.println("vbox size: " + vBox.getChildren().size());
            // refresh();
        }

        // overloaded method for adding a button at a specific index
        public void addButton(int index, Recipe r) {
            Button b = new Button(r.getName());
            System.out.println("new recipe name: " + r.getName());
            buttons.add(index, b);
            b.setPrefSize(500, 20);
            b.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;"); // sets style
                                                                                                          // of
                                                                                                          // button
            b.setOnAction(e -> {
                DisplayRecipe.display(new AppFramePopUp(this, r));
            });
            // add button to vBox
            vBox.getChildren().add(index, b);
            // refresh();
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
            System.out.println(recipes.size());
            for (int i = 0; i < recipes.size(); i++) {
                // this.recipes.set(i, this.recipes.get(i));
                System.out.println("button text before: " + this.buttons.get(i));
                System.out.println("recipe name before: " + recipes.get(i).getName());
                recipes.get(i).setName(recipes.get(i).getName().replace("\n", "").replace("\r", ""));
                this.buttons.get(i).setText(recipes.get(i).getName());
                System.out.println("button text after: " + this.buttons.get(i));
                System.out.println("recipe name after: " + recipes.get(i).getName());
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

        public UserCreateAccount(View appScenes) {
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

            for (ViewObserver obs : obsList) {
                obs.setAccountWindowListeners(this, appScenes);
            }
                        
            vbox.getChildren().addAll(usernameField, passwordField);

            VBox vbox2 = new VBox();
            vbox2.setAlignment(Pos.TOP_CENTER);
            vbox2.getChildren().addAll(title);

            this.setCenter(vbox);
            this.setBottom(hbox);
            this.setTop(vbox2);
        }

        public TextField getUsernameField() {
            return usernameField;
        }

        public PasswordField getPasswordField() {
            return passwordField;
        }

        public Button getCreateButton() {
            return CreateButton;
        }

        public Button getBackButton() {
            return BackButton;
        }
    }

    class UserLogin extends BorderPane {
        private TextField usernameField;
        private PasswordField passwordField;
        private Button CreateButton;
        private Button LoginButton;
        private CheckBox autoLoginButton;

        public UserLogin(View appScenes) {
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

            autoLoginButton = new CheckBox("Automatic Login");
            autoLoginButton.setPrefSize(200, 20);
            autoLoginButton.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 1; -fx-border-color: #737778;");

            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().addAll(CreateButton, LoginButton);

            for (ViewObserver obs : obsList) {
                // System.out.println("SETTING WINDOW LISTENERS FOR LOGIN");
                obs.setLoginWindowListeners(this, appScenes);
            }
            vbox.getChildren().addAll(usernameField, passwordField, autoLoginButton);

            VBox vbox2 = new VBox();
            vbox2.setAlignment(Pos.TOP_CENTER);
            vbox2.getChildren().addAll(title);

            this.setCenter(vbox);
            this.setBottom(hbox);
            this.setTop(vbox2);
        }

        public TextField getUsernameField() {
            return usernameField;
        }

        public PasswordField getPasswordField() {
            return passwordField;
        }

        public Button getCreateButton() {
            return CreateButton;
        }

        public Button getLoginButton() {
            return LoginButton;
        }

        public CheckBox getAutoLoginButton() {
            return autoLoginButton;
        }
    }

    class ServerDown extends BorderPane {
        public ServerDown(View appScenes) {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);

            Label title = new Label("SERVER DOWN!");
            title.setStyle("-fx-font-size: 20;");

            Label subTitle = new Label("Please Try Again Later");
            subTitle.setStyle("-fx-font-size: 14;");

            vbox.getChildren().addAll(title, subTitle);
            this.setCenter(vbox);
        }

    }
}
