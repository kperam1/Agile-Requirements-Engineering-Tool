package com.example.agile_re_tool.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class TeamSettingsView {

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Team Settings");
        title.setFont(new Font(24));

        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        TextField teamNameField = new TextField();
        teamNameField.setPromptText("Team name");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Developer", "Tester", "Product Owner", "Scrum Master");

        TextField memberField = new TextField();
        memberField.setPromptText("Add member");

        Button addMemberBtn = new Button("Add");

        HBox memberRow = new HBox(10, memberField, roleCombo, addMemberBtn);
        memberRow.setAlignment(Pos.CENTER_LEFT);

        ListView<String> membersList = new ListView<>();
        membersList.setPrefHeight(150);

        Spinner<Integer> sprintLength = new Spinner<>(1, 30, 14);
        DatePicker sprintStart = new DatePicker();

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);

        grid.add(new Label("Team Name"), 0, 0);
        grid.add(teamNameField, 1, 0);

        grid.add(new Label("Add Team Member"), 0, 1);
        grid.add(memberRow, 1, 1);

        grid.add(new Label("Team Members"), 0, 2);
        grid.add(membersList, 1, 2);

        grid.add(new Label("Sprint Length (days)"), 0, 3);
        grid.add(sprintLength, 1, 3);

        grid.add(new Label("Sprint Start Date"), 0, 4);
        grid.add(sprintStart, 1, 4);

        Button saveBtn = new Button("Save Settings");
        saveBtn.setPrefWidth(200);

        HBox saveBox = new HBox(saveBtn);
        saveBox.setAlignment(Pos.CENTER_RIGHT);
        saveBox.setPadding(new Insets(20, 0, 0, 0));

        VBox container = new VBox(20, topBox, grid, saveBox);
        container.setAlignment(Pos.TOP_LEFT);

        root.setCenter(container);

        return root;
    }
}
