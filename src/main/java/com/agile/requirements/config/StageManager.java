package com.agile.requirements.config;

import com.agile.requirements.view.FxmlView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Stage Manager
 * Manages JavaFX stage and scene switching
 */
@Component
public class StageManager {
    
    private Stage primaryStage;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public StageManager() {}
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    /**
     * Switch to a different scene
     */
    public void switchScene(FxmlView view) {
        try {
            Parent root = loadView(view.getFxmlFile());
            Scene scene = new Scene(root, view.getWidth(), view.getHeight());
            primaryStage.setScene(scene);
            primaryStage.setTitle(view.getTitle());
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load FXML view
     */
    private Parent loadView(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setControllerFactory(applicationContext::getBean);
        return loader.load();
    }
}
