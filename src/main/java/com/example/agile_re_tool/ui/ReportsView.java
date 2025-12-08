package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.session.ProjectSession;
import com.example.agile_re_tool.util.SprintFetcher;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportsView {

    private ComboBox<String> sprintDropdown;
    private LinkedHashMap<String, Long> sprintMap;
    private LineChart<String, Number> chart;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Reports");
        title.setStyle("-fx-font-size:24px; -fx-font-weight:700;");

        sprintDropdown = new ComboBox<>();
        sprintDropdown.setPrefWidth(220);

        Button loadBtn = new Button("Load Burndown");
        loadBtn.setOnAction(e -> loadBurndown());

        HBox controls = new HBox(12, sprintDropdown, loadBtn);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10, 0, 20, 0));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setPrefHeight(500);
        chart.setLegendVisible(true);

        VBox top = new VBox(title, controls);
        top.setSpacing(10);

        root.setTop(top);
        root.setCenter(chart);

        loadSprints();

        return root;
    }

    private void loadSprints() {
        if (!ProjectSession.hasProjectSelected()) {
            sprintDropdown.getItems().clear();
            return;
        }

        sprintMap = SprintFetcher.fetchSprints();
        sprintDropdown.getItems().clear();
        sprintDropdown.getItems().addAll(sprintMap.keySet());
    }

    private void loadBurndown() {
        String selectedSprint = sprintDropdown.getValue();
        if (selectedSprint == null) return;

        long sprintId = sprintMap.get(selectedSprint);

        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/sprints/" + sprintId + "/burndown";

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

                JSONObject json = new JSONObject(resp.body());

                Platform.runLater(() -> drawChart(json));

            } catch (Exception ignored) {}
        }).start();
    }

    private void drawChart(JSONObject data) {
        chart.getData().clear();

        XYChart.Series<String, Number> actual = new XYChart.Series<>();
        actual.setName("Actual Burn");

        XYChart.Series<String, Number> ideal = new XYChart.Series<>();
        ideal.setName("Ideal Burn");

        int totalPoints = 0;
        for (String key : data.keySet()) {
            int remaining = data.getInt(key);
            totalPoints = Math.max(totalPoints, remaining);
        }

        LocalDate start = LocalDate.parse(data.keys().next());
        LocalDate end = LocalDate.parse(data.keys().next());
        for (String key : data.keySet()) {
            LocalDate d = LocalDate.parse(key);
            if (d.isBefore(start)) start = d;
            if (d.isAfter(end)) end = d;
        }

        int days = (int) (end.toEpochDay() - start.toEpochDay());
        double idealStep = totalPoints / (double) days;

        int index = 0;
        for (String date : data.keySet()) {
            actual.getData().add(new XYChart.Data<>(date, data.getInt(date)));

            double idealVal = totalPoints - (idealStep * index);
            if (idealVal < 0) idealVal = 0;
            ideal.getData().add(new XYChart.Data<>(date, idealVal));

            index++;
        }

        chart.getData().addAll(actual, ideal);
    }
}
