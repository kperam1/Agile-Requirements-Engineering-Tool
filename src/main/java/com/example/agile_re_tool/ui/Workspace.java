package com.example.agile_re_tool.ui;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class Workspace extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Agile RE Tool - Workspace");

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 15, 15, 15));
        sidebar.setPrefWidth(200);
        sidebar.getStyleClass().add("sidebar");

        Label appTitle = new Label("Agile RE Tool");
        appTitle.getStyleClass().add("app-title");

        Button dashboardBtn = new Button("Dashboard");
        Button ideationBtn = new Button("Ideation Board");
        Button backlogBtn = new Button("Backlog");
        Button sprintBtn = new Button("Sprint Board");
        Button reportsBtn = new Button("Reports");
        Button settingsBtn = new Button("Team Settings");

        sidebar.getChildren().addAll(appTitle, dashboardBtn, ideationBtn, backlogBtn, sprintBtn, reportsBtn, settingsBtn);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.getStyleClass().add("top-bar");

        Label headerTitle = new Label("Dashboard");
        headerTitle.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createBtn = new Button("+ Create Idea");
        createBtn.getStyleClass().add("create-btn");

        topBar.getChildren().addAll(headerTitle, spacer, createBtn);

        BorderPane dashboardView = new DashboardView().getView();

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setTop(topBar);
        root.setCenter(dashboardView);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/workspace.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
