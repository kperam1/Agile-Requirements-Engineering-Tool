package com.example.ideaboard.frontend;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;

public class ReportController {

    @FXML private TextField sprintIdField;
    @FXML private Button generateButton;
    @FXML private Label statusLabel;

    @FXML private Label velocityPointsLabel;
    @FXML private Label storiesCompletedLabel;
    @FXML private TextArea summaryArea;

    @FXML private LineChart<Number, Number> burndownChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML
    public void initialize() {
        statusLabel.setText("");
        velocityPointsLabel.setText("-");
        storiesCompletedLabel.setText("-");
        summaryArea.setText("");

        xAxis.setLabel("Day");
        yAxis.setLabel("Remaining Story Points");

        loadDummyChart();
    }

    @FXML
    private void onGenerateReports() {
        statusLabel.setText("");

        String sprintIdText = sprintIdField.getText().trim();
        if (sprintIdText.isEmpty()) {
            statusLabel.setText("Please enter a Sprint ID.");
            return;
        }

        int sprintId;
        try {
            sprintId = Integer.parseInt(sprintIdText);
        } catch (Exception ex) {
            statusLabel.setText("Sprint ID must be a number.");
            return;
        }

        int completedPoints = 34;
        int storiesCompleted = 7;

        velocityPointsLabel.setText(String.valueOf(completedPoints));
        storiesCompletedLabel.setText(String.valueOf(storiesCompleted));

        summaryArea.setText(
            "Sprint " + sprintId + " Summary:\n" +
            "- Completed story points: " + completedPoints + "\n" +
            "- Stories completed: " + storiesCompleted + "\n" +
            "- Velocity trend: stable (dummy data)"
        );

        loadDummyChart();
        statusLabel.setText("Reports generated (dummy data). Backend next.");
    }

    private void loadDummyChart() {
        burndownChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Burndown");

        series.getData().add(new XYChart.Data<>(1, 40));
        series.getData().add(new XYChart.Data<>(2, 36));
        series.getData().add(new XYChart.Data<>(3, 32));
        series.getData().add(new XYChart.Data<>(4, 28));
        series.getData().add(new XYChart.Data<>(5, 22));
        series.getData().add(new XYChart.Data<>(6, 16));
        series.getData().add(new XYChart.Data<>(7, 10));
        series.getData().add(new XYChart.Data<>(8, 6));
        series.getData().add(new XYChart.Data<>(9, 3));
        series.getData().add(new XYChart.Data<>(10, 0));

        burndownChart.getData().add(series);
    }
}
