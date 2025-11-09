package com.agile.requirements.config;

import com.agile.requirements.view.FxmlView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StageManager {

    private Stage primaryStage;

    @Autowired
    private ApplicationContext applicationContext;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(FxmlView view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlFile()));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root, view.getWidth(), view.getHeight());
            java.net.URL css = getClass().getResource("/css/auth.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            primaryStage.setScene(scene);
            primaryStage.setTitle(view.getTitle());
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load view: " + view + ", " + e.getMessage(), e);
        }
    }
}
