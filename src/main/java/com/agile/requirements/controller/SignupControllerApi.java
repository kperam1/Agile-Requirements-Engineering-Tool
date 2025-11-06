package com.agile.requirements.controller;

import com.agile.requirements.config.StageManager;
import com.agile.requirements.dto.AuthResponse;
import com.agile.requirements.dto.SignupRequest;
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
 * Signup Controller with API Integration
 * Handles user registration via REST API
 */
@Controller
public class SignupControllerApi implements Initializable {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button signupButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private Label messageLabel;

    @FXML
    private Button googleSignupButton;

    @FXML
    private Button githubSignupButton;

    @FXML
    private Button microsoftSignupButton;

    @FXML
    private Button yahooSignupButton;

    @Autowired
    private ApiService apiService;

    @Autowired
    private StageManager stageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize role combo box
        roleComboBox.getItems().addAll(
            "Product Owner",
            "Scrum Master",
            "Developer",
            "Tester",
            "Stakeholder"
        );
        roleComboBox.getSelectionModel().selectFirst();
    }

    //Handle signup button action
    @FXML
    private void handleSignup(ActionEvent event) {
        // Clear previous messages
        messageLabel.setText("");

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Check if passwords match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match!");
            return;
        }

        // Disable button to prevent double submission
        signupButton.setDisable(true);
        showInfo("Creating account...");

        // Call API in background thread
        new Thread(() -> {
            try {
                // Create signup request
                SignupRequest request = new SignupRequest();
                request.setFirstName(firstNameField.getText().trim());
                request.setLastName(lastNameField.getText().trim());
                request.setEmail(emailField.getText().trim().toLowerCase());
                request.setUsername(usernameField.getText().trim());
                request.setPassword(passwordField.getText());
                request.setRole(roleComboBox.getValue());

                // Call API
                AuthResponse response = apiService.signup(request);

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    signupButton.setDisable(false);
                    
                    if (response.isSuccess()) {
                        showSuccess(response.getMessage());
                        clearForm();
                        
                        // Auto-switch to login after 2 seconds
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                Platform.runLater(() -> stageManager.switchScene(FxmlView.LOGIN));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else {
                        showError(response.getMessage());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    signupButton.setDisable(false);
                    showError("Failed to connect to server. Please ensure the application is running.");
                });
            }
        }).start();
    }

    //Handle login link action
    @FXML
    private void handleLoginLink(ActionEvent event) {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    //Handle Google signup
    @FXML
    private void handleGoogleSignup(ActionEvent event) {
        showInfo("Google OAuth integration coming soon...");
        // This would typically open a browser for OAuth authentication
        // and handle the callback to create/login the user
    }

    //Handle GitHub signup
    @FXML
    private void handleGitHubSignup(ActionEvent event) {
        showInfo("GitHub OAuth integration coming soon...");
    }

    //Handle Microsoft signup
    @FXML
    private void handleMicrosoftSignup(ActionEvent event) {
        showInfo("Microsoft OAuth integration coming soon...");
    }

    //Handle Yahoo signup
    @FXML
    private void handleYahooSignup(ActionEvent event) {
        showInfo("Yahoo OAuth integration coming soon...");
    }

    //Validate all input fields
    private boolean validateInputs() {
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required!");
            firstNameField.requestFocus();
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required!");
            lastNameField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showError("Email is required!");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(emailField.getText().trim())) {
            showError("Please enter a valid email address!");
            emailField.requestFocus();
            return false;
        }

        if (usernameField.getText().trim().isEmpty()) {
            showError("Username is required!");
            usernameField.requestFocus();
            return false;
        }

        if (usernameField.getText().trim().length() < 3) {
            showError("Username must be at least 3 characters long!");
            usernameField.requestFocus();
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            showError("Password is required!");
            passwordField.requestFocus();
            return false;
        }

        if (passwordField.getText().length() < 6) {
            showError("Password must be at least 6 characters long!");
            passwordField.requestFocus();
            return false;
        }

        if (confirmPasswordField.getText().isEmpty()) {
            showError("Please confirm your password!");
            confirmPasswordField.requestFocus();
            return false;
        }

        return true;
    }

    //Validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    //Show error message
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #d32f2f;");
    }

    //Show success message
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #388e3c;");
    }

    //Show info message
    private void showInfo(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #1976d2;");
    }

    //Clear all form fields
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.getSelectionModel().selectFirst();
    }
}
