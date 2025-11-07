package com.taiga.storyapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class AcceptanceCriteriaController {

    @FXML private TextArea criteriaArea;
    @FXML private Label helperLabel;
    @FXML private Label charCount;

    private static final int MAX = 1000;

    @FXML
    public void initialize() {
        // live character count
        criteriaArea.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > MAX) {
                criteriaArea.setText(newV.substring(0, MAX));
            }
            charCount.setText(criteriaArea.getText().length() + " / " + MAX);
            updateHelper();
        });

        updateHelper();
    }

    private void updateHelper() {
        String t = criteriaArea.getText().toLowerCase();
        boolean hasGiven = t.contains("given");
        boolean hasWhen = t.contains("when");
        boolean hasThen = t.contains("then");

        if (criteriaArea.getText().isBlank()) {
            helperLabel.setText("Tip: Use Given / When / Then.");
            helperLabel.setStyle("-fx-text-fill:#6b7280;");
        } else if (!(hasGiven && hasWhen && hasThen)) {
            helperLabel.setText("Include all parts: Given, When, Then.");
            helperLabel.setStyle("-fx-text-fill:#ef4444;"); // red-ish
        } else {
            helperLabel.setText("Looks good.");
            helperLabel.setStyle("-fx-text-fill:#16a34a;"); // green-ish
        }
    }

    /** Call this later from the overall form to read the value */
    public String getAcceptanceCriteria() {
        return criteriaArea.getText().trim();
    }
}
