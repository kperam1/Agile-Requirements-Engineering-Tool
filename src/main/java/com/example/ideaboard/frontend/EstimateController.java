package com.example.ideaboard.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class EstimateController {
    @FXML private TextField storyIdField;
    @FXML private TextField pointsField;
    @FXML private TextField estimatedByField;
    @FXML private TextArea latestArea;
    @FXML private ListView<String> historyList;
    @FXML private Label statusLabel;

    private final EstimateService service = new EstimateService();
    private final ObservableList<String> historyItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        historyList.setItems(historyItems);
    }

    @FXML
    private void onAddEstimate(MouseEvent e) {
        statusLabel.setText("");
        try {
            long storyId = Long.parseLong(storyIdField.getText().trim());
            int points = Integer.parseInt(pointsField.getText().trim());
            String by = estimatedByField.getText().trim();
            String resp = service.addEstimate(storyId, points, by);
            statusLabel.setText("Saved ✅");
            refresh(storyId);
        } catch (NumberFormatException nfe) {
            statusLabel.setText("Enter valid numbers for Story ID and Points.");
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void onRefresh(MouseEvent e) {
        statusLabel.setText("");
        try {
            long storyId = Long.parseLong(storyIdField.getText().trim());
            refresh(storyId);
        } catch (NumberFormatException nfe) {
            statusLabel.setText("Enter a valid Story ID.");
        }
    }

    private void refresh(long storyId) {
        try {
            String latest = service.getLatest(storyId);
            latestArea.setText(latest == null || latest.isBlank() ? "(no latest)" : latest);
            String history = service.getHistory(storyId);
            historyItems.clear();
            historyItems.add(history == null || history.isBlank() ? "(no history)" : history);
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }
}
