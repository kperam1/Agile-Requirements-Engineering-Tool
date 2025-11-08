package com.example.ideaboard.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreateIdeaController {

    @FXML
    private TextField titleField;

    @FXML
    private ChoiceBox<String> categoryChoice;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ChoiceBox<String> statusChoice;

    @FXML
    private TextField ownerNameField;

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
        categoryChoice.setValue("Product Enhancement"); // Default value

        statusChoice.getItems().addAll(
            "New",
            "Under Review",
            "Approved",
            "In Progress",
            "Completed",
            "Rejected"
        );
        statusChoice.setValue("New"); // Default value
    }

    @FXML
    private void createIdea() {
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
            
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/ideas"))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();
            
            java.net.http.HttpResponse<String> response = client.send(
                request, 
                java.net.http.HttpResponse.BodyHandlers.ofString()
            );
            
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                showSuccess(
                    "Success",
                    String.format("Idea '%s' created successfully!", title)
                );
                clearFields();
                closeWindow();
            } else {
                showError(
                    "Error", 
                    "Failed to create idea. Server returned: " + response.statusCode()
                );
            }
            
        } catch (java.io.IOException e) {
            showError(
                "Connection Error",
                "Cannot connect to backend server.\nMake sure the Spring Boot API is running on http://localhost:8080"
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showError("Error", "Request was interrupted");
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
        statusChoice.setValue("New");
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
