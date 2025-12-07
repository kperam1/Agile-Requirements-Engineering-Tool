package com.example.ideaboard.controllers;

import com.example.agile_re_tool.session.ProjectSession;
import com.example.ideaboard.util.DialogHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreateIdeaController {

    @FXML private TextField titleField;
    @FXML private ChoiceBox<String> categoryChoice;
    @FXML private TextArea descriptionArea;
    @FXML private ChoiceBox<String> statusChoice;
    @FXML private TextField ownerNameField;

    @FXML
    public void initialize() {
        categoryChoice.getItems().addAll(
                "Product Enhancement",
                "New Feature",
                "Bug Fix",
                "Process Improvement",
                "Research",
                "Other"
        );
        categoryChoice.setValue("Product Enhancement");

        statusChoice.getItems().addAll(
                "NEW",
                "UNDER_REVIEW",
                "APPROVED",
                "REJECTED"
        );
        statusChoice.setValue("NEW");
    }

    @FXML
    private void createIdea() {
        long projectId = ProjectSession.getProjectId();
        if (projectId <= 0) {
            showError("No Project Selected", "Please select a project before creating an idea.");
            return;
        }

        String title = titleField.getText().trim();
        String owner = ownerNameField.getText().trim();

        if (title.isEmpty()) {
            showError("Validation Error", "Title is required.");
            titleField.requestFocus();
            return;
        }

        String category = categoryChoice.getValue();
        String description = descriptionArea.getText().trim();
        String status = statusChoice.getValue();

        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

            String json = String.format("""
                {
                    "title": "%s",
                    "category": "%s",
                    "description": "%s",
                    "status": "%s",
                    "ownerName": "%s"
                }
                """,
                    escapeJson(title),
                    escapeJson(category),
                    escapeJson(description),
                    escapeJson(status),
                    escapeJson(owner)
            );

            String url = "http://localhost:8080/api/ideas/project/" + projectId;


            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(
                    request,
                    java.net.http.HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                showSuccess("Success", "Idea '" + title + "' created successfully.");
                clearFields();
                DialogHelper.notifyIdeaCreated();
                closeWindow();
            } else {
                showError("Error", "Failed to create idea. Server returned: " + response.statusCode());
            }

        } catch (java.io.IOException e) {
            showError("Connection Error",
                    "Cannot connect to backend server.\nIs the API running on http://localhost:8080?");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showError("Error", "Request was interrupted.");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @FXML
    private void cancel() {
        clearFields();
        closeWindow();
    }

    private void clearFields() {
        titleField.clear();
        categoryChoice.setValue("Product Enhancement");
        descriptionArea.clear();
        statusChoice.setValue("NEW");
        ownerNameField.clear();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
