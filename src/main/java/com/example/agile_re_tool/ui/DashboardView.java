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
import com.example.agile_re_tool.session.ProjectSession;

public class DashboardView {

    private final StringProperty totalIdeas = new SimpleStringProperty("0");
    private final StringProperty userStories = new SimpleStringProperty("0");
    private final StringProperty sprintReady = new SimpleStringProperty("0");
    private final StringProperty teamVelocity = new SimpleStringProperty("0");

    private Label sprintNameLabel;
    private ProgressBar sprintProgressBar;
    private Label todoLabel;
    private Label inProgressLabel;
    private Label testingLabel;
    private Label doneLabel;

    public DashboardView() {
        loadDashboardData();
        loadSprintData();
    }

    public void refresh() {
        loadDashboardData();
        loadSprintData();
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
        VBox recentIdeas = createRecentIdeasSection();
        VBox currentSprint = createCurrentSprintSection();
        centerBox.getChildren().addAll(recentIdeas, currentSprint);
        pane.setCenter(centerBox);

        HBox quickActions = createQuickActions();
        pane.setBottom(quickActions);

        return pane;
    }

    // -------------------------------------------------------------------------
    // UI COMPONENTS
    // -------------------------------------------------------------------------

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

        VBox ideasContainer = new VBox(10);
        ideasContainer.setPadding(new Insets(5));
        ideasContainer.getChildren().add(new Label("Loading ideas..."));

        ScrollPane ideasScroll = new ScrollPane(ideasContainer);
        ideasScroll.setFitToWidth(true);
        ideasScroll.setPrefHeight(280);
        ideasScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        ideasScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        ideasScroll.setStyle("-fx-background-color: transparent; -fx-background-insets: 0;");

        long projectId = ProjectSession.getProjectId();

        new Thread(() -> {
            try {
                if (projectId <= 0) {
                    Platform.runLater(() -> {
                        ideasContainer.getChildren().clear();
                        ideasContainer.getChildren().add(new Label("Please select a project."));
                    });
                    return;
                }

                HttpClient client = HttpClient.newHttpClient();
             HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/projects/" + projectId + "/ideas"))
        .build();


                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    ideasContainer.getChildren().clear();

                    if (response.statusCode() != 200) {
                        ideasContainer.getChildren().add(
                                new Label("Failed to load ideas (" + response.statusCode() + ")")
                        );
                        return;
                    }

                    JSONArray ideas = new JSONArray(response.body());
                    if (ideas.length() == 0) {
                        ideasContainer.getChildren().add(new Label("No ideas for this project."));
                        return;
                    }

                    for (int i = 0; i < ideas.length(); i++) {
                        JSONObject idea = ideas.getJSONObject(i);
                        String titleText = idea.optString("title", "Untitled Idea");
                        String descText = idea.optString("description", "No description provided");
                        String status = idea.optString("status", "NEW");

                        ideasContainer.getChildren().add(
                                createIdeaCard(i + 1, titleText, descText, status)
                        );
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    ideasContainer.getChildren().clear();
                    ideasContainer.getChildren().add(new Label("Error fetching ideas."));
                });
            }
        }).start();

        section.getChildren().addAll(title, ideasScroll);
        return section;
    }

    private VBox createIdeaCard(long index, String title, String desc, String status) {
        Label titleLabel = new Label("#" + index + " • " + title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #111827;");

        Label descLabel = new Label(desc);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        Label statusBadge = new Label(status);
        statusBadge.setStyle(
                "-fx-background-color: #eef2ff; -fx-text-fill: #4338ca;" +
                        "-fx-padding: 3 10; -fx-background-radius: 12; -fx-font-size: 11;"
        );

        VBox card = new VBox(6, titleLabel, descLabel, statusBadge);
        card.setPadding(new Insets(12));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 1);"
        );

        return card;
    }

    private VBox createCurrentSprintSection() {
        VBox sprintBox = new VBox(10);
        sprintBox.setPadding(new Insets(10));
        sprintBox.setPrefWidth(300);
        sprintBox.getStyleClass().add("sprint-box");

        Label title = new Label("Board Progress");
        title.getStyleClass().add("section-title");

        sprintNameLabel = new Label("Total stories: 0");
        sprintNameLabel.getStyleClass().add("sprint-name");

        sprintProgressBar = new ProgressBar(0.0);
        sprintProgressBar.setPrefWidth(250);

        todoLabel = new Label("To Do: 0");
        inProgressLabel = new Label("In Progress: 0");
        testingLabel = new Label("Testing: 0");
        doneLabel = new Label("Done: 0");

        VBox statusList = new VBox(5, todoLabel, inProgressLabel, testingLabel, doneLabel);

        sprintBox.getChildren().addAll(title, sprintNameLabel, sprintProgressBar, statusList);
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
        backlogCard.setOnMouseClicked(e -> {
            BacklogView view = new BacklogView();
            Stage stage = new Stage();
            stage.setTitle("Backlog");
            Scene scene = new Scene(view.getView(), 900, 600);
            stage.setScene(scene);
            stage.show();
        });

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

    // -------------------------------------------------------------------------
    // BACKEND INTEGRATIONS (UPDATED WITH PROJECT ID)
    // -------------------------------------------------------------------------

    private void loadDashboardData() {
        new Thread(() -> {
            try {
                long projectId = ProjectSession.getProjectId();

                if (projectId <= 0) {
                    Platform.runLater(() -> {
                        totalIdeas.set("0");
                        userStories.set("0");
                        sprintReady.set("0");
                        teamVelocity.set("0");
                    });
                    return;
                }

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/dashboard/" + projectId))
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

    private void loadSprintData() {
        new Thread(() -> {
            try {
                long projectId = ProjectSession.getProjectId();

                if (projectId <= 0) {
                    Platform.runLater(() -> {
                        sprintNameLabel.setText("Total stories: 0");
                        todoLabel.setText("To Do: 0");
                        inProgressLabel.setText("In Progress: 0");
                        testingLabel.setText("Testing: 0");
                        doneLabel.setText("Done: 0");
                    });
                    return;
                }

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/userstories/project/" + projectId))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) return;

                JSONArray stories = new JSONArray(response.body());

                int todo = 0, inProgress = 0, testing = 0, done = 0;
                int totalStories = stories.length();

                int totalPoints = 0;
                int donePoints = 0;

                for (int i = 0; i < stories.length(); i++) {
                    JSONObject s = stories.getJSONObject(i);
                    String status = s.optString("status", "");
                    Integer sp = s.isNull("storyPoints") ? null : s.optInt("storyPoints");

                    switch (status.toLowerCase()) {
                        case "to do":
                        case "backlog": todo++; break;
                        case "in progress": inProgress++; break;
                        case "testing": testing++; break;
                        case "done": done++; break;
                    }

                    if (sp != null && sp > 0) {
                        totalPoints += sp;
                        if ("done".equalsIgnoreCase(status)) donePoints += sp;
                    }
                }

                double progress = totalPoints > 0 ? (double) donePoints / totalPoints : 0.0;

                int fTodo = todo, fInProg = inProgress, fTesting = testing, fDone = done, fTotal = totalStories;
                double fProgress = progress;

                Platform.runLater(() -> {
                    sprintNameLabel.setText("Total stories: " + fTotal);
                    sprintProgressBar.setProgress(fProgress);
                    todoLabel.setText("To Do: " + fTodo);
                    inProgressLabel.setText("In Progress: " + fInProg);
                    testingLabel.setText("Testing: " + fTesting);
                    doneLabel.setText("Done: " + fDone);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
