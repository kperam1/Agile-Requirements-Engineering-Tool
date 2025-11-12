package com.example.agile_re_tool.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;


public class BacklogView {

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("Backlog");
        title.setFont(new Font(22));
        title.setStyle("-fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search issues...");
        searchField.setPrefWidth(250);

        Button createBtn = new Button("+ Create Issue");
        Button filterBtn = new Button("Filter");

        HBox header = new HBox(10, title, new Region(), searchField, filterBtn, createBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        Label placeholder = new Label("Backlog items will appear here");
        placeholder.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");
        content.getChildren().add(placeholder);

        root.setTop(header);
        root.setCenter(content);

        return root;
    }
}

