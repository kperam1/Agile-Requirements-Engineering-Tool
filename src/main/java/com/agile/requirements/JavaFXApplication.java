package com.agile.requirements;

import com.agile.requirements.config.StageManager;
import com.agile.requirements.view.FxmlView;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFXApplication extends Application {
    
    private ConfigurableApplicationContext applicationContext;
    
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(AgileRequirementsApplication.class)
                .run();
    }
    
    @Override
    public void start(Stage primaryStage) {
        StageManager stageManager = applicationContext.getBean(StageManager.class);
        stageManager.setPrimaryStage(primaryStage);
        stageManager.switchScene(FxmlView.SIGNUP);
    }
    
    @Override
    public void stop() {
        applicationContext.close();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
