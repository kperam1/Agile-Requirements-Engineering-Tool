package com.example.ideaboard.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for the Create New Idea modal form.
 */
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

    /**
     * Initializes the controller class.
     * Sets up default values for dropdowns.
     */
    @FXML
    public void initialize() {
        // Populate Category choices
        categoryChoice.getItems().addAll(
            "Product Enhancement",
            "New Feature",
            "Bug Fix",
            "Process Improvement",
            "Research",
            "Other"
        );
        categoryChoice.setValue("Product Enhancement"); // Default value

        // Populate Status choices
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

    /**
     * Handles the Create Idea button action.
     * Validates input and sends POST request to backend API.
     */
    @FXML
    private void createIdea() {
        // Validate required fields
        String title = titleField.getText().trim();
        String owner = ownerNameField.getText().trim();
        
        if (title.isEmpty()) {
            showError("Validation Error", "Title is required.");
            titleField.requestFocus();
            return;
        }

        // Get form values
        String category = categoryChoice.getValue();
        String description = descriptionArea.getText().trim();
        String status = statusChoice.getValue();

        // Send POST request to backend API
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
    
    /**
     * Escapes special characters in JSON strings.
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    /**
     * Handles the Cancel button action.
     * Clears fields and closes the window.
     */
    @FXML
    private void cancel() {
        clearFields();
        closeWindow();
    }

    /**
     * Clears all form fields to their default state.
     */
    private void clearFields() {
        titleField.clear();
        categoryChoice.setValue("Product Enhancement");
        descriptionArea.clear();
        statusChoice.setValue("New");
        ownerNameField.clear();
    }

    /**
     * Closes the modal window if it's in a dialog or stage.
     */
    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an error alert dialog.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a success alert dialog.
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
