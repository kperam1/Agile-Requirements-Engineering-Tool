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
import javafx.scene.control.ProgressBar;
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

    private final VBox todoColumn = buildColumn("TO DO");
    private final VBox inProgressColumn = buildColumn("IN PROGRESS");
    private final VBox testingColumn = buildColumn("TESTING");
    private final VBox doneColumn = buildColumn("DONE");
    private JSONArray allStories = new JSONArray();

    private ComboBox<String> assigneeDropdown;
    private Label totalStories, completed, inProgress, storyPoints;
    private ProgressBar sprintProgress;
    private Label progressPercent;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#ffffff;");

        // Top summary cards (pixel-perfect)
        HBox summaryCards = new HBox(24);
        summaryCards.setAlignment(Pos.CENTER);
        summaryCards.setPadding(new Insets(0,0,24,0));
        summaryCards.setPrefWidth(1); // allow to grow
        summaryCards.setMaxWidth(Double.MAX_VALUE);

        // Small horizontal summary cards
        // Use visible icons and single label per card
        HBox totalCard = smallSummaryCard("📦", "Total Stories", "0", "#2563eb", "#e0e7ff");
        totalStories = (Label) totalCard.lookup("#summary-value");

        HBox completedCard = smallSummaryCard("✔", "Completed", "0", "#10b981", "#e0f7e9");
        completed = (Label) completedCard.lookup("#summary-value");

        HBox inProgressCard = smallSummaryCard("⏳", "In Progress", "0", "#f59e0b", "#fff7e0");
        inProgress = (Label) inProgressCard.lookup("#summary-value");

        HBox pointsCard = smallSummaryCard("🏆", "Story Points", "0/0", "#6366f1", "#ececff");
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
        sprintProgress = new ProgressBar(0.3);
        sprintProgress.setPrefWidth(400);
        sprintProgress.setStyle("-fx-accent:#2563eb; -fx-background-radius:8;");
        Label progressLabel = new Label("Sprint Progress");
        progressLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#2563eb; -fx-font-weight:600;");
        progressPercent = new Label("30%");
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

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color:#10b981; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600; -fx-cursor:hand;");
        refreshBtn.setOnAction(e -> {
            loadStories();
            loadAssignees();
        });
        
        Button exportBtn = new Button("Export");
        exportBtn.setStyle("-fx-background-color:#e0e7ff; -fx-text-fill:#2563eb; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600;");
        Button addStoryBtn = new Button("+ Add Story");
        addStoryBtn.setStyle("-fx-background-color:#2563eb; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:6 14; -fx-font-weight:600;");
        HBox filterBar = new HBox(12, assigneeDropdown, new Region(), refreshBtn, exportBtn, addStoryBtn);
        HBox.setHgrow(filterBar.getChildren().get(1), Priority.ALWAYS);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(0,0,12,0));

        VBox topSection = new VBox(10, summaryCards, filterBar, progressBox);

        // Columns: TO DO, IN PROGRESS, TESTING, DONE
        ScrollPane todoScroll = wrapScrollable(todoColumn);
        ScrollPane inProgressScroll = wrapScrollable(inProgressColumn);
        ScrollPane testingScroll = wrapScrollable(testingColumn);
        ScrollPane doneScroll = wrapScrollable(doneColumn);

        enableScrollDrop(todoScroll, "To Do");
        enableScrollDrop(inProgressScroll, "In Progress");
        enableScrollDrop(testingScroll, "Testing");
        enableScrollDrop(doneScroll, "Done");

        HBox columns = new HBox(16, todoScroll, inProgressScroll, testingScroll, doneScroll);
        columns.setPrefHeight(600);
        columns.setAlignment(Pos.TOP_LEFT);
        root.setTop(topSection);
        root.setCenter(columns);
        loadStories();
        loadAssignees();
        return root;
    }

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
                        loadAssignees();
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

    // Populate assignees from loaded user stories
    private void loadAssignees() {
        Platform.runLater(() -> {
            Set<String> assignees = new LinkedHashSet<>();
            assignees.add("All Assignees");
            for (int i = 0; i < allStories.length(); i++) {
                JSONObject o = allStories.getJSONObject(i);
                String assignee = o.optString("assignedTo", "").trim();
                if (!assignee.isEmpty()) assignees.add(assignee);
            }
            assigneeDropdown.getItems().clear();
            assigneeDropdown.getItems().addAll(assignees);
            assigneeDropdown.setValue("All Assignees");
        });
    }
    
    private void applyFilter() {
        todoColumn.getChildren().retainAll(todoColumn.getChildren().get(0));
        inProgressColumn.getChildren().retainAll(inProgressColumn.getChildren().get(0));
        testingColumn.getChildren().retainAll(testingColumn.getChildren().get(0));
        doneColumn.getChildren().retainAll(doneColumn.getChildren().get(0));
        if (allStories.length() == 0) {
            Label empty = new Label("No stories in sprint.");
            empty.setStyle("-fx-text-fill:#6b7280;");
            todoColumn.getChildren().add(empty);
            updateCount(todoColumn, "to-do-count", 0);
            updateCount(inProgressColumn, "in-progress-count", 0);
            updateCount(testingColumn, "testing-count", 0);
            updateCount(doneColumn, "done-count", 0);
            updateSummaryCards(0, 0, 0, 0, 0);
            updateProgressBar(0);
            return;
        }
        int todoCount = 0, inProgressCount = 0, testingCount = 0, doneCount = 0, totalPoints = 0, completedPoints = 0;
        String selectedAssignee = assigneeDropdown != null ? assigneeDropdown.getValue() : "All Assignees";
        for (int i=0; i<allStories.length(); i++) {
            JSONObject o = allStories.getJSONObject(i);
            long id = o.getLong("id");
            String title = o.optString("title","Untitled");
            String desc = o.optString("description","");
            String status = o.optString("status","To Do");
            String assignee = o.optString("assignedTo","");
            int points = o.optInt("storyPoints",0);
            String tags = o.optString("tags","");
            String dueDate = o.optString("dueDate","");
            if (!selectedAssignee.equals("All Assignees") && !assignee.equals(selectedAssignee)) continue;
            VBox card = buildCard(id, title, desc, status, assignee, points, tags, dueDate);
            totalPoints += points;
            if (status.equals("Done")) completedPoints += points;
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
        int totalStoriesCount = todoCount + inProgressCount + testingCount + doneCount;
        updateCount(todoColumn, "to-do-count", todoCount);
        updateCount(inProgressColumn, "in-progress-count", inProgressCount);
        updateCount(testingColumn, "testing-count", testingCount);
        updateCount(doneColumn, "done-count", doneCount);
        updateSummaryCards(totalStoriesCount, doneCount, inProgressCount, completedPoints, totalPoints);
        updateProgressBar(totalPoints == 0 ? 0 : (double)completedPoints/totalPoints);
    }

    private void updateSummaryCards(int total, int completedVal, int inProgressVal, int completedPoints, int totalPoints) {
        if (totalStories != null) totalStories.setText("Total Stories: " + total);
        if (completed != null) completed.setText("Completed: " + completedVal);
        if (inProgress != null) inProgress.setText("In Progress: " + inProgressVal);
        if (storyPoints != null) storyPoints.setText("Story Points: " + completedPoints + "/" + totalPoints);
    }

    private void updateProgressBar(double percent) {
        if (sprintProgress != null) sprintProgress.setProgress(percent);
        if (progressPercent != null) progressPercent.setText((int)(percent*100) + "%");
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

    private VBox buildCard(long id, String title, String desc, String status, String assignee, int points, String tags, String dueDate) {
        // Icon for type
        Label iconLbl = new Label();
        if (title.toLowerCase().contains("bug")) {
            iconLbl.setText("\u26A0"); // warning/bug icon
            iconLbl.setStyle("-fx-text-fill:#dc2626; -fx-font-size:16px; -fx-font-weight:700;");
        } else if (title.toLowerCase().contains("task")) {
            iconLbl.setText("\u26A1"); // lightning for task
            iconLbl.setStyle("-fx-text-fill:#f59e0b; -fx-font-size:16px; -fx-font-weight:700;");
        } else {
            iconLbl.setText("\u2714"); // check for story
            iconLbl.setStyle("-fx-text-fill:#10b981; -fx-font-size:16px; -fx-font-weight:700;");
        }

        // Tags
        HBox tagBox = new HBox(4);
        if (!tags.isBlank()) {
            for (String tag : tags.split(",")) {
                Label tagLbl = new Label(tag.trim());
                tagLbl.setStyle("-fx-background-color:#e0e7ff; -fx-text-fill:#2563eb; -fx-background-radius:8; -fx-padding:2 8; -fx-font-size:11px; -fx-font-weight:600;");
                tagBox.getChildren().add(tagLbl);
            }
        }

        // Due date
        Label dateLbl = new Label(dueDate);
        dateLbl.setStyle("-fx-text-fill:#dc2626; -fx-font-size:12px; -fx-font-weight:600;");

        // Avatar
        Circle avatar = new Circle(20, Color.web("#e0f2fe"));
        Text initials = new Text(getInitials(assignee));
        initials.setStyle("-fx-font-weight:700; -fx-fill:#0369a1; -fx-font-size:12px;");
        StackPane avatarPane = new StackPane(avatar, initials);

        // Title
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size:15px; -fx-font-weight:700; -fx-text-fill:#111827;");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(220);

        // Description
        Label descLbl = new Label(desc.length() > 100 ? desc.substring(0, 100) + "..." : desc);
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(220);
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

        // Header with avatar, icon, and title
        HBox header = new HBox(8, avatarPane, iconLbl, titleLbl);
        header.setAlignment(Pos.TOP_LEFT);

        // Top row: tags and due date
        HBox topRow = new HBox(8, tagBox, dateLbl);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Footer with points and assignee
        HBox footer = new HBox(10, pointsBadge, new Region(), assigneeLbl);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);

        // Card container
        VBox card = new VBox(10, topRow, header, descLbl, footer, editBtn);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color:#fff; -fx-background-radius:16; " +
            "-fx-border-color:#e0e7ff; -fx-border-radius:16; -fx-border-width:2; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 10, 0, 0, 3); " +
            "-fx-cursor:hand;"
        );
        card.setUserData(id);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color:#f3f4f6; -fx-background-radius:16; " +
            "-fx-border-color:#2563eb; -fx-border-radius:16; -fx-border-width:2; " +
            "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.10), 12, 0, 0, 4); " +
            "-fx-cursor:hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color:#fff; -fx-background-radius:16; " +
            "-fx-border-color:#e0e7ff; -fx-border-radius:16; -fx-border-width:2; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 10, 0, 0, 3); " +
            "-fx-cursor:hand;"
        ));

            // Enable drag for story card
            card.setOnDragDetected(event -> {
                javafx.scene.input.Dragboard db = card.startDragAndDrop(javafx.scene.input.TransferMode.MOVE);
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(Long.toString(id));
                db.setContent(content);
                event.consume();
            });

            card.setOnDragDone(event -> {
                event.consume();
            });

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
