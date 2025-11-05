package com.example.agile_re_tool.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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

        HBox centerBox = new HBox(30);
        centerBox.setPadding(new Insets(20, 0, 0, 0));

        VBox recentIdeas = createRecentIdeasSection();
        VBox currentSprint = createCurrentSprintSection();

        centerBox.getChildren().addAll(recentIdeas, currentSprint);
        pane.setCenter(centerBox);

        HBox quickActions = createQuickActions();
        pane.setBottom(quickActions);

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

    private VBox createRecentIdeasSection() {
        VBox section = new VBox(10);
        section.setPrefWidth(600);

        Label title = new Label("Recent Ideas");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox ideasList = new VBox(10);
        ideasList.getChildren().addAll(
                createIdeaItem("Mobile App Dark Mode", "Implement dark mode theme for better user experience.", "Approved"),
                createIdeaItem("Advanced Search Filters", "Add search options to help users find content faster.", "Under Review"),
                createIdeaItem("Real-time Notifications", "Push updates for team collaboration.", "New")
        );

        section.getChildren().addAll(title, ideasList);
        return section;
    }

    private HBox createIdeaItem(String title, String desc, String status) {
        HBox idea = new HBox(10);
        idea.setPadding(new Insets(10));
        idea.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");
        idea.setAlignment(Pos.CENTER_LEFT);

        VBox text = new VBox(5);
        Label ideaTitle = new Label(title);
        ideaTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label ideaDesc = new Label(desc);
        ideaDesc.setStyle("-fx-text-fill: #555;");
        Label ideaStatus = new Label(status);
        ideaStatus.setStyle("-fx-background-color: #e0f7e9; -fx-padding: 2 8; -fx-background-radius: 6;");

        text.getChildren().addAll(ideaTitle, ideaDesc, ideaStatus);
        idea.getChildren().add(text);
        return idea;
    }

    private VBox createCurrentSprintSection() {
        VBox sprintBox = new VBox(10);
        sprintBox.setPadding(new Insets(10));
        sprintBox.setPrefWidth(300);
        sprintBox.setStyle("-fx-background-color: #f6f8ff; -fx-background-radius: 10;");

        Label title = new Label("Current Sprint");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label sprintName = new Label("Sprint 24 - Active");
        sprintName.setStyle("-fx-text-fill: #3f51b5; -fx-font-weight: bold;");

        ProgressBar progress = new ProgressBar(0.65);
        progress.setPrefWidth(250);

        VBox statusList = new VBox(5);
        statusList.getChildren().addAll(
                new Label("To Do: 3"),
                new Label("In Progress: 5"),
                new Label("Testing: 2"),
                new Label("Done: 8")
        );

        sprintBox.getChildren().addAll(title, sprintName, progress, statusList);
        return sprintBox;
    }

private HBox createQuickActions() {
    HBox box = new HBox(20);
    box.setPadding(new Insets(20, 0, 0, 0));
    box.setAlignment(Pos.CENTER);

    VBox ideaCard = createActionCard(" Create Idea", "Share your innovative ideas with the team", "#e8f0fe");
    VBox storyCard = createActionCard(" Create User Story", "Convert ideas into actionable stories", "#e8f0fe");
    VBox backlogCard = createActionCard(" Prioritize Backlog", "Reorder stories by business value", "#fff4e5");
    VBox reportCard = createActionCard(" Generate Reports", "View velocity and burndown charts", "#e7f7f0");

    box.getChildren().addAll(ideaCard, storyCard, backlogCard, reportCard);
    return box;
}

private VBox createActionCard(String title, String subtitle, String color) {
    VBox card = new VBox(5);
    card.setAlignment(Pos.CENTER_LEFT);
    card.setPadding(new Insets(15));
    card.setPrefWidth(220);
    card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-cursor: hand;");
    
    card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-background-radius: 10; -fx-cursor: hand;"));
    card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-cursor: hand;"));

    Label titleLabel = new Label(title);
    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    Label subLabel = new Label(subtitle);
    subLabel.setStyle("-fx-text-fill: #555;");

    card.getChildren().addAll(titleLabel, subLabel);
    return card;
}

    

}
