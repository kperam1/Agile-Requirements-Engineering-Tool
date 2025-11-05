import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;

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
        // Initialize defaults after FXML loading
        assert categoryBox != null : "fx:id=\"categoryBox\" was not injected";
        assert statusBox != null : "fx:id=\"statusBox\" was not injected";
        assert titleField != null : "fx:id=\"titleField\" was not injected";
        assert descriptionArea != null : "fx:id=\"descriptionArea\" was not injected";
        assert ownerField != null : "fx:id=\"ownerField\" was not injected";
        assert submitButton != null : "fx:id=\"submitButton\" was not injected";
        
        Platform.runLater(() -> {
            if (categoryBox != null && categoryBox.getItems() != null && !categoryBox.getItems().isEmpty()) {
                categoryBox.setValue("Product Enhancement");
            }
            if (statusBox != null && statusBox.getItems() != null && !statusBox.getItems().isEmpty()) {
                statusBox.setValue("New");
            }
        });
    }

    @FXML
    private void createIdea() {
        try {
            // Validate required fields
            if (titleField == null || titleField.getText() == null || titleField.getText().trim().isEmpty()) {
                showError("Please enter a title for your idea");
                if (titleField != null) titleField.requestFocus();
                return;
            }

            String title = titleField.getText().trim();
            String category = categoryBox.getValue();
            String status = statusBox.getValue();
            String owner = ownerField.getText();
            String description = descriptionArea.getText();

            // Validate selections
            if (category == null || category.isEmpty()) {
                showError("Please select a category");
                categoryBox.requestFocus();
                return;
            }
            if (status == null || status.isEmpty()) {
                showError("Please select a status");
                statusBox.requestFocus();
                return;
            }

            String json = String.format("""
                {
                  "title": "%s",
                  "category": "%s",
                  "status": "%s",
                  "ownerName": "%s",
                  "description": "%s"
                }
                """, 
                escape(title),
                escape(category),
                escape(status),
                escape(owner),
                escape(description)
            );

            HttpRequest req = HttpRequest.newBuilder(CREATE_URI)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 201) {
                showSuccess("Idea Created", 
                          "Saved to database successfully",
                          String.format("Title: %s%nOwner: %s%nStatus: %s", title, owner, status));
                clearForm();
            } else {
                showError("Create failed (HTTP " + res.statusCode() + "): " + res.body());
            }
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void cancel() {
        clearForm();
        // Optionally close the window if loaded in a Dialog
        if (submitButton != null) {
            Window window = submitButton.getScene().getWindow();
            if (window instanceof Stage) {
                ((Stage) window).close();
            }
        }
    }

    private void clearForm() {
        Platform.runLater(() -> {
            if (titleField != null) titleField.clear();
            if (ownerField != null) ownerField.clear();
            if (descriptionArea != null) descriptionArea.clear();
            if (categoryBox != null && categoryBox.getItems() != null) {
                categoryBox.setValue("Product Enhancement");
            }
            if (statusBox != null && statusBox.getItems() != null) {
                statusBox.setValue("New");
            }
        });
    }

    private void showSuccess(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}