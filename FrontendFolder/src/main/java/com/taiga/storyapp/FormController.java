package com.taiga.storyapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FormController {

    @FXML private TextArea criteriaArea;
    @FXML private Label helperLabel;
    @FXML private Label charCount;
    @FXML private Button saveBtn;

    @FXML private ComboBox<String> assigneeCb;
    @FXML private Spinner<Integer> storyPointsSp;
    @FXML private ComboBox<String> priorityCb;
    @FXML private ComboBox<String> statusCb;

    private static final int MAX = 1000;

    @FXML
    public void initialize() {
        // Assignee options
        assigneeCb.getItems().setAll("vtiruma3", "Sakshi Agarwal", "keerthana", "mounika");
        assigneeCb.getSelectionModel().selectFirst();

        // Story points spinner (1..20, default 3)
        SpinnerValueFactory.IntegerSpinnerValueFactory vf =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3, 1);
        storyPointsSp.setValueFactory(vf);
        storyPointsSp.setEditable(false);

        // Priority dropdown
        priorityCb.getItems().setAll("High", "Medium", "Low");
        priorityCb.getSelectionModel().select("Medium");

        // Status dropdown
        statusCb.getItems().setAll("To Do", "In Progress", "New", "Closed");
        statusCb.getSelectionModel().select("To Do");

        // Acceptance Criteria validation and char count
        criteriaArea.textProperty().addListener((obs, o, n) -> {
            if (n.length() > MAX) {
                criteriaArea.setText(n.substring(0, MAX));
            }
            charCount.setText(criteriaArea.getText().length() + " / " + MAX);
            validateCriteria();
        });

        // initial
        charCount.setText("0 / " + MAX);
        validateCriteria();
    }

    private void validateCriteria() {
        String t = criteriaArea.getText().trim().toLowerCase();
        boolean ok = !t.isBlank() && t.contains("given") && t.contains("when") && t.contains("then");

        if (criteriaArea.getText().isBlank()) {
            helperLabel.setText("Tip: Use Given / When / Then.");
            helperLabel.setStyle("-fx-text-fill:#6b7280;");
        } else if (!ok) {
            helperLabel.setText("Include all parts: Given, When, Then.");
            helperLabel.setStyle("-fx-text-fill:#ef4444;");
        } else {
            helperLabel.setText("Looks good.");
            helperLabel.setStyle("-fx-text-fill:#16a34a;");
        }
        saveBtn.setDisable(!ok);
    }

    @FXML
    private void onSave() {
        String ac = criteriaArea.getText().trim();
        String assignee = assigneeCb.getValue();
        int storyPoints = storyPointsSp.getValue();
        String priority = priorityCb.getValue();
        String status = statusCb.getValue();

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Add Task");
        a.setHeaderText("Captured Values");
        a.setContentText(
            "Acceptance Criteria:\n" + ac +
            "\n\nAssignee: " + assignee +
            "\nStory Points: " + storyPoints +
            "\nPriority: " + priority +
            "\nStatus: " + status
        );
        a.showAndWait();
        // later: send these to backend
    }

    @FXML
    private void onCancel() {
        criteriaArea.clear();
        // leave others as chosen
        validateCriteria();
    }

    // expose for future backend integration
    public String getAcceptanceCriteria() { return criteriaArea.getText().trim(); }
    public String getAssignee() { return assigneeCb.getValue(); }
    public int getStoryPoints() { return storyPointsSp.getValue(); }
    public String getPriority() { return priorityCb.getValue(); }
    public String getStatus() { return statusCb.getValue(); }
}
