package cse.project.team;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DisplayRecipe {

    public static void DisplayRecipe(AppFramePopUp Recipe) {

        Scene newScene = new Scene(Recipe);
        Stage popUp = new Stage();
        popUp.setTitle(null);
        // Create scene of mentioned size with the border pane
        popUp.setScene(newScene);
        // Make window non-resizable
        popUp.setResizable(false);

        // Show the app
        popUp.show();                
    }
}