package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.session.ProjectSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class SprintManagementView {

    private final String API_URL = "http://localhost:8080/api/sprints";

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Header
        Label title = new Label("Sprint Management");
        title.setFont(new Font(26));

        VBox header = new VBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));

        // ListView with custom cells
        ListView<JSONObject> sprintList = new ListView<>();
        sprintList.setPrefHeight(450);
        sprintList.setCellFactory(list -> sprintCellFactory(sprintList));

        // Action row
        Button refreshBtn = new Button("Refresh");
        Button createBtn = new Button("Create Sprint");

        refreshBtn.setOnAction(e -> loadSprints(sprintList));
        createBtn.setOnAction(e -> openCreateDialog(sprintList));

        HBox toolbar = new HBox(12, refreshBtn, createBtn);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(15, sprintList, toolbar);
        root.setTop(header);
        root.setCenter(content);

        if (ProjectSession.hasProjectSelected()) {
            loadSprints(sprintList);
        }

        return root;
    }

    // ---------------------------
    // Load Sprints
    // ---------------------------
    private void loadSprints(ListView<JSONObject> list) {
        long projectId = ProjectSession.getProjectId();

        if (projectId <= 0) {
            list.getItems().clear();
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + projectId))
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            JSONArray arr = new JSONArray(resp.body());

            list.getItems().clear();

            for (int i = 0; i < arr.length(); i++) {
                list.getItems().add(arr.getJSONObject(i));
            }

        } catch (Exception ex) {
            list.getItems().clear();
        }
    }

    // ---------------------------
    // Custom Cell Factory (UI per sprint)
    // ---------------------------
private ListCell<JSONObject> sprintCellFactory(ListView<JSONObject> parentList) {
    return new ListCell<>() {
        @Override
        protected void updateItem(JSONObject obj, boolean empty) {
            super.updateItem(obj, empty);

            if (empty || obj == null) {
                setGraphic(null);
                return;
            }

            long id = obj.getLong("id");
            String name = obj.getString("name");
            String status = obj.optString("status", "UNKNOWN");

            Label title = new Label(name + " (" + status + ")");
            title.setFont(new Font(16));

            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");

            editBtn.setOnAction(e -> openEditDialog(obj, parentList));
            deleteBtn.setOnAction(e -> deleteSprint(id, parentList));

            HBox actions = new HBox(10, editBtn, deleteBtn);
            actions.setAlignment(Pos.CENTER_RIGHT);

            BorderPane row = new BorderPane();
            row.setLeft(title);
            row.setRight(actions);
            row.setPadding(new Insets(8));

            // Attach visual UI to cell
            setGraphic(row);

            // Make entire row clickable
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 1) {
                    SprintNavigator.goToSprintBoard(id);
                }
            });
        }
    };
}

           

    // ---------------------------
    // Create Sprint Dialog
    // ---------------------------
    private void openCreateDialog(ListView<JSONObject> list) {
        openSprintDialog("Create Sprint", null, list);
    }

    // ---------------------------
    // Edit Sprint Dialog
    // ---------------------------
    private void openEditDialog(JSONObject sprint, ListView<JSONObject> list) {
        openSprintDialog("Edit Sprint", sprint, list);
    }

    // ---------------------------
    // Shared Create/Edit Dialog
    // ---------------------------
    private void openSprintDialog(String title, JSONObject sprint, ListView<JSONObject> list) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);

        Label lblName = new Label("Sprint Name:");
        TextField nameField = new TextField(sprint != null ? sprint.getString("name") : "");

        Label lblStart = new Label("Start Date:");
        TextField startField = new TextField(
                sprint != null ? sprint.getString("startDate") : LocalDate.now().toString()
        );

        Label lblEnd = new Label("End Date:");
        TextField endField = new TextField(
                sprint != null ? sprint.getString("endDate") : LocalDate.now().plusDays(14).toString()
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        grid.add(lblName, 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(lblStart, 0, 1);
        grid.add(startField, 1, 1);

        grid.add(lblEnd, 0, 2);
        grid.add(endField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType okType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                if (sprint == null) {
                    createSprint(nameField.getText(), startField.getText(), endField.getText());
                } else {
                    updateSprint(sprint.getLong("id"), nameField.getText(), startField.getText(), endField.getText());
                }
                loadSprints(list);
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ---------------------------
    // Create Sprint (POST)
    // ---------------------------
    private void createSprint(String name, String startDate, String endDate) {
        try {
            long projectId = ProjectSession.getProjectId();
            JSONObject obj = new JSONObject();

            obj.put("name", name);
            obj.put("startDate", startDate);
            obj.put("endDate", endDate);
            obj.put("status", "FUTURE");

            JSONObject proj = new JSONObject();
            proj.put("id", projectId);
            obj.put("project", proj);

            HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

        } catch (Exception ignored) {}
    }

    // ---------------------------
    // Update Sprint (PUT)
    // ---------------------------
    private void updateSprint(long id, String name, String startDate, String endDate) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            obj.put("startDate", startDate);
            obj.put("endDate", endDate);

            HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/" + id))
                            .header("Content-Type", "application/json")
                            .PUT(HttpRequest.BodyPublishers.ofString(obj.toString()))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (Exception ignored) {}
    }

    // ---------------------------
    // Delete Sprint
    // ---------------------------
    private void deleteSprint(long id, ListView<JSONObject> list) {
        try {
            HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/" + id))
                            .DELETE()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            loadSprints(list);

        } catch (Exception ignored) {}
    }
}
