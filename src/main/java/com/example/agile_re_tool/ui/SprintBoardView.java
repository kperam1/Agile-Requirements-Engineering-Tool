package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.UC04EditUserStory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class SprintBoardView {

    private final VBox todoColumn = buildColumn("TO DO");
    private final VBox inProgressColumn = buildColumn("IN PROGRESS");
    private final VBox testingColumn = buildColumn("TESTING");
    private JSONArray allStories = new JSONArray();

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#ffffff;");

        // Top summary cards
        HBox summaryCards = new HBox(18);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        summaryCards.setPadding(new Insets(0,0,18,0));

        Label totalStories = createSummaryLabel("Total Stories\n0", "#2563eb");
        Label completed = createSummaryLabel("Completed\n0", "#10b981");
        Label inProgress = createSummaryLabel("In Progress\n0", "#f59e0b");
        Label storyPoints = createSummaryLabel("Story Points\n0/0", "#6366f1");
        summaryCards.getChildren().addAll(totalStories, completed, inProgress, storyPoints);

        // Sprint Progress bar
        ProgressBar sprintProgress = new ProgressBar(0.0);
        sprintProgress.setPrefWidth(400);
        sprintProgress.setStyle("-fx-accent:#2563eb; -fx-background-radius:8;");
        Label progressLabel = new Label("Sprint Progress");
        progressLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#2563eb; -fx-font-weight:600;");
        HBox progressBox = new HBox(12, progressLabel, sprintProgress);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.setPadding(new Insets(0,0,18,0));

        // Top right controls
        Button exportBtn = new Button("Export");
        exportBtn.setStyle("-fx-background-color:#e0e7ff; -fx-text-fill:#2563eb; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600;");
        Button addStoryBtn = new Button("+ Add Story");
        addStoryBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600;");
        HBox topRight = new HBox(10, exportBtn, addStoryBtn);
        topRight.setAlignment(Pos.CENTER_RIGHT);

        HBox topBar = new HBox(20, summaryCards, new Region(), topRight);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);
        VBox topSection = new VBox(10, topBar, progressBox);

        // Columns: only TO DO, IN PROGRESS, TESTING
        HBox columns = new HBox(16,
            wrapScrollable(todoColumn),
            wrapScrollable(inProgressColumn),
            wrapScrollable(testingColumn));
        columns.setPrefHeight(600);
        columns.setAlignment(Pos.TOP_LEFT);

        root.setTop(topSection);
        root.setCenter(columns);

        loadStories();
        return root;
    }
    
    private Label createSummaryLabel(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size:15px; -fx-font-weight:700; -fx-text-fill:" + color + "; -fx-padding:8 16; -fx-background-color:#ffffff; -fx-background-radius:10; -fx-border-color:" + color + "; -fx-border-radius:10; -fx-border-width:2;");
        return lbl;
    }

    private VBox buildColumn(String title) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size:16px; -fx-font-weight:700; -fx-text-fill:#1f2937;");
        
        Label countLbl = new Label("0");
        countLbl.setStyle("-fx-font-size:14px; -fx-font-weight:600; -fx-text-fill:#6b7280; -fx-background-color:#e5e7eb; -fx-background-radius:12; -fx-padding:4 10;");
        countLbl.setId(title.toLowerCase().replace(" ", "-") + "-count");
        
        HBox header = new HBox(10, titleLbl, new Region(), countLbl);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        
        VBox box = new VBox(10, header);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:#f9fafb; -fx-background-radius:12; -fx-border-color:#e5e7eb; -fx-border-radius:12;");
        box.setPrefWidth(320);
        return box;
    }

    private ScrollPane wrapScrollable(VBox column) {
        ScrollPane sp = new ScrollPane(column);
        sp.setFitToWidth(true);
        sp.setPrefHeight(600);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }

    private void loadStories() {
        todoColumn.getChildren().retainAll(todoColumn.getChildren().get(0));
        inProgressColumn.getChildren().retainAll(inProgressColumn.getChildren().get(0));
        testingColumn.getChildren().retainAll(testingColumn.getChildren().get(0));

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/userstories"))
                        .GET().build();
                HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Platform.runLater(() -> {
                        allStories = new JSONArray(response.body());
                        applyFilter();
                    });
                } else {
                    Platform.runLater(() -> showError("Failed: " + response.statusCode()));
                }
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error: " + ex.getMessage()));
            }
        }).start();
    }
    
    private void applyFilter() {
        todoColumn.getChildren().retainAll(todoColumn.getChildren().get(0));
        inProgressColumn.getChildren().retainAll(inProgressColumn.getChildren().get(0));
        testingColumn.getChildren().retainAll(testingColumn.getChildren().get(0));
        if (allStories.length() == 0) {
            Label empty = new Label("No stories in sprint.");
            empty.setStyle("-fx-text-fill:#6b7280;");
            todoColumn.getChildren().add(empty);
            updateCount(todoColumn, "to-do-count", 0);
            updateCount(inProgressColumn, "in-progress-count", 0);
            updateCount(testingColumn, "testing-count", 0);
            return;
        }
        int todoCount = 0, inProgressCount = 0, testingCount = 0;
        for (int i=0; i<allStories.length(); i++) {
            JSONObject o = allStories.getJSONObject(i);
            long id = o.getLong("id");
            String title = o.optString("title","Untitled");
            String desc = o.optString("description","");
            String status = o.optString("status","To Do");
            String assignee = o.optString("assignedTo","");
            int points = o.optInt("storyPoints",0);
            VBox card = buildCard(id, title, desc, status, assignee, points);
            switch (status) {
                case "In Progress" -> {
                    inProgressColumn.getChildren().add(card);
                    inProgressCount++;
                }
                case "Testing" -> {
                    testingColumn.getChildren().add(card);
                    testingCount++;
                }
                default -> {
                    todoColumn.getChildren().add(card);
                    todoCount++;
                }
            }
        }
        updateCount(todoColumn, "to-do-count", todoCount);
        updateCount(inProgressColumn, "in-progress-count", inProgressCount);
        updateCount(testingColumn, "testing-count", testingCount);
    }
    
    private void showError(String msg) {
        Label err = new Label(msg);
        err.setStyle("-fx-text-fill:#dc2626;");
        todoColumn.getChildren().add(err);
    }
    
    private void updateCount(VBox column, String id, int count) {
        column.lookupAll("#" + id).forEach(node -> {
            if (node instanceof Label lbl) {
                lbl.setText(String.valueOf(count));
            }
        });
    }

    private VBox buildCard(long id, String title, String desc, String status, String assignee, int points) {
        // Avatar
        Circle avatar = new Circle(20, Color.web("#e0f2fe"));
        Text initials = new Text(getInitials(assignee));
        initials.setStyle("-fx-font-weight:700; -fx-fill:#0369a1; -fx-font-size:12px;");
        StackPane avatarPane = new StackPane(avatar, initials);

        // Title
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size:15px; -fx-font-weight:700; -fx-text-fill:#111827;");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(260);

        // Description
        Label descLbl = new Label(desc.length() > 100 ? desc.substring(0, 100) + "..." : desc);
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(260);
        descLbl.setStyle("-fx-text-fill:#6b7280; -fx-font-size:13px;");

        // Story Points Badge
        Label pointsBadge = new Label("SP: " + points);
        pointsBadge.setStyle(
            "-fx-background-color:#dbeafe; -fx-text-fill:#1e40af; " +
            "-fx-padding:4 10; -fx-background-radius:12; -fx-font-size:11px; -fx-font-weight:600;"
        );

        // Assignee Label
        Label assigneeLbl = new Label(assignee.isEmpty() ? "Unassigned" : assignee);
        assigneeLbl.setStyle("-fx-text-fill:#6b7280; -fx-font-size:12px;");

        // Edit Button
        Button editBtn = new Button("Edit");
        editBtn.setStyle(
            "-fx-background-color:#2563eb; -fx-text-fill:white; " +
            "-fx-background-radius:8; -fx-padding:6 14; -fx-font-size:12px; -fx-cursor:hand;"
        );
        editBtn.setOnAction(e -> new UC04EditUserStory(id).openWindow());

        // Header with avatar and title
        HBox header = new HBox(10, avatarPane, titleLbl);
        header.setAlignment(Pos.TOP_LEFT);

        // Footer with points and assignee
        HBox footer = new HBox(10, pointsBadge, new Region(), assigneeLbl);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);

        // Card container
        VBox card = new VBox(10, header, descLbl, footer, editBtn);
        card.setPadding(new Insets(14));
        card.setStyle(
            "-fx-background-color:#ffffff; -fx-background-radius:12; " +
            "-fx-border-color:#e5e7eb; -fx-border-radius:12; -fx-border-width:1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); " +
            "-fx-cursor:hand;"
        );

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color:#ffffff; -fx-background-radius:12; " +
            "-fx-border-color:#3b82f6; -fx-border-radius:12; -fx-border-width:2; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 3); " +
            "-fx-cursor:hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color:#ffffff; -fx-background-radius:12; " +
            "-fx-border-color:#e5e7eb; -fx-border-radius:12; -fx-border-width:1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); " +
            "-fx-cursor:hand;"
        ));

        return card;
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p: parts) sb.append(p.charAt(0));
        return sb.toString().toUpperCase();
    }
}