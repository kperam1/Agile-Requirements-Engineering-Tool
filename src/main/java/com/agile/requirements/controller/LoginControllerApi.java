package com.agile.requirements.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import com.example.agile_re_tool.session.UserSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginControllerApi {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            messageLabel.setText("Username and password are required.");
            return;
        }

        try {
            String apiUrl = String.format("http://localhost:8080/api/auth/login?username=%s&password=%s",
                    username, password);
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();

                if (response != null && !response.equals("null") && !response.isBlank()) {
                    UserSession.setCurrentUser(username);
                    openWorkspace();
                } else {
                    messageLabel.setText("Invalid credentials. Try again.");
                }
            } else {
                messageLabel.setText("Server error. Try again.");
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Backend not reachable.");
        }
    }

    @FXML
    private void handleSignupLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/auth.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openWorkspace() {
        try {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
            new com.example.agile_re_tool.ui.Workspace().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading workspace.");
        }
    }
}
