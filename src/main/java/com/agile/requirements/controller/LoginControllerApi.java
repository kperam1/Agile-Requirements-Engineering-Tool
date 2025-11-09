package com.agile.requirements.controller;

import com.agile.requirements.config.StageManager;
import com.agile.requirements.service.UserService;
import com.agile.requirements.view.FxmlView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Simplified login controller: directly uses UserService (no HTTP layer) for basic auth demo.
 */
@Controller
public class LoginControllerApi implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink signupLink;
    @FXML private Label messageLabel;
    @FXML private CheckBox rememberMeCheckBox;

    @Autowired private UserService userService;
    @Autowired private StageManager stageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    @FXML
    private void handleLogin(ActionEvent event) {
        messageLabel.setText("");
        if (!validateInputs()) return;

        loginButton.setDisable(true);
        showInfo("Authenticating...");

        // Run auth in background to avoid UI freeze (trivial here but scalable later)
        new Thread(() -> {
            boolean success = userService.authenticate(
                    usernameField.getText().trim(),
                    passwordField.getText()
            ).isPresent();

            javafx.application.Platform.runLater(() -> {
                loginButton.setDisable(false);
                if (success) {
                    showSuccess("Login successful");
                    // TODO: switch to main workspace/dashboard scene when implemented
                } else {
                    showError("Invalid credentials");
                    passwordField.clear();
                    passwordField.requestFocus();
                }
            });
        }).start();
    }

    @FXML
    private void handleSignupLink(ActionEvent event) {
        stageManager.switchScene(FxmlView.SIGNUP);
    }

    private boolean validateInputs() {
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username required");
            usernameField.requestFocus();
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            showError("Password required");
            passwordField.requestFocus();
            return false;
        }
        return true;
    }

    private void showError(String msg) { messageLabel.setText(msg); messageLabel.setStyle("-fx-text-fill:#d32f2f;"); }
    private void showSuccess(String msg) { messageLabel.setText(msg); messageLabel.setStyle("-fx-text-fill:#388e3c;"); }
    private void showInfo(String msg) { messageLabel.setText(msg); messageLabel.setStyle("-fx-text-fill:#1976d2;"); }
}
