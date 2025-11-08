package com.example.ideaboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
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

    private void openCreateIdeaDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/ideaboard/views/create_idea.fxml")
            );
            Parent dialogContent = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Create New Idea");
            
            Scene dialogScene = new Scene(dialogContent);
            dialogScene.getStylesheets().add(
                getClass().getResource("/com/example/ideaboard/styles/app.css").toExternalForm()
            );
            
            dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(dialogScene);
            
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
