package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.session.ProjectSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class ReleasePlanView {

    private VBox releaseListBox;
    private VBox detailsBox;

    public Pane getView() {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Release Plans");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox top = new VBox(title);
        top.setPadding(new Insets(0, 0, 20, 0));
        root.setTop(top);

        // LEFT: RELEASE LIST
        releaseListBox = new VBox(12);
        releaseListBox.setPadding(new Insets(5));

        ScrollPane sp1 = new ScrollPane(releaseListBox);
        sp1.setFitToWidth(true);
        sp1.setStyle("-fx-background-color: transparent;");
        root.setLeft(sp1);

        // RIGHT: DETAILS
        detailsBox = new VBox(15);
        detailsBox.setPadding(new Insets(10));

        ScrollPane sp2 = new ScrollPane(detailsBox);
        sp2.setFitToWidth(true);
        sp2.setStyle("-fx-background-color: transparent;");
        root.setCenter(sp2);

        // CREATE RELEASE BUTTON
        Button createBtn = new Button("Create Release Plan");
        createBtn.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-padding: 10 16; -fx-background-radius: 6;"
        );
        createBtn.setOnAction(e -> openCreateDialog());

        HBox bottom = new HBox(createBtn);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(20, 0, 0, 0));
        root.setBottom(bottom);

        loadReleases();

        return root;
    }

    // ========================================================
    // LOAD RELEASES
    // ========================================================
    private void loadReleases() {
        releaseListBox.getChildren().clear();

        Long projectId = ProjectSession.getProjectId();
        if (projectId == null || projectId <= 0) {
            releaseListBox.getChildren().add(new Label("Select a project."));
            return;
        }

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/releases/" + projectId))
                        .GET().build();

                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

                JSONArray arr = new JSONArray(resp.body());

                Platform.runLater(() -> {
                    releaseListBox.getChildren().clear();

                    if (arr.length() == 0) {
                        releaseListBox.getChildren().add(new Label("No releases found"));
                        return;
                    }

                    for (int i = 0; i < arr.length(); i++) {
                        releaseListBox.getChildren().add(createReleaseCard(arr.getJSONObject(i)));
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> releaseListBox.getChildren().add(new Label("Error loading releases")));
            }
        }).start();
    }

    // ========================================================
    // RELEASE LIST CARD
    // ========================================================
    private HBox createReleaseCard(JSONObject r) {

        HBox card = new HBox(10);
        card.setPadding(new Insets(12));
        card.setAlignment(Pos.CENTER_LEFT);

        card.setStyle(
                "-fx-background-color: white; -fx-border-color: #DDD;" +
                        "-fx-border-radius: 6; -fx-background-radius: 6;"
        );

        Label lbl = new Label(r.getString("name"));
        lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        card.getChildren().add(lbl);

        card.setOnMouseClicked(e -> showDetails(r));

        return card;
    }

    // ========================================================
    // SHOW DETAILS
    // ========================================================
    private void showDetails(JSONObject r) {

        detailsBox.getChildren().clear();

        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white; -fx-border-color: #DDD;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        Label head = new Label("Release Details");
        head.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label name = new Label("Name: " + r.getString("name"));
        Label desc = new Label("Description: " + r.optString("description", ""));
        desc.setWrapText(true);

        Label dates = new Label(
                "Start: " + r.optString("startDate", "") +
                        "    |    End: " + r.optString("endDate", "")
        );

        // STORIES
        Label sh = new Label("Stories");
        sh.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        VBox storyList = new VBox(8);

        if (r.has("stories")) {
            JSONArray arr = r.getJSONArray("stories");
            if (arr.length() == 0) {
                storyList.getChildren().add(new Label("No stories assigned."));
            } else {
                for (int i = 0; i < arr.length(); i++) {
                    storyList.getChildren().add(
                            createStoryRow(r.getLong("id"), arr.getJSONObject(i))
                    );
                }
            }
        }

        Button assign = new Button("Assign Stories");
        assign.setStyle("-fx-background-color:#1976D2;-fx-text-fill:white;-fx-padding:6 12;-fx-background-radius:6;");
        assign.setOnAction(e -> openAssignDialog(r));

        Button delete = new Button("Delete Release");
        delete.setStyle("-fx-background-color:#D32F2F;-fx-text-fill:white;-fx-padding:6 12;-fx-background-radius:6;");
        delete.setOnAction(e -> deleteRelease(r.getLong("id")));

        card.getChildren().addAll(head, name, dates, desc, sh, storyList, assign, delete);
        detailsBox.getChildren().add(card);
    }

    // ========================================================
    // STORY ROW + REMOVE
    // ========================================================
    private HBox createStoryRow(Long releaseId, JSONObject s) {

        HBox box = new HBox(10);
        box.setPadding(new Insets(8));
        box.setAlignment(Pos.CENTER_LEFT);

        box.setStyle("-fx-background-color:#F7F7F7;-fx-border-color:#DDD;" +
                "-fx-background-radius:6;-fx-border-radius:6;");

        Label lbl = new Label(s.getString("title"));
        lbl.setStyle("-fx-font-weight: bold;");

        Button remove = new Button("Remove");
        remove.setStyle("-fx-background-color:#E53935;-fx-text-fill:white;-fx-background-radius:6;");
        remove.setOnAction(e -> removeStory(releaseId, s.getLong("id")));

        box.getChildren().addAll(lbl, remove);
        return box;
    }

    // ========================================================
    // CREATE RELEASE DIALOG
    // ========================================================
    private void openCreateDialog() {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Create Release Plan");

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TextField n = new TextField();
        n.setPromptText("Name");

        DatePicker s = new DatePicker();
        s.setPromptText("Start Date");

        DatePicker e = new DatePicker();
        e.setPromptText("End Date");

        TextArea d = new TextArea();
        d.setPromptText("Description");
        d.setPrefRowCount(3);

        Button create = new Button("Create");
        create.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white;-fx-padding:6 12;-fx-background-radius:6;");
        create.setOnAction(ev -> {
            createRelease(
                    n.getText(),
                    s.getValue() == null ? "" : s.getValue().toString(),
                    e.getValue() == null ? "" : e.getValue().toString(),
                    d.getText()
            );
            dialog.close();
        });

        box.getChildren().addAll(n, s, e, d, create);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    // ========================================================
    // CREATE RELEASE API
    // ========================================================
private void createRelease(String name, String start, String end, String desc) {

    Long projectId = ProjectSession.getProjectId();
    if (projectId == null) return;

    JSONObject body = new JSONObject();
    body.put("name", name);

    // Must send null instead of empty string for LocalDate
    body.put("startDate", (start == null || start.isBlank()) ? JSONObject.NULL : start);
    body.put("endDate", (end == null || end.isBlank()) ? JSONObject.NULL : end);

    body.put("description", desc);

    new Thread(() -> {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/releases/" + projectId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> resp =
                    client.send(req, HttpResponse.BodyHandlers.ofString());

            System.out.println("Create Release Response: " + resp.body());

            Platform.runLater(this::loadReleases);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }).start();
}


    // ========================================================
    // ASSIGN STORIES DIALOG
    // ========================================================
    private void openAssignDialog(JSONObject r) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Assign Stories");

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        ListView<JSONObject> list = new ListView<>();
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        loadProjectStories(list);

        Button assign = new Button("Assign Selected");
        assign.setStyle("-fx-background-color:#1976D2;-fx-text-fill:white;-fx-background-radius:6;");
        assign.setOnAction(e -> {
            for (JSONObject s : list.getSelectionModel().getSelectedItems()) {
                assignStory(r.getLong("id"), s.getLong("id"));
            }
            dialog.close();
        });

        box.getChildren().addAll(new Label("Select stories:"), list, assign);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    // ========================================================
    // LOAD STORIES OF THIS PROJECT
    // ========================================================
    private void loadProjectStories(ListView<JSONObject> list) {

        Long pid = ProjectSession.getProjectId();
        if (pid == null) return;

        new Thread(() -> {
            try {
                HttpClient c = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/userstories/project/" + pid))
                        .GET().build();

                HttpResponse<String> resp = c.send(req, HttpResponse.BodyHandlers.ofString());
                JSONArray arr = new JSONArray(resp.body());

                Platform.runLater(() -> {
                    for (int i = 0; i < arr.length(); i++)
                        list.getItems().add(arr.getJSONObject(i));

                    list.setCellFactory(v -> new ListCell<>() {
                        @Override
                        protected void updateItem(JSONObject item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) setText(null);
                            else setText(item.getString("title"));
                        }
                    });
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // ========================================================
    // ASSIGN STORY API
    // ========================================================
    private void assignStory(Long releaseId, Long storyId) {

        new Thread(() -> {
            try {
                HttpClient c = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/releases/" + releaseId + "/assign/" + storyId))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                c.send(req, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(this::loadReleases);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // ========================================================
    // REMOVE STORY API
    // ========================================================
    private void removeStory(Long releaseId, Long storyId) {

        new Thread(() -> {
            try {
                HttpClient c = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/releases/" + releaseId + "/remove/" + storyId))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                c.send(req, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(this::loadReleases);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // ========================================================
    // DELETE RELEASE API
    // ========================================================
    private void deleteRelease(Long releaseId) {

        new Thread(() -> {
            try {
                HttpClient c = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/releases/" + releaseId))
                        .DELETE()
                        .build();

                c.send(req, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
                    detailsBox.getChildren().clear();
                    loadReleases();
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
