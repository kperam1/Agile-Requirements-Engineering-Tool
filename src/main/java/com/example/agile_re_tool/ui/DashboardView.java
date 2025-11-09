package com.example.agile_re_tool.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DashboardView {

    private final StringProperty totalIdeas = new SimpleStringProperty("42");
    private final StringProperty userStories = new SimpleStringProperty("28");
    private final StringProperty sprintReady = new SimpleStringProperty("15");
    private final StringProperty teamVelocity = new SimpleStringProperty("32");

    public BorderPane getView() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));
        pane.getStyleClass().add("dashboard-root");

        pane.getStylesheets().add(
            getClass().getResource("/styles/dashboard.css").toExternalForm()
        );

        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER_LEFT);
        summaryBox.getChildren().addAll(
                createInfoCard("Total Ideas", totalIdeas, "+12% from last month", "card-blue"),
                createInfoCard("User Stories", userStories, "+8% from last month", "card-blue"),
                createInfoCard("Sprint Ready", sprintReady, "Ready for development", "card-yellow"),
                createInfoCard("Team Velocity", teamVelocity, "Points per sprint", "card-green")
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

    private VBox createInfoCard(String title, StringProperty mainValue, String subtitle, String colorClass) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);
        card.getStyleClass().addAll("card", colorClass);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label mainLabel = new Label();
        mainLabel.textProperty().bind(mainValue);
        mainLabel.getStyleClass().add("card-main");

        Label subLabel = new Label(subtitle);
        subLabel.getStyleClass().add("card-sub");

        card.getChildren().addAll(titleLabel, mainLabel, subLabel);
        return card;
    }

    private VBox createRecentIdeasSection() {
        VBox section = new VBox(10);
        section.setPrefWidth(600);

        Label title = new Label("Recent Ideas");
        title.getStyleClass().add("section-title");

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
        idea.getStyleClass().add("idea-item");
        idea.setAlignment(Pos.CENTER_LEFT);

        VBox text = new VBox(5);
        Label ideaTitle = new Label(title);
        ideaTitle.getStyleClass().add("idea-title");

        Label ideaDesc = new Label(desc);
        ideaDesc.getStyleClass().add("idea-desc");

        Label ideaStatus = new Label(status);
        ideaStatus.getStyleClass().add("idea-status");

        text.getChildren().addAll(ideaTitle, ideaDesc, ideaStatus);
        idea.getChildren().add(text);
        return idea;
    }

    private VBox createCurrentSprintSection() {
        VBox sprintBox = new VBox(10);
        sprintBox.setPadding(new Insets(10));
        sprintBox.setPrefWidth(300);
        sprintBox.getStyleClass().add("sprint-box");

        Label title = new Label("Current Sprint");
        title.getStyleClass().add("section-title");

        Label sprintName = new Label("Sprint 24 - Active");
        sprintName.getStyleClass().add("sprint-name");

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

        VBox ideaCard = createActionCard("Create Idea", "Share your innovative ideas with the team", "card-blue");
        ideaCard.setOnMouseClicked(e -> {
            com.example.ideaboard.util.DialogHelper.openCreateIdeaDialog();
        });

        VBox storyCard = createActionCard("Create User Story", "Convert ideas into actionable stories", "card-blue");
        VBox backlogCard = createActionCard("Prioritize Backlog", "Reorder stories by business value", "card-yellow");
        VBox reportCard = createActionCard("Generate Reports", "View velocity and burndown charts", "card-green");

        box.getChildren().addAll(ideaCard, storyCard, backlogCard, reportCard);
        return box;
    }

    private VBox createActionCard(String title, String subtitle, String colorClass) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.getStyleClass().addAll("action-card", colorClass);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("action-title");

        Label subLabel = new Label(subtitle);
        subLabel.getStyleClass().add("action-sub");

        card.setOnMouseEntered(e -> card.setStyle("-fx-opacity: 0.9;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-opacity: 1;"));

        card.getChildren().addAll(titleLabel, subLabel);
        return card;
    }
}
