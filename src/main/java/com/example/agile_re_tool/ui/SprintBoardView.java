package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.UC04EditUserStory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

import java.io.File;
import java.io.FileWriter;
import javafx.stage.FileChooser;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert;


public class SprintBoardView {

    private final VBox todoColumn = buildColumn("To Do");
    private final VBox inProgressColumn = buildColumn("In Progress");
    private final VBox testingColumn = buildColumn("Testing");
    private final VBox doneColumn = buildColumn("Done");

    private JSONArray allStories = new JSONArray();
    private String currentFilter = "All Members";

    private Label todoSummary;
    private Label inProgressSummary;
    private Label testingSummary;
    private Label doneSummary;
    private ComboBox<String> assigneeFilter;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#ffffff;");

        Label heading = new Label("Sprint Board");
        heading.setStyle("-fx-font-size:22px; -fx-font-weight:700; -fx-text-fill:#111827;");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 14;");
        refreshBtn.setOnAction(e -> loadStories());

        Button exportBtn = new Button("Export");
        exportBtn.setStyle("-fx-background-color:#e0e7ff; -fx-text-fill:#2563eb; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600;");
        exportBtn.setOnAction(e -> showExportOptions());

        assigneeFilter = new ComboBox<>();
        assigneeFilter.getItems().add("All Members");
        assigneeFilter.setValue("All Members");
        assigneeFilter.setStyle("-fx-background-radius:8; -fx-padding:6 12;");
        assigneeFilter.setPrefWidth(180);
        assigneeFilter.setOnAction(e -> {
            currentFilter = assigneeFilter.getValue();
            applyFilter();
        });

        HBox header = new HBox(12, heading, new Region(), assigneeFilter, refreshBtn, exportBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0,0,12,0));
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        todoSummary = createSummaryLabel("To Do: 0", "#3b82f6");
        inProgressSummary = createSummaryLabel("In Progress: 0", "#f59e0b");
        testingSummary = createSummaryLabel("Testing: 0", "#8b5cf6");
        doneSummary = createSummaryLabel("Done: 0", "#10b981");

        HBox summaryBox = new HBox(16, todoSummary, inProgressSummary, testingSummary, doneSummary);
        summaryBox.setPadding(new Insets(12));
        summaryBox.setAlignment(Pos.CENTER_LEFT);
        summaryBox.setStyle("-fx-background-color:#f9fafb; -fx-background-radius:12; -fx-border-color:#e5e7eb; -fx-border-radius:12;");

        VBox topSection = new VBox(12, header, summaryBox);

        HBox columns = new HBox(16,
                wrapScrollable(todoColumn),
                wrapScrollable(inProgressColumn),
                wrapScrollable(testingColumn),
                wrapScrollable(doneColumn));
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
        doneColumn.getChildren().retainAll(doneColumn.getChildren().get(0));

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
                        populateAssigneeFilter();
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

    private void populateAssigneeFilter() {
        Set<String> assignees = new HashSet<>();
        assignees.add("All Members");
        for (int i=0; i<allStories.length(); i++) {
            String assignee = allStories.getJSONObject(i).optString("assignedTo","");
            if (!assignee.isEmpty()) assignees.add(assignee);
        }
        String prev = assigneeFilter.getValue();
        assigneeFilter.getItems().clear();
        assigneeFilter.getItems().addAll(assignees.stream().sorted().toList());
        if (assignees.contains(prev)) {
            assigneeFilter.setValue(prev);
            currentFilter = prev;
        } else {
            assigneeFilter.setValue("All Members");
            currentFilter = "All Members";
        }
    }

    private void applyFilter() {
        if (currentFilter == null) currentFilter = "All Members";

        todoColumn.getChildren().retainAll(todoColumn.getChildren().get(0));
        inProgressColumn.getChildren().retainAll(inProgressColumn.getChildren().get(0));
        testingColumn.getChildren().retainAll(testingColumn.getChildren().get(0));
        doneColumn.getChildren().retainAll(doneColumn.getChildren().get(0));

        if (allStories.length() == 0) {
            Label empty = new Label("No stories in sprint.");
            empty.setStyle("-fx-text-fill:#6b7280;");
            todoColumn.getChildren().add(empty);
            updateSummary(0, 0, 0, 0);
            updateCount(todoColumn, "to-do-count", 0);
            updateCount(inProgressColumn, "in-progress-count", 0);
            updateCount(testingColumn, "testing-count", 0);
            updateCount(doneColumn, "done-count", 0);
            return;
        }

        int todoCount = 0, inProgressCount = 0, testingCount = 0, doneCount = 0;

        for (int i=0; i<allStories.length(); i++) {
            JSONObject o = allStories.getJSONObject(i);
            String assignee = o.optString("assignedTo","");

            if (!"All Members".equals(currentFilter) && !currentFilter.equals(assignee)) {
                continue;
            }

            long id = o.getLong("id");
            String title = o.optString("title","Untitled");
            String desc = o.optString("description","");
            String status = o.optString("status","To Do");
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
                case "Done" -> {
                    doneColumn.getChildren().add(card);
                    doneCount++;
                }
                default -> {
                    todoColumn.getChildren().add(card);
                    todoCount++;
                }
            }
        }

        updateSummary(todoCount, inProgressCount, testingCount, doneCount);
        updateCount(todoColumn, "to-do-count", todoCount);
        updateCount(inProgressColumn, "in-progress-count", inProgressCount);
        updateCount(testingColumn, "testing-count", testingCount);
        updateCount(doneColumn, "done-count", doneCount);
    }

    private void updateSummary(int todo, int inProgress, int testing, int done) {
        todoSummary.setText("To Do: " + todo);
        inProgressSummary.setText("In Progress: " + inProgress);
        testingSummary.setText("Testing: " + testing);
        doneSummary.setText("Done: " + done);
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
        Circle avatar = new Circle(20, Color.web("#e0f2fe"));
        Text initials = new Text(getInitials(assignee));
        initials.setStyle("-fx-font-weight:700; -fx-fill:#0369a1; -fx-font-size:12px;");
        StackPane avatarPane = new StackPane(avatar, initials);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size:15px; -fx-font-weight:700; -fx-text-fill:#111827;");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(260);

        Label descLbl = new Label(desc.length() > 100 ? desc.substring(0, 100) + "..." : desc);
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(260);
        descLbl.setStyle("-fx-text-fill:#6b7280; -fx-font-size:13px;");

        Label pointsBadge = new Label("SP: " + points);
        pointsBadge.setStyle(
                "-fx-background-color:#dbeafe; -fx-text-fill:#1e40af; " +
                        "-fx-padding:4 10; -fx-background-radius:12; -fx-font-size:11px; -fx-font-weight:600;"
        );

        Label assigneeLbl = new Label(assignee.isEmpty() ? "Unassigned" : assignee);
        assigneeLbl.setStyle("-fx-text-fill:#6b7280; -fx-font-size:12px;");

        Button editBtn = new Button("Edit");
        editBtn.setStyle(
                "-fx-background-color:#2563eb; -fx-text-fill:white; " +
                        "-fx-background-radius:8; -fx-padding:6 14; -fx-font-size:12px; -fx-cursor:hand;"
        );
        editBtn.setOnAction(e -> new UC04EditUserStory(id).openWindow());

        HBox header = new HBox(10, avatarPane, titleLbl);
        header.setAlignment(Pos.TOP_LEFT);

        HBox footer = new HBox(10, pointsBadge, new Region(), assigneeLbl);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);

        VBox card = new VBox(10, header, descLbl, footer, editBtn);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color:#ffffff; -fx-background-radius:12; " +
                        "-fx-border-color:#e5e7eb; -fx-border-radius:12; -fx-border-width:1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); " +
                        "-fx-cursor:hand;"
        );

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
