package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.UC04EditUserStory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BacklogView {

    private VBox backlogList;

    private JSONArray fullData = new JSONArray(); // store original data

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("Backlog");
        title.setFont(new Font(22));
        title.setStyle("-fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search issues...");
        searchField.setPrefWidth(250);

        Button createBtn = new Button("+ Create Issue");
        Button filterBtn = new Button("Filter");

        HBox header = new HBox(10, title, new Region(), searchField, filterBtn, createBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        backlogList = new VBox(20);
        backlogList.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(backlogList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.setTop(header);
        root.setCenter(scrollPane);

        filterBtn.setOnAction(e -> openFilterDialog());
        searchField.textProperty().addListener((obs, oldV, newV) -> applySearch(newV));

        loadUserStories();

        return root;
    }

    private void loadUserStories() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/userstories"))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(req, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Platform.runLater(() -> updateUI(response.body()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUI(String json) {
        fullData = new JSONArray(json); 
        renderList(fullData);
    }

    private void renderList(JSONArray data) {
        backlogList.getChildren().clear();

        if (data.length() == 0) {
            Label empty = new Label("No backlog items match.");
            empty.setStyle("-fx-text-fill: #777; -fx-font-size: 14;");
            empty.setPadding(new Insets(20));
            backlogList.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);

            backlogList.getChildren().add(
                    createCard(
                            obj.getLong("id"),
                            obj.optString("title", "Untitled"),
                            obj.optString("description", ""),
                            obj.optInt("storyPoints", 0),
                            obj.optString("assignedTo", ""),
                            obj.optString("priority", "Medium"),
                            obj.optString("status", "To Do")
                    )
            );
        }
    }

    private void applySearch(String keyword) {
        keyword = keyword.toLowerCase();

        JSONArray result = new JSONArray();

        for (int i = 0; i < fullData.length(); i++) {
            JSONObject obj = fullData.getJSONObject(i);

            String title = obj.optString("title", "").toLowerCase();
            String desc = obj.optString("description", "").toLowerCase();

            if (title.contains(keyword) || desc.contains(keyword)) {
                result.put(obj);
            }
        }

        renderList(result);
    }

    private void openFilterDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Filter Backlog");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Backlog","All", "To Do", "In Progress", "Done");
        statusBox.setValue("All");

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("All", "Low", "Medium", "High");
        priorityBox.setValue("All");

        ButtonType applyBtn = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        ButtonType clearBtn = new ButtonType("Clear", ButtonBar.ButtonData.BACK_PREVIOUS);

        dialog.getDialogPane().getButtonTypes().addAll(applyBtn, clearBtn, ButtonType.CANCEL);

        VBox content = new VBox(15,
                new Label("Status:"), statusBox,
                new Label("Priority:"), priorityBox
        );
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == applyBtn) {
                applyFilter(statusBox.getValue(), priorityBox.getValue());
            } else if (btn == clearBtn) {
                renderList(fullData); 
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void applyFilter(String status, String priority) {
        JSONArray filtered = new JSONArray();

        for (int i = 0; i < fullData.length(); i++) {
            JSONObject obj = fullData.getJSONObject(i);

            boolean matchStatus = status.equals("All") ||
                    obj.optString("status", "").equalsIgnoreCase(status);

            boolean matchPriority = priority.equals("All") ||
                    obj.optString("priority", "").equalsIgnoreCase(priority);

            if (matchStatus && matchPriority) {
                filtered.put(obj);
            }
        }

        renderList(filtered);
    }

    private VBox createCard(long id, String title, String desc, int points,
                            String assignee, String priority, String status) {

        Circle avatar = new Circle(18, Color.web("#e6f4ff"));
        Text initials = new Text(getInitials(assignee));
        initials.setStyle("-fx-font-weight: 700; -fx-fill: #0b66c2;");
        StackPane avatarPane = new StackPane(avatar, initials);

        Label statusBadge = new Label(status);
        statusBadge.setStyle(
                "-fx-background-color: #eef2ff; -fx-text-fill: #4338ca;" +
                        "-fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11;"
        );

        Label priorityBadge = new Label(priority);
        priorityBadge.setStyle(
                "-fx-background-color: #fef3c7; -fx-text-fill: #92400e;" +
                        "-fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #111827;");

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");
        descLabel.setWrapText(true);

        Label pointsLabel = new Label("Story Points: " + points);
        pointsLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 12;");

        Button editBtn = new Button("Edit");
        editBtn.setStyle(
                "-fx-background-color: #2563eb; -fx-text-fill: white;" +
                        "-fx-padding: 6 14; -fx-background-radius: 8;"
        );

        editBtn.setOnAction(e -> {
            UC04EditUserStory editor = new UC04EditUserStory(id);
            editor.openWindow();
        });

        HBox badges = new HBox(10, statusBadge, priorityBadge);
        badges.setAlignment(Pos.CENTER_LEFT);

        HBox headerRow = new HBox(12, avatarPane, titleLabel);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(8, headerRow, descLabel, badges, pointsLabel, editBtn);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );

        return card;
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) sb.append(p.charAt(0));
        return sb.toString().toUpperCase();
    }
}
