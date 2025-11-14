package com.example.agile_re_tool;

import com.example.agile_re_tool.model.CommentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ViewStoryWithComments extends Application {

    private long storyId = 1L; // The story we're viewing
    private String currentUser = "Current User"; // You can make this dynamic
    private VBox commentsContainer;
    private TextArea commentInput;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UC-12 - View Story with Comments");

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Top: Story Title and Details
        VBox topSection = createStorySection();
        root.setTop(topSection);

        // Center: Comments Section
        VBox commentsSection = createCommentsSection();
        ScrollPane scrollPane = new ScrollPane(commentsSection);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");
        root.setCenter(scrollPane);

        // Bottom: Add Comment Section
        VBox addCommentSection = createAddCommentSection();
        root.setBottom(addCommentSection);

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/uc03-style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load comments when the window opens
        loadComments();
    }

    private VBox createStorySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(0, 0, 20, 0));
        section.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 0 0 2 0;");

        Label titleLabel = new Label("User Story #" + storyId);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label storyTitle = new Label("As a user, I want to see comments");
        storyTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #374151;");

        section.getChildren().addAll(titleLabel, storyTitle);
        return section;
    }

    private VBox createCommentsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20, 0, 20, 0));

        Label header = new Label("Comments");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        commentsContainer = new VBox(12);
        commentsContainer.setPadding(new Insets(10, 0, 0, 0));

        section.getChildren().addAll(header, commentsContainer);
        return section;
    }

    private VBox createAddCommentSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20, 0, 0, 0));
        section.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 2 0 0 0;");

        Label header = new Label("Add a Comment");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        commentInput = new TextArea();
        commentInput.setPromptText("Write your comment here...");
        commentInput.setPrefRowCount(3);
        commentInput.setWrapText(true);
        commentInput.setStyle("-fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-background-radius: 8;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Clear");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #cbd5e1; " +
                "-fx-text-fill: #374151; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setOnAction(e -> commentInput.clear());

        Button submitBtn = new Button("Post Comment");
        submitBtn.setStyle("-fx-background-color: linear-gradient(#2563eb, #1e40af); " +
                "-fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 16;");
        submitBtn.setOnAction(e -> addComment());

        buttonBox.getChildren().addAll(cancelBtn, submitBtn);

        section.getChildren().addAll(header, commentInput, buttonBox);
        return section;
    }

    private VBox createCommentCard(CommentDTO comment) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 10; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-border-width: 1;");

        // Header with avatar and author
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        Circle avatar = new Circle(20, Color.web("#3b82f6"));
        String initials = getInitials(comment.getAuthor());
        Label avatarText = new Label(initials);
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        StackPane avatarPane = new StackPane(avatar, avatarText);

        // Author and timestamp
        VBox authorInfo = new VBox(2);
        Label authorLabel = new Label(comment.getAuthor());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #111827;");

        String timeAgo = formatTimeAgo(comment.getCreatedAt());
        Label timeLabel = new Label(timeAgo);
        timeLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        authorInfo.getChildren().addAll(authorLabel, timeLabel);

        // Action buttons
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; " +
                "-fx-font-size: 12px; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> deleteComment(comment.getId()));

        header.getChildren().addAll(avatarPane, authorInfo, spacer, deleteBtn);

        // Comment content
        Label contentLabel = new Label(comment.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px;");

        card.getChildren().addAll(header, contentLabel);
        return card;
    }

    private void loadComments() {
        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/comments/story/" + storyId))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    CommentDTO[] comments = objectMapper.readValue(response.body(), CommentDTO[].class);
                    
                    Platform.runLater(() -> {
                        commentsContainer.getChildren().clear();
                        if (comments.length == 0) {
                            Label noComments = new Label("No comments yet. Be the first to comment!");
                            noComments.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
                            commentsContainer.getChildren().add(noComments);
                        } else {
                            for (CommentDTO comment : comments) {
                                commentsContainer.getChildren().add(createCommentCard(comment));
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", 
                        "Failed to load comments: " + e.getMessage()));
            }
        }).start();
    }

    private void addComment() {
        String content = commentInput.getText();
        if (content == null || content.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Comment cannot be empty");
            return;
        }

        new Thread(() -> {
            try {
                Map<String, String> commentData = new HashMap<>();
                commentData.put("author", currentUser);
                commentData.put("content", content.trim());

                String jsonBody = objectMapper.writeValueAsString(commentData);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/comments/story/" + storyId))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    Platform.runLater(() -> {
                        commentInput.clear();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Comment posted successfully!");
                        loadComments(); // Reload to show the new comment
                    });
                } else {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", 
                            "Failed to post comment. Status: " + response.statusCode()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", 
                        "Failed to post comment: " + e.getMessage()));
            }
        }).start();
    }

    private void deleteComment(Long commentId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete this comment?", 
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            new Thread(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/comments/" + commentId))
                            .DELETE()
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Comment deleted successfully!");
                            loadComments(); // Reload to update the list
                        });
                    } else {
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", 
                                "Failed to delete comment. Status: " + response.statusCode()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", 
                            "Failed to delete comment: " + e.getMessage()));
                }
            }).start();
        }
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        } else {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
    }

    private String formatTimeAgo(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";
        
        java.time.Duration duration = java.time.Duration.between(dateTime, java.time.LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) return seconds + " seconds ago";
        if (seconds < 3600) return (seconds / 60) + " minutes ago";
        if (seconds < 86400) return (seconds / 3600) + " hours ago";
        if (seconds < 2592000) return (seconds / 86400) + " days ago";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        return dateTime.format(formatter);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type, message, ButtonType.OK);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
