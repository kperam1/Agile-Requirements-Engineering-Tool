package com.agile.requirements;

import com.agile.requirements.view.FxmlView;
import com.agile.requirements.config.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.HashMap;
import java.util.Map;

public class AgileReToolDesktopApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        SpringApplication app = new SpringApplication(com.example.agile_re_tool.AgileRequirementsEngineeringToolApplication.class);
        Map<String,Object> defaults = new HashMap<>();
        defaults.put("spring.profiles.active","dev");
        app.setDefaultProperties(defaults);
        springContext = app.run();
    }

    @Override
    public void start(Stage primaryStage) {
        StageManager stageManager = springContext.getBean(StageManager.class);
        stageManager.setPrimaryStage(primaryStage);
        stageManager.switchScene(FxmlView.LOGIN);
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
