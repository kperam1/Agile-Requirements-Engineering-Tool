package com.example.ideaboard.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogHelper {

    public static void openCreateIdeaDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                DialogHelper.class.getResource("/com/example/ideaboard/views/create_idea.fxml")
            );
            Parent dialogContent = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Create New Idea");
            dialogStage.setResizable(false);
            
            Scene dialogScene = new Scene(dialogContent);
            dialogScene.getStylesheets().add(
                DialogHelper.class.getResource("/com/example/ideaboard/styles/app.css").toExternalForm()
            );
            
            dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(dialogScene);
            
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error opening Create Idea dialog: " + e.getMessage());
        }
    }
}
