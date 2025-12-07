package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.session.ProjectSession;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;

public class ProjectsView {

    private static final String API_URL = "http://localhost:8080/api/projects";

    private final HttpClient client = HttpClient.newHttpClient();
    private VBox projectListBox;

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Projects");
        title.setFont(new Font(24));

        Button addBtn = new Button("Add Project");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerRow = new HBox(10, title, spacer, addBtn);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(0, 0, 20, 0));

        projectListBox = new VBox(10);
        projectListBox.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(projectListBox);
        scrollPane.setFitToWidth(true);

        addBtn.setOnAction(e -> openCreateDialog());

        root.setTop(headerRow);
        root.setCenter(scrollPane);

        loadProjects();
        return root;
    }

    private void loadProjects() {
        new Thread(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .GET()
                        .build();

                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200) {
                    System.err.println("Failed to load projects: " + resp.statusCode());
                    return;
                }

                JSONArray arr = new JSONArray(resp.body());
                List<Pane> cards = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    long id = obj.getLong("id");
                    String name = obj.optString("name", "(no name)");
                    String desc = obj.isNull("description") ? "" : obj.getString("description");
                    cards.add(buildProjectCard(id, name, desc));
                }

                Platform.runLater(() -> projectListBox.getChildren().setAll(cards));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private Pane buildProjectCard(long id, String name, String desc) {
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font(16));

        Label descLabel = new Label(desc == null ? "" : desc);
        descLabel.setWrapText(true);

        VBox textBox = new VBox(4, nameLabel, descLabel);

        Button openBtn = new Button("Open");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");

        HBox buttonRow = new HBox(8, openBtn, editBtn, deleteBtn);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(8, textBox, buttonRow);
        content.setPadding(new Insets(12));

        BorderPane card = new BorderPane();
        card.setCenter(content);
        card.setStyle("-fx-border-color: #e5e7eb; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: white;");

        openBtn.setOnAction(e -> Workspace.setProjectAndGoToDashboard(id));
        editBtn.setOnAction(e -> openEditDialog(id, name, desc));
        deleteBtn.setOnAction(e -> deleteProject(id, name));

        return card;
    }

    private void openCreateDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Project");

        Label nameLbl = new Label("Name:");
        TextField nameField = new TextField();

        Label descLbl = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nameLbl, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(descLbl, 0, 1);
        grid.add(descArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType createType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == createType) {
                String name = nameField.getText().trim();
                String desc = descArea.getText().trim();
                if (!name.isEmpty()) createProject(name, desc);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void openEditDialog(long id, String currentName, String currentDesc) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Project");

        Label nameLbl = new Label("Name:");
        TextField nameField = new TextField(currentName);

        Label descLbl = new Label("Description:");
        TextArea descArea = new TextArea(currentDesc == null ? "" : currentDesc);
        descArea.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nameLbl, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(descLbl, 0, 1);
        grid.add(descArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                updateProject(id, nameField.getText().trim(), descArea.getText().trim());
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void createProject(String name, String desc) {
        new Thread(() -> {
            try {
                JSONObject obj = new JSONObject();
                obj.put("name", name);
                obj.put("description", desc.isEmpty() ? JSONObject.NULL : desc);

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                        .build();

                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() >= 200) {
                    Platform.runLater(() -> {
                        loadProjects();
                        Workspace.refreshProjectsDropdown();
                    });
                }

            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }

    private void updateProject(long id, String name, String desc) {
        new Thread(() -> {
            try {
                JSONObject obj = new JSONObject();
                obj.put("name", name);
                obj.put("description", desc.isEmpty() ? JSONObject.NULL : desc);

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL + "/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(obj.toString()))
                        .build();

                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() >= 200) {
                    Platform.runLater(() -> {
                        loadProjects();
                        Workspace.refreshProjectsDropdown();
                    });
                }

            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }

    private void deleteProject(long id, String name) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Delete project \"" + name + "\"?");
        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        new Thread(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL + "/" + id))
                        .DELETE()
                        .build();

                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() >= 200) {
                    Platform.runLater(() -> {
                        loadProjects();
                        Workspace.refreshProjectsDropdown();
                    });
                }

            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }
}
