package com.agile.requirements;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AgileReToolDesktopApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Login.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/css/auth.css").toExternalForm()
            );

            primaryStage.setTitle("Agile RE Tool - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
