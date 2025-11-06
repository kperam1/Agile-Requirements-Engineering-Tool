package com.agile.requirements.controller;

import com.agile.requirements.config.StageManager;
import com.agile.requirements.dto.AuthResponse;
import com.agile.requirements.dto.LoginRequest;
import com.agile.requirements.service.ApiService;
import com.agile.requirements.view.FxmlView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Login Controller with API Integration
 * Handles user authentication via REST API
 */
@Controller
public class LoginControllerApi implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink signupLink;

    @FXML
    private Label messageLabel;

    @FXML
    private CheckBox rememberMeCheckBox;

    @Autowired
    private ApiService apiService;

    @Autowired
    private StageManager stageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Any initialization logic can go here
    }

    /**
     * Handle login button action
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        // Clear previous messages
        messageLabel.setText("");

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Disable button to prevent double submission
        loginButton.setDisabled(true);
        showInfo("Logging in...");

        // Call API in background thread
        new Thread(() -> {
            try {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();

                // Create login request
                LoginRequest request = new LoginRequest(username, password);

                // Call API
                AuthResponse response = apiService.login(request);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    loginButton.setDisabled(false);
                    
                    if (response.isSuccess()) {
                        showSuccess("Welcome back, " + response.getUser().getFirstName() + "!");
                        
                        // TODO: Navigate to main dashboard
                        // stageManager.switchScene(FxmlView.DASHBOARD);
                        
                    } else {
                        showError(response.getMessage());
                        passwordField.clear();
                        passwordField.requestFocus();
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    loginButton.setDisabled(false);
                    showError("Failed to connect to server. Please ensure the application is running.");
                });
            }
        }).start();
    }

    /**
     * Handle signup link action
     */
    @FXML
    private void handleSignupLink(ActionEvent event) {
        stageManager.switchScene(FxmlView.SIGNUP);
    }

    /**
     * Validate all input fields
     */
    private boolean validateInputs() {
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username is required!");
            usernameField.requestFocus();
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            showError("Password is required!");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #d32f2f;");
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #388e3c;");
    }

    /**
     * Show info message
     */
    private void showInfo(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #1976d2;");
    }
}
