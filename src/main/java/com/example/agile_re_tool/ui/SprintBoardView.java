package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.UC04EditUserStory;
import com.example.agile_re_tool.session.ProjectSession;
import com.example.agile_re_tool.util.SprintFetcher;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class SprintBoardView {

    // If opened from SprintManagementView with a specific sprint
    private final Long selectedSprintId;

    // Constructors
    public SprintBoardView(Long sprintId) {
        this.selectedSprintId = sprintId;
    }

    // Used from sidebar button (no pre-selected sprint)
    public SprintBoardView() {
        this.selectedSprintId = null;
    }

    // Columns
    private final VBox todoColumn = buildColumn("To Do");
    private final VBox inProgressColumn = buildColumn("In Progress");
    private final VBox testingColumn = buildColumn("Testing");
    private final VBox doneColumn = buildColumn("Done");

    // Data
    private JSONArray allStories = new JSONArray();
    private String currentAssigneeFilter = "All Members";
    private String currentSprintFilter = "All Sprints";

    // Status summaries (row with badges)
    private Label todoSummary;
    private Label inProgressSummary;
    private Label testingSummary;
    private Label doneSummary;

    // Filters
    private ComboBox<String> assigneeFilter;
    private ComboBox<String> sprintFilter;
    private Map<String, Long> sprintMap = new LinkedHashMap<>();

    // Top summary cards + progress
    private Label totalStoriesLabel;
    private Label completedStoriesLabel;
    private Label inProgressStoriesLabel;
    private Label storyPointsLabel;
    private ProgressBar sprintProgress;
    private Label progressPercent;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#ffffff;");

        // Heading
        Label heading = new Label("Sprint Board");
        heading.setStyle("-fx-font-size:22px; -fx-font-weight:700; -fx-text-fill:#111827;");

        // Filters + actions
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 14;");
        refreshBtn.setOnAction(e -> {
            loadSprints();
            loadStories();
        });

        Button exportBtn = new Button("Export");
        exportBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600;");
        exportBtn.setOnAction(e -> showExportOptions());

        sprintFilter = new ComboBox<>();
        sprintFilter.setStyle("-fx-background-radius:8; -fx-padding:6 12;");
        sprintFilter.setPrefWidth(180);
        sprintFilter.setOnAction(e -> {
            currentSprintFilter = sprintFilter.getValue();
            applyFilter();
        });

        assigneeFilter = new ComboBox<>();
        assigneeFilter.setStyle("-fx-background-radius:8; -fx-padding:6 12;");
        assigneeFilter.setPrefWidth(180);
        assigneeFilter.setOnAction(e -> {
            currentAssigneeFilter = assigneeFilter.getValue();
            applyFilter();
        });

        HBox header = new HBox(12, heading, new Region(), sprintFilter, assigneeFilter, refreshBtn, exportBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 12, 0));
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        // --- Summary cards row (Total, Completed, In Progress, Story Points) ---

        totalStoriesLabel = new Label("0");
        completedStoriesLabel = new Label("0");
        inProgressStoriesLabel = new Label("0");
        storyPointsLabel = new Label("0/0");

        HBox totalCard = buildSummaryCard("#", "Total Stories", totalStoriesLabel, "#2563eb", "#e0e7ff");
        HBox completedCard = buildSummaryCard("✓", "Completed", completedStoriesLabel, "#10b981", "#e0f7e9");
        HBox inProgressCard = buildSummaryCard("○", "In Progress", inProgressStoriesLabel, "#f59e0b", "#fff7e0");
        HBox pointsCard = buildSummaryCard("★", "Story Points", storyPointsLabel, "#6366f1", "#ececff");

        HBox summaryCards = new HBox(16, totalCard, completedCard, inProgressCard, pointsCard);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        summaryCards.setPadding(new Insets(4, 0, 12, 0));

        // --- Sprint progress ---

        sprintProgress = new ProgressBar(0);
        sprintProgress.setPrefWidth(300);
        sprintProgress.setStyle("-fx-accent:#2563eb; -fx-background-radius:8;");

        Label progressLabel = new Label("Sprint Progress");
        progressLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#2563eb; -fx-font-weight:600;");
        progressPercent = new Label("0%");
        progressPercent.setStyle("-fx-font-size:13px; -fx-text-fill:#2563eb; -fx-font-weight:600;");

        HBox progressBox = new HBox(10, progressLabel, sprintProgress, progressPercent);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.setPadding(new Insets(0, 0, 8, 0));

        // --- Status badge row (To Do / In Progress / Testing / Done) ---

        todoSummary = createSummaryLabel("To Do: 0", "#3b82f6");
        inProgressSummary = createSummaryLabel("In Progress: 0", "#f59e0b");
        testingSummary = createSummaryLabel("Testing: 0", "#8b5cf6");
        doneSummary = createSummaryLabel("Done: 0", "#10b981");

        HBox summaryBox = new HBox(16, todoSummary, inProgressSummary, testingSummary, doneSummary);
        summaryBox.setPadding(new Insets(8, 0, 8, 0));
        summaryBox.setAlignment(Pos.CENTER_LEFT);
        summaryBox.setStyle("-fx-background-color:#f9fafb; -fx-background-radius:12; -fx-border-color:#e5e7eb; -fx-border-radius:12; -fx-padding:8;");

        VBox topSection = new VBox(12, header, summaryCards, progressBox, summaryBox);

        // --- Columns ---

        ScrollPane todoPane = wrapScrollable(todoColumn);
        ScrollPane inProgressPane = wrapScrollable(inProgressColumn);
        ScrollPane testingPane = wrapScrollable(testingColumn);
        ScrollPane donePane = wrapScrollable(doneColumn);

        HBox columns = new HBox(16, todoPane, inProgressPane, testingPane, donePane);
        columns.setPrefHeight(600);
        columns.setAlignment(Pos.TOP_LEFT);

        root.setTop(topSection);
        root.setCenter(columns);

        // Initial load
        loadSprints();
        loadStories();
        return root;
    }

    // Build a horizontal summary card
    private HBox buildSummaryCard(String iconText, String labelText, Label valueLabel, String color, String bgColor) {
        Label icon = new Label(iconText);
        icon.setStyle("-fx-font-size:18px; -fx-text-fill:" + color + "; -fx-font-weight:700; -fx-padding:0 8 0 0;");

        Label label = new Label(labelText + ": ");
        label.setStyle("-fx-font-size:14px; -fx-text-fill:" + color + "; -fx-font-weight:600;");

        valueLabel.setStyle("-fx-font-size:16px; -fx-text-fill:" + color + "; -fx-font-weight:700;");

        HBox inner = new HBox(4, label, valueLabel);
        inner.setAlignment(Pos.CENTER_LEFT);

        HBox card = new HBox(8, icon, inner);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(8, 16, 8, 16));
        card.setStyle(
                "-fx-background-color:" + bgColor + ";" +
                "-fx-background-radius:12;" +
                "-fx-border-color:transparent;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 4, 0, 0, 1);"
        );
        card.setMinHeight(32);
        return card;
    }

    private Label createSummaryLabel(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle(
                "-fx-font-size:15px; -fx-font-weight:700; -fx-text-fill:" + color + ";" +
                "-fx-padding:8 16; -fx-background-color:#ffffff; -fx-background-radius:10;" +
                "-fx-border-color:" + color + "; -fx-border-radius:10; -fx-border-width:2;"
        );
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

    private void resetColumns() {
        todoColumn.getChildren().retainAll(todoColumn.getChildren().get(0));
        inProgressColumn.getChildren().retainAll(inProgressColumn.getChildren().get(0));
        testingColumn.getChildren().retainAll(testingColumn.getChildren().get(0));
        doneColumn.getChildren().retainAll(doneColumn.getChildren().get(0));
    }

    // ----------------- SPRINTS -----------------

    private void loadSprints() {
        Long projectId = ProjectSession.getProjectId();

        sprintMap = new LinkedHashMap<>();
        sprintFilter.getItems().clear();

        sprintFilter.getItems().add("All Sprints");
        sprintFilter.getItems().add("No Sprint");

        if (projectId == null) {
            sprintFilter.setValue("All Sprints");
            currentSprintFilter = "All Sprints";
            return;
        }

        Map<String, Long> fetched = SprintFetcher.fetchSprints();
        sprintMap.putAll(fetched);

        sprintFilter.getItems().addAll(sprintMap.keySet());

        // If opened from a specific sprint, auto-select it
        if (selectedSprintId != null) {
            for (Map.Entry<String, Long> entry : sprintMap.entrySet()) {
                if (entry.getValue().equals(selectedSprintId)) {
                    sprintFilter.setValue(entry.getKey());
                    currentSprintFilter = entry.getKey();
                    return;
                }
            }
        }

        // Fallback to previous or "All Sprints"
        if (currentSprintFilter == null || !sprintFilter.getItems().contains(currentSprintFilter)) {
            sprintFilter.setValue("All Sprints");
            currentSprintFilter = "All Sprints";
        } else {
            sprintFilter.setValue(currentSprintFilter);
        }
    }

    // ----------------- STORIES -----------------

    private void loadStories() {
        resetColumns();

        Long projectId = ProjectSession.getProjectId();

        if (projectId == null) {
            Platform.runLater(() -> {
                resetColumns();
                Label msg = new Label("Select a project to view sprint board.");
                msg.setStyle("-fx-text-fill:#6b7280;");
                todoColumn.getChildren().add(msg);
                updateSummary(0, 0, 0, 0);
                updateCount(todoColumn, "to-do-count", 0);
                updateCount(inProgressColumn, "in-progress-count", 0);
                updateCount(testingColumn, "testing-count", 0);
                updateCount(doneColumn, "done-count", 0);
                updateSummaryCards(0, 0, 0, 0, 0);
                updateProgressBar(0.0);
            });
            return;
        }

        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/userstories/project/" + projectId;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

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
        for (int i = 0; i < allStories.length(); i++) {
            String assignee = allStories.getJSONObject(i).optString("assignedTo", "");
            if (!assignee.isEmpty()) assignees.add(assignee);
        }
        String prev = currentAssigneeFilter;
        assigneeFilter.getItems().clear();
        assigneeFilter.getItems().addAll(assignees.stream().sorted().toList());
        if (assignees.contains(prev)) {
            assigneeFilter.setValue(prev);
            currentAssigneeFilter = prev;
        } else {
            assigneeFilter.setValue("All Members");
            currentAssigneeFilter = "All Members";
        }
    }

    private void applyFilter() {
        if (currentAssigneeFilter == null) currentAssigneeFilter = "All Members";
        if (currentSprintFilter == null) currentSprintFilter = "All Sprints";

        resetColumns();

        if (allStories.length() == 0) {
            Label empty = new Label("No stories in sprint.");
            empty.setStyle("-fx-text-fill:#6b7280;");
            todoColumn.getChildren().add(empty);
            updateSummary(0, 0, 0, 0);
            updateCount(todoColumn, "to-do-count", 0);
            updateCount(inProgressColumn, "in-progress-count", 0);
            updateCount(testingColumn, "testing-count", 0);
            updateCount(doneColumn, "done-count", 0);
            updateSummaryCards(0, 0, 0, 0, 0);
            updateProgressBar(0.0);
            return;
        }

        int todoCount = 0, inProgressCount = 0, testingCount = 0, doneCount = 0;

        for (int i = 0; i < allStories.length(); i++) {
            JSONObject o = allStories.getJSONObject(i);

            // Assignee filter
            String assignee = o.optString("assignedTo", "");
            if (!"All Members".equals(currentAssigneeFilter) && !currentAssigneeFilter.equals(assignee)) {
                continue;
            }

            // Sprint filter
            JSONObject sprintObj = o.optJSONObject("sprint");
            Long storySprintId = null;
            if (sprintObj != null && !sprintObj.isNull("id")) {
                storySprintId = sprintObj.getLong("id");
            }

            if (!"All Sprints".equals(currentSprintFilter)) {
                if ("No Sprint".equals(currentSprintFilter)) {
                    if (storySprintId != null) continue;
                } else {
                    Long expectedId = sprintMap.get(currentSprintFilter);
                    if (expectedId == null || !expectedId.equals(storySprintId)) continue;
                }
            }

            long id = o.getLong("id");
            String title = o.optString("title", "Untitled");
            String desc = o.optString("description", "");
            String status = o.optString("status", "To Do");
            int points = o.optInt("storyPoints", 0);

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

        // Recompute global cards + progress
        recalculateSummaryFromData();
    }

    private void updateSummary(int todo, int inProgress, int testing, int done) {
        todoSummary.setText("To Do: " + todo);
        inProgressSummary.setText("In Progress: " + inProgress);
        testingSummary.setText("Testing: " + testing);
        doneSummary.setText("Done: " + done);
    }

    private void showError(String msg) {
        resetColumns();
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

        String descText = desc == null ? "" : desc;
        if (descText.length() > 100) descText = descText.substring(0, 100) + "...";
        Label descLbl = new Label(descText);
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
        for (String p : parts) sb.append(p.charAt(0));
        return sb.toString().toUpperCase();
    }

    // ----------------- SUMMARY CARDS + PROGRESS -----------------

    private void recalculateSummaryFromData() {
        if (allStories == null || allStories.length() == 0) {
            updateSummaryCards(0, 0, 0, 0, 0);
            updateProgressBar(0.0);
            return;
        }

        int todoCount = 0, inProgressCount = 0, testingCount = 0, doneCount = 0;
        int totalStoriesCount = 0;
        int totalPoints = 0;
        int completedPoints = 0;

        for (int i = 0; i < allStories.length(); i++) {
            JSONObject o = allStories.getJSONObject(i);

            // Respect current filters
            String assignee = o.optString("assignedTo", "");
            if (!"All Members".equals(currentAssigneeFilter) && !currentAssigneeFilter.equals(assignee)) {
                continue;
            }

            JSONObject sprintObj = o.optJSONObject("sprint");
            Long storySprintId = null;
            if (sprintObj != null && !sprintObj.isNull("id")) {
                storySprintId = sprintObj.getLong("id");
            }

            if (!"All Sprints".equals(currentSprintFilter)) {
                if ("No Sprint".equals(currentSprintFilter)) {
                    if (storySprintId != null) continue;
                } else {
                    Long expectedId = sprintMap.get(currentSprintFilter);
                    if (expectedId == null || !expectedId.equals(storySprintId)) continue;
                }
            }

            totalStoriesCount++;

            String status = o.optString("status", "To Do");
            int points = o.optInt("storyPoints", 0);

            switch (status) {
                case "In Progress" -> inProgressCount++;
                case "Testing" -> testingCount++;
                case "Done" -> doneCount++;
                default -> todoCount++;
            }

            if (points > 0) {
                totalPoints += points;
                if ("Done".equalsIgnoreCase(status)) {
                    completedPoints += points;
                }
            }
        }

        updateSummary(todoCount, inProgressCount, testingCount, doneCount);
        updateCount(todoColumn, "to-do-count", todoCount);
        updateCount(inProgressColumn, "in-progress-count", inProgressCount);
        updateCount(testingColumn, "testing-count", testingCount);
        updateCount(doneColumn, "done-count", doneCount);

        updateSummaryCards(totalStoriesCount, doneCount, inProgressCount, completedPoints, totalPoints);

        double progress = (totalPoints == 0) ? 0.0 : (double) completedPoints / totalPoints;
        updateProgressBar(progress);
    }

    private void updateSummaryCards(int totalStories, int completed, int inProgress, int completedPoints, int totalPoints) {
        totalStoriesLabel.setText(String.valueOf(totalStories));
        completedStoriesLabel.setText(String.valueOf(completed));
        inProgressStoriesLabel.setText(String.valueOf(inProgress));
        storyPointsLabel.setText(completedPoints + "/" + totalPoints);
    }

    private void updateProgressBar(double ratio) {
        sprintProgress.setProgress(ratio);
        int percent = (int) Math.round(ratio * 100.0);
        progressPercent.setText(percent + "%");
    }

    // ----------------- EXPORT -----------------

    private void showExportOptions() {
        if (allStories == null || allStories.length() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No stories to export.");
            alert.setHeaderText(null);
            alert.show();
            return;
        }

        List<String> choices = List.of("CSV", "JSON");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("CSV", choices);
        dialog.setTitle("Export Stories");
        dialog.setHeaderText("Choose Export Format");
        dialog.setContentText("Format:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String choice = result.get();

        FileChooser fc = new FileChooser();
        fc.setTitle("Save Exported Stories");

        if (choice.equals("CSV")) {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fc.setInitialFileName("sprint_stories.csv");
        } else {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            fc.setInitialFileName("sprint_stories.json");
        }

        File file = fc.showSaveDialog(null);
        if (file == null) return;

        try {
            // Export stories as currently loaded (respecting filters not necessary, but we can)
            JSONArray exportData = new JSONArray();

            for (int i = 0; i < allStories.length(); i++) {
                JSONObject o = allStories.getJSONObject(i);

                String assignee = o.optString("assignedTo", "");
                if (!"All Members".equals(currentAssigneeFilter) && !currentAssigneeFilter.equals(assignee)) {
                    continue;
                }

                JSONObject sprintObj = o.optJSONObject("sprint");
                Long storySprintId = null;
                if (sprintObj != null && !sprintObj.isNull("id")) {
                    storySprintId = sprintObj.getLong("id");
                }

                if (!"All Sprints".equals(currentSprintFilter)) {
                    if ("No Sprint".equals(currentSprintFilter)) {
                        if (storySprintId != null) continue;
                    } else {
                        Long expectedId = sprintMap.get(currentSprintFilter);
                        if (expectedId == null || !expectedId.equals(storySprintId)) continue;
                    }
                }

                exportData.put(o);
            }

            if (choice.equals("CSV")) {
                exportCSV(exportData, file);
            } else {
                exportJSON(exportData, file);
            }

            new Alert(Alert.AlertType.INFORMATION, "Export completed!").show();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Export failed: " + ex.getMessage()).show();
        }
    }

    private void exportCSV(JSONArray stories, File file) throws Exception {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,title,description,status,assignedTo,storyPoints\n");

            for (int i = 0; i < stories.length(); i++) {
                JSONObject o = stories.getJSONObject(i);
                writer.write(
                        o.optLong("id") + "," +
                                escape(o.optString("title")) + "," +
                                escape(o.optString("description")) + "," +
                                o.optString("status") + "," +
                                o.optString("assignedTo") + "," +
                                o.optInt("storyPoints") + "\n"
                );
            }
        }
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace(",", " ");
    }

    private void exportJSON(JSONArray stories, File file) throws Exception {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(stories.toString(4));
        }
    }
}
