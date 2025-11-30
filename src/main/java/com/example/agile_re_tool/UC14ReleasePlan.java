package com.example.agile_re_tool;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class UC14ReleasePlan extends Application {

    public UC14ReleasePlan() {
    }

    public void openWindow() {
        Stage stage = new Stage();
        try {
            start(stage);
        } catch (Exception e) {
            System.err.println("Failed to open UC14 window: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Release Plan");

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("release-root");

        // Header with title and New Release button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 12, 0));
        Label title = new Label("Release Plan");
        title.setStyle("-fx-font-size:20px; -fx-font-weight:700;");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Button newReleaseBtn = new Button("+ New Release");
        newReleaseBtn.getStyleClass().add("primary-button");
        // releasesList will be created below; capture via lambda later

        header.getChildren().addAll(title, headerSpacer, newReleaseBtn);

        // Top summary
        HBox summary = new HBox(12);
        summary.setAlignment(Pos.CENTER_LEFT);

        summary.getChildren().addAll(
            buildSummaryCard("Total Releases", "3"),
            buildSummaryCard("In Progress", "1"),
            buildSummaryCard("Completed", "1"),
            buildSummaryCard("Total Sprints", "6")
        );

        // Releases list (populated from backend)

        VBox releasesList = new VBox(12);

        Label loading = new Label("Loading releases...");
        releasesList.getChildren().add(loading);

        ScrollPane sc = new ScrollPane(releasesList);
        sc.setFitToWidth(true);
        sc.setStyle("-fx-background-color:transparent;");

        root.getChildren().addAll(header, summary, sc);

        // wire header new release button to open the create dialog and refresh the list
        newReleaseBtn.setOnAction(e -> {
            // open the form and refresh the releases list when saved
            showReleaseForm(null, "", "", "", "", 0.0, "", "", releasesList);
        });

        Scene scene = new Scene(root, 1100, 700);
        try {
            String css = getClass().getResource("/uc14-style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        // Fetch releases from backend and populate list
        fetchAndPopulate(releasesList);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox buildSummaryCard(String title, String value) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12));
        card.setPrefWidth(220);
        card.getStyleClass().add("uc-summary");

        Label t = new Label(title);
        t.getStyleClass().add("uc-summary-title");

        Label v = new Label(value);
        v.setFont(Font.font(20));
        v.getStyleClass().add("uc-summary-value");

        card.getChildren().addAll(t, v);
        return card;
    }

    private void fetchAndPopulate(VBox releasesList) {
        new Thread(() -> {
            try {
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create("http://localhost:8080/api/releases"))
                        .GET()
                        .build();

                java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    JSONArray arr = new JSONArray(resp.body());
                    javafx.application.Platform.runLater(() -> {
                        releasesList.getChildren().clear();
                        if (arr.length() == 0) {
                            releasesList.getChildren().add(new Label("No releases found."));
                        } else {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject o = arr.getJSONObject(i);
                                Long id = o.has("id") ? o.optLong("id") : null;
                                String name = o.optString("name", "Untitled");
                                String version = o.optString("version", "");
                                String desc = o.optString("description", "");
                                String start = o.optString("startDate", "");
                                String end = o.optString("endDate", "");
                                String dates = (start.isBlank() ? "" : start) + (end.isBlank() ? "" : " - " + end);
                                double progress = o.has("progress") ? o.optDouble("progress", 0.0) : 0.0;
                                String sprintsCsv = o.optString("sprints", "");
                                String teamCsv = o.optString("team", "");
                                List<String> sprints = sprintsCsv.isBlank() ? List.of() : List.of(sprintsCsv.split(","));
                                List<String> team = teamCsv.isBlank() ? List.of() : List.of(teamCsv.split(","));
                                releasesList.getChildren().add(createReleaseCard(id, name, version, "", desc, progress, dates, sprints, team));
                            }
                        }
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        releasesList.getChildren().clear();
                        releasesList.getChildren().add(new Label("Failed to load releases (status " + resp.statusCode() + ")"));
                    });
                }
            } catch (Exception ex) {
                System.err.println("Error fetching releases: " + ex.getMessage());
                javafx.application.Platform.runLater(() -> {
                    releasesList.getChildren().clear();
                    releasesList.getChildren().add(new Label("Error fetching releases: " + ex.getMessage()));
                });
            }
        }).start();
    }

    // Show modal form for creating or editing a release (enhanced UI)
    private void showReleaseForm(Long id, String name, String version, String description,
                                 String dates, double progress, String sprintsCsv, String teamCsv, VBox parentList) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle(id == null ? "Create New Release" : "Edit Release");

        VBox modalRoot = new VBox(12);
        modalRoot.getStyleClass().add("modal-root");
        modalRoot.setPadding(new Insets(14));

        // Title row with close X
        HBox titleRow = new HBox();
        Label title = new Label(id == null ? "Create New Release" : "Edit Release");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:700;");
        Region tSpacer = new Region();
        HBox.setHgrow(tSpacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("modal-close");
        closeBtn.setOnAction(e -> modal.close());
        titleRow.getChildren().addAll(title, tSpacer, closeBtn);

        HBox content = new HBox(16);

        // Left: form fields (grid-like)
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(8));
        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(35);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(65);
        form.getColumnConstraints().addAll(c0, c1);

        Label nameLbl = new Label("Release Name *");
        TextField nameField = new TextField(name == null ? "" : name);
        nameField.setPromptText("e.g., Beta Launch");

        Label verLbl = new Label("Version *");
        TextField versionField = new TextField(version == null ? "" : version);
        versionField.setPromptText("e.g., v1.0.0");

        Label goalLbl = new Label("Release Goal");
        TextArea goalArea = new TextArea(description == null ? "" : description);
        goalArea.setPrefRowCount(4);
        goalArea.setPromptText("Describe the main objectives and features for this release");

        Label startLbl = new Label("Start Date *");
        DatePicker startPicker = new DatePicker();
        Label endLbl = new Label("End Date *");
        DatePicker endPicker = new DatePicker();

        // configure date format dd-MM-yyyy for display
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        StringConverter<java.time.LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(java.time.LocalDate date) {
                return date == null ? "" : date.format(fmt);
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                if (string == null || string.isBlank()) return null;
                try {
                    return java.time.LocalDate.parse(string, fmt);
                } catch (DateTimeParseException ex) {
                    try { return java.time.LocalDate.parse(string); } catch (Exception ex2) { return null; }
                }
            }
        };
        startPicker.setConverter(converter);
        endPicker.setConverter(converter);
        startPicker.setPromptText("dd-mm-yyyy");
        endPicker.setPromptText("dd-mm-yyyy");

        // try to parse incoming dates (support ISO or dd-MM-yyyy)
        if (dates != null && dates.contains(" - ")) {
            try {
                String[] p = dates.split(" - ", 2);
                try { startPicker.setValue(java.time.LocalDate.parse(p[0])); } catch (Exception ex) { try { startPicker.setValue(java.time.LocalDate.parse(p[0], fmt)); } catch (Exception ignored) {} }
                try { endPicker.setValue(java.time.LocalDate.parse(p[1])); } catch (Exception ex) { try { endPicker.setValue(java.time.LocalDate.parse(p[1], fmt)); } catch (Exception ignored) {} }
            } catch (Exception ignored) {}
        }

        Label statusLbl = new Label("Status");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Planning", "In Progress", "Completed");
        statusCombo.setValue("Planning");

        form.add(nameLbl, 0, 0);
        form.add(nameField, 1, 0);
        form.add(verLbl, 0, 1);
        form.add(versionField, 1, 1);
        form.add(goalLbl, 0, 2);
        form.add(goalArea, 1, 2);
        form.add(startLbl, 0, 3);
        form.add(startPicker, 1, 3);
        form.add(endLbl, 0, 4);
        form.add(endPicker, 1, 4);
        form.add(statusLbl, 0, 5);
        form.add(statusCombo, 1, 5);

        // Right: Sprints + Team lists
        VBox right = new VBox(12);
        right.setPadding(new Insets(8));
        right.setPrefWidth(420);

        Label sprintsTitle = new Label("Sprints");
        sprintsTitle.setStyle("-fx-font-weight:600;");
        VBox sprintsBox = new VBox(6);
        sprintsBox.setPadding(new Insets(6));
        sprintsBox.getStyleClass().add("list-sheet");

        // populate sprints from CSV if provided
        if (sprintsCsv != null && !sprintsCsv.isBlank()) {
            String[] sp = sprintsCsv.split(",");
            for (String s : sp) {
                HBox item = new HBox(8);
                item.setAlignment(Pos.CENTER_LEFT);
                CheckBox cb = new CheckBox();
                Label lbl = new Label(s.trim());
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                Label pill = new Label("planned");
                pill.getStyleClass().add("status-pill");
                item.getChildren().addAll(cb, lbl, spacer2, pill);
                sprintsBox.getChildren().add(item);
            }
        } else {
            // placeholder sample if none
            String[] sampleSprints = new String[]{"Sprint 1 (2025-01-06 - 2025-01-19)|completed", "Sprint 2 (2025-01-20 - 2025-02-02)|completed", "Sprint 3 (2025-02-03 - 2025-02-16)|active", "Sprint 4 (2025-02-17 - 2025-03-02)|planned", "Sprint 5 (2025-03-03 - 2025-03-16)|planned"};
            for (String s : sampleSprints) {
                String[] parts = s.split("\\|",2);
                String text = parts[0];
                String st = parts.length>1?parts[1]:"planned";
                HBox item = new HBox(8);
                item.setAlignment(Pos.CENTER_LEFT);
                CheckBox cb = new CheckBox();
                Label lbl = new Label(text);
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                Label pill = new Label(st);
                pill.getStyleClass().add("status-pill");
                item.getChildren().addAll(cb, lbl, spacer2, pill);
                sprintsBox.getChildren().add(item);
            }
        }

        ScrollPane sprintsScroll = new ScrollPane(sprintsBox);
        sprintsScroll.setFitToWidth(true);
        sprintsScroll.setPrefHeight(180);

        Label teamTitle = new Label("Team Members");
        teamTitle.setStyle("-fx-font-weight:600;");
        VBox teamBox = new VBox(6);
        teamBox.setPadding(new Insets(6));
        teamBox.getStyleClass().add("list-sheet");

        if (teamCsv != null && !teamCsv.isBlank()) {
            String[] tm = teamCsv.split(",");
            for (String t : tm) {
                HBox item = new HBox(8);
                item.setAlignment(Pos.CENTER_LEFT);
                CheckBox cb = new CheckBox();
                String trimmed = t.trim();
                String initials = "";
                String nameRole = trimmed;
                if (trimmed.length() > 0) initials = trimmed.substring(0, Math.min(2, trimmed.length()));
                Label avatar = new Label(initials);
                avatar.getStyleClass().add("avatar-circle");
                Label info = new Label(nameRole);
                item.getChildren().addAll(cb, avatar, info);
                teamBox.getChildren().add(item);
            }
        } else {
            String[][] sample = new String[][]{{"SJ","Sarah Johnson – Product Owner"},{"MC","Michael Chen – Developer"},{"EW","Emma Williams – Designer"},{"JB","James Brown – Developer"},{"LD","Lisa Davis – Tester"}};
            for (String[] t : sample) {
                HBox item = new HBox(8);
                item.setAlignment(Pos.CENTER_LEFT);
                CheckBox cb = new CheckBox();
                Label avatar = new Label(t[0]);
                avatar.getStyleClass().add("avatar-circle");
                Label info = new Label(t[1]);
                item.getChildren().addAll(cb, avatar, info);
                teamBox.getChildren().add(item);
            }
        }

        ScrollPane teamScroll = new ScrollPane(teamBox);
        teamScroll.setFitToWidth(true);
        teamScroll.setPrefHeight(220);

        right.getChildren().addAll(sprintsTitle, sprintsScroll, teamTitle, teamScroll);

        content.getChildren().addAll(form, right);

        // Bottom actions
        Button createBtn = new Button(id == null ? "Create Release" : "Save Changes");
        createBtn.getStyleClass().add("primary-button");
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        HBox actions = new HBox(10, cancelBtn, createBtn);
        actions.setPadding(new Insets(8));
        actions.setAlignment(Pos.CENTER_RIGHT);

        modalRoot.getChildren().addAll(titleRow, content, actions);

        Scene scene = new Scene(modalRoot, 980, 620);
        try {
            String css = getClass().getResource("/uc14-style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        cancelBtn.setOnAction(e -> modal.close());

        createBtn.setOnAction(e -> {
            String n = nameField.getText().trim();
            if (n.isEmpty()) {
                nameField.requestFocus();
                return;
            }
            String ver = versionField.getText().trim();
            String desc = goalArea.getText().trim();
            String start = startPicker.getValue() == null ? "" : startPicker.getValue().toString();
            String end = endPicker.getValue() == null ? "" : endPicker.getValue().toString();
            double pr = progress;

            StringBuilder sprintsBuilder = new StringBuilder();
            for (javafx.scene.Node nnode : sprintsBox.getChildren()) {
                if (nnode instanceof HBox) {
                    HBox hb = (HBox) nnode;
                    for (javafx.scene.Node child : hb.getChildren()) {
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            if (cb.isSelected()) {
                                // find label
                                for (javafx.scene.Node c2 : hb.getChildren()) {
                                    if (c2 instanceof Label && !((Label) c2).getStyleClass().contains("status-pill")) {
                                        if (sprintsBuilder.length() > 0) sprintsBuilder.append(",");
                                        sprintsBuilder.append(((Label) c2).getText());
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            String sprints = sprintsBuilder.toString();

            StringBuilder teamBuilder = new StringBuilder();
            for (javafx.scene.Node nnode : teamBox.getChildren()) {
                if (nnode instanceof HBox) {
                    HBox hb = (HBox) nnode;
                    for (javafx.scene.Node child : hb.getChildren()) {
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            if (cb.isSelected()) {
                                // name is likely at index 2
                                for (javafx.scene.Node c2 : hb.getChildren()) {
                                    if (c2 instanceof Label && ((Label) c2).getStyleClass().contains("avatar-circle") == false) {
                                        if (teamBuilder.length() > 0) teamBuilder.append(",");
                                        teamBuilder.append(((Label) c2).getText());
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            String team = teamBuilder.toString();

            org.json.JSONObject obj = new org.json.JSONObject();
            obj.put("name", n);
            obj.put("version", ver);
            obj.put("description", desc);
            obj.put("startDate", start);
            obj.put("endDate", end);
            obj.put("progress", pr);
            obj.put("sprints", sprints);
            obj.put("team", team);

            new Thread(() -> {
                try {
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                    java.net.http.HttpRequest req;
                    if (id == null) {
                        req = java.net.http.HttpRequest.newBuilder()
                                .uri(java.net.URI.create("http://localhost:8080/api/releases"))
                                .header("Content-Type", "application/json")
                                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(obj.toString()))
                                .build();
                    } else {
                        req = java.net.http.HttpRequest.newBuilder()
                                .uri(java.net.URI.create("http://localhost:8080/api/releases/" + id))
                                .header("Content-Type", "application/json")
                                .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(obj.toString()))
                                .build();
                    }
                    java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        javafx.application.Platform.runLater(() -> {
                            modal.close();
                            if (parentList != null) fetchAndPopulate(parentList);
                        });
                    } else {
                        System.err.println("Save failed: " + resp.statusCode());
                    }
                } catch (Exception ex) {
                    System.err.println("Save error: " + ex.getMessage());
                }
            }).start();
        });

        modal.setScene(scene);
        modal.showAndWait();
    }

    private VBox createReleaseCard(Long id, String name, String version, String state, String desc,
                                   double progress, String dates,
                                   List<String> sprints, List<String> team) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-radius: 10;");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size:16px; -fx-font-weight:700; -fx-text-fill:#111827;");

        Label ver = new Label(version);
        ver.setStyle("-fx-background-color:#eef2ff; -fx-text-fill:#4338ca; -fx-padding:3 8; -fx-background-radius:12; -fx-font-size:11;");

        Label status = new Label(state);
        status.getStyleClass().add("uc-badge");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit");
        Button delBtn = new Button("Delete");
        HBox actions = new HBox(8, editBtn, delBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(nameLbl, ver, status, spacer, actions, new Label(dates));

        Label descLbl = new Label(desc);
        descLbl.setWrapText(true);
        descLbl.setStyle("-fx-text-fill:#6b7280;");

        ProgressBar pb = new ProgressBar(progress);
        pb.setPrefWidth(800);

        HBox progressRow = new HBox(8, pb, new Label(String.format("%d%%", Math.round(progress * 100))));
        progressRow.setAlignment(Pos.CENTER_LEFT);

        FlowPane sprintPane = new FlowPane(8, 8);
        sprintPane.getChildren().add(new Label("Sprints: "));
        for (String s : sprints) {
            Label chip = new Label(s);
            chip.getStyleClass().add("chip");
            sprintPane.getChildren().add(chip);
        }

        FlowPane teamPane = new FlowPane(8, 8);
        teamPane.getChildren().add(new Label("Team: "));
        for (String m : team) {
            Label avatar = new Label(m);
            avatar.getStyleClass().add("avatar");
            teamPane.getChildren().add(avatar);
        }

        card.getChildren().addAll(header, descLbl, progressRow, sprintPane, teamPane);

        editBtn.setOnAction(e -> showReleaseForm(id, name, version, desc, dates, progress, String.join(",", sprints), String.join(",", team), (VBox) card.getParent()));

        delBtn.setOnAction(e -> {
            if (id == null) return;
            new Thread(() -> {
                try {
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                    java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8080/api/releases/" + id))
                            .DELETE()
                            .build();
                    java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
                    if (resp.statusCode() == 204 || (resp.statusCode() >= 200 && resp.statusCode() < 300)) {
                        javafx.application.Platform.runLater(() -> fetchAndPopulate((VBox) card.getParent()));
                    } else {
                        System.err.println("Delete failed: " + resp.statusCode());
                    }
                } catch (Exception ex) {
                    System.err.println("Delete error: " + ex.getMessage());
                }
            }).start();
        });

        return card;
    }

    // Public helper to open a standalone create dialog (used from Dashboard)
    public void openCreateDialogStandalone() {
        // call showReleaseForm with empty values and no parent list
        showReleaseForm(null, "", "", "", "", 0.0, "", "", null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
