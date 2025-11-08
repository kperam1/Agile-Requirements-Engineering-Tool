package com.example.ideaboard.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Utility class to help with opening dialogs and modal windows.
 */
public class DialogHelper {

    /**
     * Opens the Create Idea form in a modal dialog.
     * 
     * Usage example:
     * <pre>
     * Button myButton = new Button("Create Idea");
     * myButton.setOnAction(e -> DialogHelper.openCreateIdeaDialog());
     * </pre>
     */
    public static void openCreateIdeaDialog() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(
                DialogHelper.class.getResource("/com/example/ideaboard/views/create_idea.fxml")
            );
            Parent dialogContent = loader.load();
            
            // Create a new stage (modal dialog)
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Create New Idea");
            dialogStage.setResizable(false);
            
            // Create scene and apply stylesheet
            Scene dialogScene = new Scene(dialogContent);
            dialogScene.getStylesheets().add(
                DialogHelper.class.getResource("/com/example/ideaboard/styles/app.css").toExternalForm()
            );
            
            // Set transparent fill for smooth appearance
            dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(dialogScene);
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error opening Create Idea dialog: " + e.getMessage());
        }
    }
}
