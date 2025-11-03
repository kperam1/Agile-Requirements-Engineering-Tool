package com.example.agile_re_tool.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DashboardView {

    public BorderPane getView() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));

        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER_LEFT);

        summaryBox.getChildren().addAll(
                createCard("Total Ideas", "42", "+12% from last month", "#e8f0fe"),
                createCard("User Stories", "28", "+8% from last month", "#e8f0fe"),
                createCard("Sprint Ready", "15", "Ready for development", "#fff4e5"),
                createCard("Team Velocity", "32", "Points per sprint", "#e7f7f0")
        );

        pane.setTop(summaryBox);
        return pane;
    }

    private VBox createCard(String title, String mainValue, String subtitle, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label mainLabel = new Label(mainValue);
        mainLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subLabel = new Label(subtitle);
        subLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        card.getChildren().addAll(titleLabel, mainLabel, subLabel);
        return card;
    }
}
