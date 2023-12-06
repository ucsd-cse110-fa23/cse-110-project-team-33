package cse.gradle;

import cse.gradle.View.AppFramePopUp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DisplayRecipe {

    public static void display(AppFramePopUp Recipe) {

        Scene newScene = new Scene(Recipe, 500, 500);
        Stage popUp = new Stage();
        popUp.setTitle(null);
        // Create scene of mentioned size with the border pane
        popUp.setScene(newScene);
        // Make window non-resizable
        popUp.setResizable(true);

        // Show the app
        popUp.show();                
    }
}