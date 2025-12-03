package com.example.agile_re_tool.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TeamSettingsView {

    private TableView<User> table;
    private int editingRowIndex = -1;

    public BorderPane getView() {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f9fc, #edf1f7);");

        Label title = new Label("Team Settings");
        title.setFont(new Font(26));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1f2933;");

        Label subtitle = new Label("Manage team members, roles, and access");
        subtitle.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");

        VBox top = new VBox(4, title, subtitle);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 16, 0));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #d1d5db;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-table-cell-border-color: #e5e7eb;" +
                        "-fx-font-size: 13px;"
        );

        TableColumn<User, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        idCol.setMinWidth(60);
        idCol.setMaxWidth(80);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(c -> c.getValue().usernameProperty());

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(c -> c.getValue().roleProperty());

        roleCol.setCellFactory(col -> new TableCell<User, String>() {

            private final ComboBox<String> dropdown = new ComboBox<>();
            private final Button saveBtn = new Button("Save");
            private final Button cancelBtn = new Button("Cancel");
            private final HBox editBox = new HBox(6, dropdown, saveBtn, cancelBtn);

            {
                dropdown.getItems().addAll(
                        "USER",
                        "PRODUCT_OWNER",
                        "SCRUM_MASTER",
                        "DEVELOPER",
                        "TESTER"
                );

                saveBtn.setStyle(
                        "-fx-background-color: #2563eb;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 4;" +
                                "-fx-padding: 3 10;"
                );

                cancelBtn.setStyle(
                        "-fx-background-color: #9ca3af;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 4;" +
                                "-fx-padding: 3 10;"
                );

                editBox.setAlignment(Pos.CENTER_LEFT);

                saveBtn.setOnAction(e -> {
                    User rowUser = getTableView().getItems().get(getIndex());
                    updateRole(rowUser.getId(), dropdown.getValue());
                    editingRowIndex = -1;
                    loadUsers();
                });

                cancelBtn.setOnAction(e -> {
                    editingRowIndex = -1;
                    table.refresh();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (getIndex() == editingRowIndex) {
                    User rowUser = getTableView().getItems().get(getIndex());
                    dropdown.setValue(rowUser.getRole());
                    setGraphic(editBox);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item);
                }
            }
        });

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setMinWidth(80);   
        actionsCol.setPrefWidth(90);  
        actionsCol.setMaxWidth(120);  


        actionsCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #2563eb;" +
                                "-fx-underline: true;" +
                                "-fx-cursor: hand;"
                );

                deleteBtn.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #dc2626;" +
                                "-fx-underline: true;" +
                                "-fx-cursor: hand;"
                );

                editBtn.setOnAction(e -> {
                    editingRowIndex = getIndex();
                    table.refresh();
                });

                deleteBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    deleteUser(u.getId());
                    editingRowIndex = -1;
                    loadUsers();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(12, editBtn, deleteBtn);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(idCol, usernameCol, roleCol, actionsCol);

        VBox card = new VBox(10, table);
        card.setPadding(new Insets(12));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #d1d5db;"
        );

        root.setTop(top);
        root.setCenter(card);

        loadUsers();
        return root;
    }

    private void loadUsers() {
        try {
            String json = sendGet("http://localhost:8080/api/users");
            User[] users = User.fromJsonArray(json);
            table.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRole(Long userId, String role) {
        try {
            sendPut("http://localhost:8080/api/users/" + userId + "/role", "\"" + role + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(Long userId) {
        try {
            sendDelete("http://localhost:8080/api/users/" + userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendPut(String urlStr, String body) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        con.getInputStream().close();
    }

    private void sendDelete(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.getInputStream().close();
    }

    public static class User {
        private Long id;
        private String username;
        private String role;

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }

        public javafx.beans.property.LongProperty idProperty() {
            return new javafx.beans.property.SimpleLongProperty(id);
        }

        public javafx.beans.property.StringProperty usernameProperty() {
            return new javafx.beans.property.SimpleStringProperty(username);
        }

        public javafx.beans.property.StringProperty roleProperty() {
            return new javafx.beans.property.SimpleStringProperty(role);
        }

        public static User[] fromJsonArray(String json) {
            com.google.gson.Gson g = new com.google.gson.Gson();
            return g.fromJson(json, User[].class);
        }
    }
}
