package com.example.agile_re_tool.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.example.agile_re_tool.UC03CreateUserStory;

public class DashboardView {

    private final StringProperty totalIdeas = new SimpleStringProperty("0");
    private final StringProperty userStories = new SimpleStringProperty("0");
    private final StringProperty sprintReady = new SimpleStringProperty("0");
    private final StringProperty teamVelocity = new SimpleStringProperty("0");

    public DashboardView() {
        loadDashboardData();
    }

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
        VBox recentIdeas = createRecentIdeasSection(); // dynamic now
        VBox currentSprint = createCurrentSprintSection();
        centerBox.getChildren().addAll(recentIdeas, currentSprint);
        pane.setCenter(centerBox);

        // Bottom quick actions
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

    // ============================
    // Dynamic Recent Ideas Section
    // ============================
    private VBox createRecentIdeasSection() {
        VBox section = new VBox(10);
        section.setPrefWidth(600);
        Label title = new Label("Recent Ideas");
        title.getStyleClass().add("section-title");

        VBox ideasList = new VBox(10);
        ideasList.getChildren().add(new Label("Loading ideas..."));

        // Fetch from backend
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/ideas"))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JSONArray ideas = new JSONArray(response.body());

                    Platform.runLater(() -> {
                        ideasList.getChildren().clear();
                        if (ideas.length() == 0) {
                            ideasList.getChildren().add(new Label("No ideas found."));
                        } else {
                            for (int i = 0; i < Math.min(5, ideas.length()); i++) {
                                JSONObject idea = ideas.getJSONObject(i);
                                String titleText = idea.optString("title", "Untitled Idea");
                                String desc = idea.optString("description", "No description provided");
                                String status = idea.optString("status", "New");
                                ideasList.getChildren().add(createIdeaItem(titleText, desc, status));
                            }
                        }
                    });
                } else {
                    Platform.runLater(() ->
                            ideasList.getChildren().add(new Label("Failed to load ideas (status " + response.statusCode() + ")"))
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() ->
                        ideasList.getChildren().add(new Label("Error fetching ideas from backend.")));
            }
        }).start();

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

        VBox ideaCard = createActionCard("Create Idea", "Share your innovative ideas", "card-blue");
        ideaCard.setOnMouseClicked(e -> com.example.ideaboard.util.DialogHelper.openCreateIdeaDialog());

        VBox storyCard = createActionCard("Create User Story", "Convert ideas into actionable stories", "card-blue");
        storyCard.setOnMouseClicked(e -> {
            UC03CreateUserStory app = new UC03CreateUserStory();
            Stage stage = new Stage();
            try {
                app.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

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

    // === Load live dashboard data from backend ===
    private void loadDashboardData() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/dashboard"))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject json = new JSONObject(response.body());

                Platform.runLater(() -> {
                    totalIdeas.set(String.valueOf(json.getInt("totalIdeas")));
                    userStories.set(String.valueOf(json.getInt("userStories")));
                    sprintReady.set(String.valueOf(json.getInt("sprintReady")));
                    teamVelocity.set(String.valueOf(json.getInt("teamVelocity")));
                });

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
