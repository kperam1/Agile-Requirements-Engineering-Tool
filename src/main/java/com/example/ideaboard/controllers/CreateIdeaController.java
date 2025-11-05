package com.example.ideaboard.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for Create Idea modal.
 * - initialize(): sets defaults
 * - createIdea(): validates title and POSTs JSON to backend asynchronously
 * - cancel(): clears fields and closes the window if possible
 */
public class CreateIdeaController {
    @FXML private TextField titleField;
    @FXML private ChoiceBox<String> categoryBox;
    @FXML private TextArea descriptionArea;
    @FXML private ChoiceBox<String> statusBox;
    @FXML private TextField ownerField;
    @FXML private Button submitButton;

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final URI CREATE_URI = URI.create("http://localhost:8080/api/ideas");

    @FXML
    public void initialize() {
        // sanity checks
        assert titleField != null : "fx:id=\"titleField\" not injected";
        assert categoryBox != null : "fx:id=\"categoryBox\" not injected";
        assert descriptionArea != null : "fx:id=\"descriptionArea\" not injected";
        assert statusBox != null : "fx:id=\"statusBox\" not injected";
        assert ownerField != null : "fx:id=\"ownerField\" not injected";
        assert submitButton != null : "fx:id=\"submitButton\" not injected";

        // defaults
        Platform.runLater(() -> {
            if (categoryBox.getItems() != null && !categoryBox.getItems().isEmpty()) {
                categoryBox.setValue("Product Enhancement");
            }
            if (statusBox.getItems() != null && !statusBox.getItems().isEmpty()) {
                statusBox.setValue("New");
            }
        });
    }

    @FXML
    private void createIdea() {
        // Validate non-empty title
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation", null, "Please enter a title for your idea.");
            titleField.requestFocus();
            return;
        }

        String category = categoryBox.getValue();
        String status = statusBox.getValue();
        String owner = ownerField.getText();
        String description = descriptionArea.getText();

    // Build JSON payload
    String json = String.format("{\"title\":\"%s\",\"category\":\"%s\",\"status\":\"%s\",\"ownerName\":\"%s\",\"description\":\"%s\"}",
        jsonEscape(title),
        jsonEscape(category),
        jsonEscape(status),
        jsonEscape(owner),
        jsonEscape(description)
    );

        HttpRequest req = HttpRequest.newBuilder(CREATE_URI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        // Send async to avoid blocking FX thread
        CompletableFuture<HttpResponse<String>> future = HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        future.thenAccept(response -> {
            int statusCode = response.statusCode();
            String body = response.body();
            if (statusCode == 201) {
                showSuccess("Idea Created", "Saved to database", "Title: " + title);
                clearForm();
            } else {
                showError("Create failed (HTTP " + statusCode + ")\n" + body);
            }
        }).exceptionally(ex -> {
            showError("Network error: " + ex.getMessage());
            return null;
        });
    }

    @FXML
    private void cancel() {
        clearForm();
        // close window if this was opened in a stage
        if (submitButton != null && submitButton.getScene() != null) {
            Window w = submitButton.getScene().getWindow();
            if (w instanceof Stage) {
                ((Stage) w).close();
            }
        }
    }

    private void clearForm() {
        Platform.runLater(() -> {
            titleField.clear();
            ownerField.clear();
            descriptionArea.clear();
            if (categoryBox.getItems() != null && !categoryBox.getItems().isEmpty()) categoryBox.setValue("Product Enhancement");
            if (statusBox.getItems() != null && !statusBox.getItems().isEmpty()) statusBox.setValue("New");
        });
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert a = new Alert(type);
            a.setTitle(title);
            a.setHeaderText(header);
            a.setContentText(content);
            a.showAndWait();
        });
    }

    private void showSuccess(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    private void showError(String content) {
        showAlert(Alert.AlertType.ERROR, "Error", null, content);
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String nullSafe(String s) { return s == null ? "" : s; }
}
