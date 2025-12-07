package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.UC04EditUserStory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
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

        // Top summary cards (pixel-perfect)
        HBox summaryCards = new HBox(24);
        summaryCards.setAlignment(Pos.CENTER);
        summaryCards.setPadding(new Insets(0,0,24,0));
        summaryCards.setPrefWidth(1); // allow to grow
        summaryCards.setMaxWidth(Double.MAX_VALUE);

        // summary cards
        HBox totalCard = smallSummaryCard("#", "Total Stories", "0", "#2563eb", "#e0e7ff");
        totalStories = (Label) totalCard.lookup("#summary-value");

        HBox completedCard = smallSummaryCard("✓", "Completed", "0", "#10b981", "#e0f7e9");
        completed = (Label) completedCard.lookup("#summary-value");

        HBox inProgressCard = smallSummaryCard("○", "In Progress", "0", "#f59e0b", "#fff7e0");
        inProgress = (Label) inProgressCard.lookup("#summary-value");

        HBox pointsCard = smallSummaryCard("★", "Story Points", "0/0", "#6366f1", "#ececff");
        storyPoints = (Label) pointsCard.lookup("#summary-value");

        summaryCards.getChildren().setAll(totalCard, completedCard, inProgressCard, pointsCard);
        summaryCards.setSpacing(16);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        summaryCards.setStyle("-fx-background-color:transparent;");
        summaryCards.setPrefHeight(40);
        summaryCards.setMaxWidth(Double.MAX_VALUE);
        for (javafx.scene.Node card : summaryCards.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }

        // Sprint Progress bar
        sprintProgress = new ProgressBar(0);
        sprintProgress.setPrefWidth(400);
        sprintProgress.setStyle("-fx-accent:#2563eb; -fx-background-radius:8;");
        Label progressLabel = new Label("Sprint Progress");
        progressLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#2563eb; -fx-font-weight:600;");
        progressPercent = new Label("0%");
        progressPercent.setStyle("-fx-font-size:13px; -fx-text-fill:#2563eb; -fx-font-weight:600;");
        HBox progressBox = new HBox(12, progressLabel, sprintProgress, progressPercent);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        progressBox.setPadding(new Insets(0,0,18,0));

        // Assignee Dropdown
        assigneeDropdown = new ComboBox<>();
        assigneeDropdown.setPrefWidth(180);
        assigneeDropdown.setValue("All Assignees");
        assigneeDropdown.getItems().add("All Assignees");
        assigneeDropdown.setOnAction(e -> applyFilter());

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color:#e0e7ff; -fx-text-fill:#2563eb; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600; -fx-cursor:hand;");
        refreshBtn.setOnAction(e -> {
            loadStories();
            loadAssignees();
        });
        
        Button exportBtn = new Button("Export");
        exportBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600;");
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
    // Enable drop for a column
    private void enableScrollDrop(ScrollPane scroll, String newStatus) {
        scroll.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.MOVE);
                scroll.setStyle(scroll.getStyle() + "; -fx-background-color:#e0f7fa;"); // highlight
            }
            event.consume();
        });
        scroll.setOnDragExited(event -> {
            scroll.setStyle(scroll.getStyle().replace("; -fx-background-color:#e0f7fa;", ""));
            event.consume();
        });
        scroll.setOnDragDropped(event -> {
            javafx.scene.input.Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                long storyId = Long.parseLong(db.getString());
                handleStoryDrop(storyId, newStatus);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    // Handle story drop 
    private void handleStoryDrop(long storyId, String newStatus) {
        // Update the status in allStories JSONArray
        for (int i = 0; i < allStories.length(); i++) {
            JSONObject story = allStories.getJSONObject(i);
            if (story.getLong("id") == storyId) {
                story.put("status", newStatus);
                break;
            }
        }
        
        // Find the card node for the storyId
        VBox[] columns = {todoColumn, inProgressColumn, testingColumn, doneColumn};
        VBox targetColumn = null;
        switch (newStatus) {
            case "To Do": targetColumn = todoColumn; break;
            case "In Progress": targetColumn = inProgressColumn; break;
            case "Testing": targetColumn = testingColumn; break;
            case "Done": targetColumn = doneColumn; break;
        }
        for (VBox col : columns) {
            for (Iterator<javafx.scene.Node> it = col.getChildren().iterator(); it.hasNext(); ) {
                javafx.scene.Node node = it.next();
                if (node.getUserData() != null && node.getUserData().equals(storyId)) {
                    it.remove();
                    if (targetColumn != null) {
                        targetColumn.getChildren().add(node);
                    }
                    break;
                }
            }
        }
        
        // Recalculate summary and update UI
        recalculateSummary();
        
        // Send backend update (async)
        Platform.runLater(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                JSONObject payload = new JSONObject();
                payload.put("id", storyId);
                payload.put("status", newStatus);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/userstories/" + storyId + "/status"))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .build();
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> System.out.println("Status update: " + response.body()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    // Recalculate summary cards and progress based on current column contents
    private void recalculateSummary() {
        int todoCount = todoColumn.getChildren().size() - 1; // -1 for header
        int inProgressCount = inProgressColumn.getChildren().size() - 1;
        int testingCount = testingColumn.getChildren().size() - 1;
        int doneCount = doneColumn.getChildren().size() - 1;
        
        int totalStoriesCount = todoCount + inProgressCount + testingCount + doneCount;
        int totalPoints = 0;
        int completedPoints = 0;
        
        // Calculate story points from allStories
        for (int i = 0; i < allStories.length(); i++) {
            JSONObject story = allStories.getJSONObject(i);
            int points = story.optInt("storyPoints", 0);
            totalPoints += points;
            if (story.optString("status", "").equals("Done")) {
                completedPoints += points;
            }
        }
        
        updateCount(todoColumn, "to-do-count", todoCount);
        updateCount(inProgressColumn, "in-progress-count", inProgressCount);
        updateCount(testingColumn, "testing-count", testingCount);
        updateCount(doneColumn, "done-count", doneCount);
        updateSummaryCards(totalStoriesCount, doneCount, inProgressCount, completedPoints, totalPoints);
        updateProgressBar(totalPoints == 0 ? 0 : (double)completedPoints / totalPoints);
    }
    
    // Small horizontal summary card
    private HBox smallSummaryCard(String iconUnicode, String label, String value, String color, String bgColor) {
        Label iconLbl = new Label(iconUnicode);
        iconLbl.setStyle("-fx-font-size:18px; -fx-text-fill:" + color + "; -fx-font-weight:700; -fx-padding:0 8 0 0;");

        Label labelAndValue = new Label(label + ": " + value);
        labelAndValue.setId("summary-value");
        labelAndValue.setStyle("-fx-font-size:16px; -fx-font-weight:600; -fx-text-fill:" + color + ";");

        HBox card = new HBox(8, iconLbl, labelAndValue);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(8, 16, 8, 16));
        card.setStyle(
            "-fx-background-color:" + bgColor + "; " +
            "-fx-background-radius:12; " +
            "-fx-border-color:transparent; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 4, 0, 0, 1);"
        );
        card.setMinHeight(32);
        card.setMaxHeight(40);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
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

    private void showExportOptions() {

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
        JSONArray exportData = allStories;

        if (choice.equals("CSV")) {
            exportCSV(exportData, file);
        } else {
            exportJSON(exportData, file);
        }

        new Alert(Alert.AlertType.INFORMATION, "Export completed!").show();

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

private void exportCSV(JSONArray stories, File file) throws Exception {
    FileWriter writer = new FileWriter(file);
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

    writer.close();
}

private String escape(String text) {
    if (text == null) return "";
    return text.replace(",", " "); 
}

private void exportJSON(JSONArray stories, File file) throws Exception {
    FileWriter writer = new FileWriter(file);
    writer.write(stories.toString(4)); 
    writer.close();
}
}
