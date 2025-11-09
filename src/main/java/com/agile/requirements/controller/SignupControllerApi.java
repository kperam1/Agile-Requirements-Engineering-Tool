package com.agile.requirements.controller;

import com.agile.requirements.config.StageManager;
import com.agile.requirements.service.UserService;
import com.agile.requirements.view.FxmlView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class SignupControllerApi implements Initializable {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button signupButton;
    @FXML private Hyperlink loginLink;
    @FXML private Label messageLabel;

    private final UserService userService;
    private final StageManager stageManager;

    public SignupControllerApi(UserService userService, StageManager stageManager) {
        this.userService = userService;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.getItems().setAll(
            "Product Owner",
            "Scrum Master",
            "Developer",
            "Tester/QA",
            "Business Analyst",
            "Stakeholder",
            "Project Manager"
        );
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        messageLabel.setText("");
        if (!validateInputs()) return;

        try {
            userService.register(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    usernameField.getText().trim(),
                    passwordField.getText(),
                    roleComboBox.getValue()
            );
            messageLabel.setStyle("-fx-text-fill:#388e3c;");
            messageLabel.setText("Account created. You can log in now.");
        } catch (Exception ex) {
            messageLabel.setStyle("-fx-text-fill:#d32f2f;");
            messageLabel.setText("Signup failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleLoginLink(ActionEvent event) {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    private boolean validateInputs() {
        if (firstNameField.getText().trim().isEmpty()) { showErr("First name required"); return false; }
        if (lastNameField.getText().trim().isEmpty()) { showErr("Last name required"); return false; }
        if (emailField.getText().trim().isEmpty()) { showErr("Email required"); return false; }
        if (usernameField.getText().trim().isEmpty()) { showErr("Username required"); return false; }
        if (passwordField.getText().isEmpty()) { showErr("Password required"); return false; }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) { showErr("Passwords do not match"); return false; }
        if (roleComboBox.getValue() == null) { showErr("Role required"); return false; }
        return true;
    }

    private void showErr(String msg) { messageLabel.setStyle("-fx-text-fill:#d32f2f;"); messageLabel.setText(msg); }
}
