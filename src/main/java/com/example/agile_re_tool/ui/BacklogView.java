package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.UC04EditUserStory;
import com.example.agile_re_tool.UC03CreateUserStory;
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

        createBtn.setOnAction(e -> {
            UC03CreateUserStory createWindow = new UC03CreateUserStory();
            createWindow.openWindow();
        });

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
        backlogList.getChildren().clear();

        JSONArray arr = new JSONArray(json);

        if (arr.length() == 0) {
            Label empty = new Label("No backlog items yet.");
            empty.setStyle("-fx-text-fill: #777; -fx-font-size: 14;");
            empty.setPadding(new Insets(20));
            backlogList.getChildren().add(empty);
            return;
        }

        boolean addedAny = false;

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            long id = obj.getLong("id");
            String title = obj.optString("title", "Untitled");
            String desc = obj.optString("description", "");
            int points = obj.optInt("storyPoints", 0);
            String assignee = obj.optString("assignedTo", "");
            String priority = obj.optString("priority", "Medium");
            String status = obj.optString("status", "Backlog");

            if (!status.equalsIgnoreCase("Backlog")) {
                continue;
            }

            addedAny = true;

            backlogList.getChildren().add(
                    createCard(id, title, desc, points, assignee, priority, status)
            );
        }

        if (!addedAny) {
            Label empty = new Label("No backlog items yet.");
            empty.setStyle("-fx-text-fill: #777; -fx-font-size: 14;");
            empty.setPadding(new Insets(20));
            backlogList.getChildren().add(empty);
        }
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
