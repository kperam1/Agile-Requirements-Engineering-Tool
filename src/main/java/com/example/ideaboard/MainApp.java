package com.example.ideaboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Modality;

/**
 * Main application class for the IdeaBoard JavaFX application.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create a simple main window with a button to open the Create Idea modal
            Button openModalButton = new Button("Open Create Idea Form");
            openModalButton.setOnAction(e -> openCreateIdeaDialog());
            openModalButton.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-padding: 12 24; " +
                "-fx-background-color: #2563EB; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand;"
            );
            
            StackPane root = new StackPane(openModalButton);
            root.setStyle("-fx-background-color: #F6F7FB;");
            
            Scene scene = new Scene(root, 800, 600);
            
            // Load the global stylesheet
            scene.getStylesheets().add(
                getClass().getResource("/com/example/ideaboard/styles/app.css").toExternalForm()
            );
            
            primaryStage.setTitle("IdeaBoard - Main");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Create Idea form in a modal dialog.
     */
    private void openCreateIdeaDialog() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/ideaboard/views/create_idea.fxml")
            );
            Parent dialogContent = loader.load();
            
            // Create a new stage (modal dialog)
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Create New Idea");
            
            // Create scene and apply stylesheet
            Scene dialogScene = new Scene(dialogContent);
            dialogScene.getStylesheets().add(
                getClass().getResource("/com/example/ideaboard/styles/app.css").toExternalForm()
            );
            
            // Make the dialog background transparent/match the modal surface
            dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(dialogScene);
            
            // Show the dialog
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
