package com.example.agile_re_tool;

import com.example.agile_re_tool.session.ProjectSession;
import com.example.agile_re_tool.session.UserSession;
import com.example.agile_re_tool.util.SprintFetcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class UC04EditUserStory extends Application {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Runnable onStoryChanged;

    private List<String> assignees = List.of();
    private List<String> estimateTypes = List.of("Story Points", "T-shirt Sizes", "Time");
    private List<String> tshirtSizes = List.of("XS", "S", "M", "L", "XL");
    private int defaultStoryPoints = 3;
    private String defaultPriority = "Medium";
    private String defaultStatus = "To Do";

    private static final List<Integer> FIBONACCI_POINTS = List.of(1, 2, 3, 5, 8, 13, 21);

    private long editingId;
    private Long existingSprintId = null;

    public UC04EditUserStory() { this(0L, null); }
    public UC04EditUserStory(long storyId) { this(storyId, null); }

    public UC04EditUserStory(long storyId, Runnable onStoryChanged) {
        this.editingId = storyId;
        this.onStoryChanged = onStoryChanged;
    }

    public void openWindow() {
        Stage stage = new Stage();
        try { start(stage); } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void start(Stage primaryStage) {

        CreateStoryDto existing = loadExistingFromBackend(editingId)
                .orElseGet(() -> {
                    CreateStoryDto d = new CreateStoryDto();
                    d.title = "";
                    d.description = "";
                    d.acceptanceCriteria = "";
                    d.assignee = null;
                    d.estimateType = "Story Points";
                    d.storyPoints = defaultStoryPoints;
                    d.priority = defaultPriority;
                    d.status = defaultStatus;
                    d.mvp = false;
                    d.sprintReady = false;
                    return d;
                });

        primaryStage.setTitle(editingId > 0 ? "Edit User Story #" + editingId : "Edit User Story");

        Label heading = new Label("Edit Story");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: 700;");

        TextField titleField = new TextField(existing.title);

        TextArea descriptionArea = new TextArea(existing.description);
        descriptionArea.setPrefRowCount(3);

        TextArea acceptanceArea = new TextArea(existing.acceptanceCriteria);
        acceptanceArea.setPrefRowCount(2);

        ComboBox<String> assigneeCombo = new ComboBox<>();
        assigneeCombo.getItems().setAll(assignees);
        if (existing.assignee != null) assigneeCombo.getSelectionModel().select(existing.assignee);

        GridPane leftGrid = new GridPane();
        leftGrid.setHgap(12);
        leftGrid.setVgap(12);
        leftGrid.setPadding(new Insets(8));

        int r = 0;
        leftGrid.add(new Label("Task Title *"), 0, r); leftGrid.add(titleField, 1, r++);
        leftGrid.add(new Label("Description"), 0, r); leftGrid.add(descriptionArea, 1, r++);
        leftGrid.add(new Label("Acceptance Criteria"), 0, r); leftGrid.add(acceptanceArea, 1, r++);
        leftGrid.add(new Label("Assignee"), 0, r); leftGrid.add(assigneeCombo, 1, r++);

        VBox leftColumn = new VBox(10, heading, leftGrid);

        ComboBox<String> estimateTypeCombo = new ComboBox<>();
        estimateTypeCombo.getItems().setAll(estimateTypes);
        estimateTypeCombo.getSelectionModel().select(existing.estimateType);

        ComboBox<Integer> pointsCombo = new ComboBox<>();
        pointsCombo.getItems().addAll(FIBONACCI_POINTS);
        if (existing.storyPoints != null) pointsCombo.getSelectionModel().select(existing.storyPoints);

        ComboBox<String> tshirtCombo = new ComboBox<>();
        tshirtCombo.getItems().setAll(tshirtSizes);

        TextField timeEstimateField = new TextField();

        if (existing.estimateType.equals("T-shirt Sizes") && existing.size != null)
            tshirtCombo.getSelectionModel().select(existing.size);

        if (existing.estimateType.equals("Time") && existing.timeEstimate != null)
            timeEstimateField.setText(existing.timeEstimate);

        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.getSelectionModel().select(existing.priority);

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Backlog", "To Do", "In Progress", "Testing", "Done");
        statusCombo.getSelectionModel().select(existing.status);

        VBox compactEstimateBox = new VBox(8);

        HBox storyPointsRow = new HBox(8, new Label("Story Points"), pointsCombo);
        HBox tshirtRow = new HBox(8, new Label("Size"), tshirtCombo);
        HBox timeRow = new HBox(8, new Label("Time"), timeEstimateField);

        Runnable updateEstimate = () -> {
            compactEstimateBox.getChildren().clear();
            switch (estimateTypeCombo.getValue()) {
                case "Story Points" -> compactEstimateBox.getChildren().add(storyPointsRow);
                case "T-shirt Sizes" -> compactEstimateBox.getChildren().add(tshirtRow);
                case "Time" -> compactEstimateBox.getChildren().add(timeRow);
            }
        };

        updateEstimate.run();
        estimateTypeCombo.setOnAction(e -> updateEstimate.run());

        ToggleButton mvpToggle = buildToggle(existing.mvp);
        ToggleButton sprintToggle = buildToggle(existing.sprintReady);

        Map<String, Long> sprintMap = SprintFetcher.fetchSprints();
        ComboBox<String> sprintDropdown = new ComboBox<>();
        sprintDropdown.getItems().add("None");
        sprintDropdown.getItems().addAll(sprintMap.keySet());
        sprintDropdown.setValue("None");

        existingSprintId = loadExistingSprintId(editingId);
        if (existingSprintId != null) {
            sprintMap.forEach((name, id) -> {
                if (id.equals(existingSprintId)) sprintDropdown.setValue(name);
            });
        }

        VBox rightColumn = new VBox(16,
                new Label("Estimate"), estimateTypeCombo, compactEstimateBox,
                new Label("Priority"), priorityCombo,
                new Label("Status"), statusCombo,
                new Label("Sprint"), sprintDropdown
        );

        ListView<CommentDto> commentsList = new ListView<>();
        commentsList.setPrefHeight(240);
        loadCommentsAsync(commentsList);

        TextArea newCommentArea = new TextArea();
        newCommentArea.setPrefRowCount(2);

        Button addCommentBtn = new Button("Add Comment");
        addCommentBtn.setOnAction(ev -> addComment(commentsList, newCommentArea));

        VBox commentsBox = new VBox(8, new Label("Comments"), commentsList,
                new Label("Add a comment"), newCommentArea, addCommentBtn);

        Button deleteBtn = new Button("Delete Task");
        deleteBtn.setOnAction(e -> deleteStory(editingId, primaryStage));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> primaryStage.close());

        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            try {
                CreateStoryDto dto = buildDto(existing, titleField, descriptionArea, acceptanceArea,
                        assigneeCombo, estimateTypeCombo, pointsCombo, tshirtCombo, timeEstimateField,
                        priorityCombo, statusCombo, mvpToggle, sprintToggle);

                updateStoryJson(editingId, dto, sprintDropdown, sprintMap);

                if (onStoryChanged != null) Platform.runLater(onStoryChanged);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Story updated");
                Platform.runLater(primaryStage::close);

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        BorderPane root = new BorderPane();
        root.setCenter(new VBox(20, leftColumn, commentsBox));
        root.setRight(rightColumn);
        root.setBottom(new HBox(10, deleteBtn, cancelBtn, saveBtn));

        Scene scene = new Scene(root, 1120, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Optional<CreateStoryDto> loadExistingFromBackend(long id) {
        if (id <= 0) return Optional.empty();
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/userstories/" + id))
                    .GET().build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return Optional.of(parseStory(new JSONObject(resp.body())));
            }

        } catch (Exception ignored) {}

        return Optional.empty();
    }

    private Long loadExistingSprintId(long id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/userstories/" + id))
                    .GET().build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            JSONObject obj = new JSONObject(resp.body());
            if (!obj.isNull("sprint")) return obj.getJSONObject("sprint").getLong("id");

        } catch (Exception ignored) {}

        return null;
    }

    private CreateStoryDto parseStory(JSONObject obj) {
        CreateStoryDto d = new CreateStoryDto();
        d.title = obj.optString("title", "");
        d.description = obj.optString("description", "");
        d.acceptanceCriteria = obj.optString("acceptanceCriteria", "");
        d.assignee = obj.optString("assignedTo", null);
        d.estimateType = "Story Points";
        int sp = obj.optInt("storyPoints", 0);
        d.storyPoints = sp == 0 ? null : sp;
        d.priority = obj.optString("priority", defaultPriority);
        d.status = obj.optString("status", defaultStatus);
        d.mvp = obj.optBoolean("mvp", false);
        d.sprintReady = obj.optBoolean("sprintReady", false);
        return d;
    }

    private CreateStoryDto buildDto(CreateStoryDto existing,
                                    TextField titleField, TextArea descriptionArea, TextArea acceptanceArea,
                                    ComboBox<String> assigneeCombo, ComboBox<String> estimateTypeCombo,
                                    ComboBox<Integer> pointsCombo, ComboBox<String> tshirtCombo,
                                    TextField timeEstimateField, ComboBox<String> priorityCombo,
                                    ComboBox<String> statusCombo,
                                    ToggleButton mvpToggle, ToggleButton sprintToggle) {

        CreateStoryDto dto = new CreateStoryDto();
        dto.title = titleField.getText().trim();
        dto.description = emptyToNull(descriptionArea.getText());
        dto.acceptanceCriteria = emptyToNull(acceptanceArea.getText());
        dto.assignee = assigneeCombo.getValue();
        dto.estimateType = estimateTypeCombo.getValue();

        switch (dto.estimateType) {
            case "Story Points" -> dto.storyPoints = pointsCombo.getValue();
            case "T-shirt Sizes" -> dto.size = tshirtCombo.getValue();
            case "Time" -> dto.timeEstimate = emptyToNull(timeEstimateField.getText());
        }

        dto.priority = priorityCombo.getValue();
        dto.status = statusCombo.getValue();
        dto.mvp = mvpToggle.isSelected();
        dto.sprintReady = sprintToggle.isSelected();

        return dto;
    }

    private void updateStoryJson(long id, CreateStoryDto dto,
                                 ComboBox<String> sprintDropdown,
                                 Map<String, Long> sprintMap) throws Exception {

        JSONObject obj = new JSONObject();
        obj.put("title", dto.title);
        obj.put("description", dto.description);
        obj.put("acceptanceCriteria", dto.acceptanceCriteria);
        obj.put("assignedTo", dto.assignee);
        obj.put("priority", dto.priority);
        obj.put("status", dto.status);
        obj.put("storyPoints", dto.storyPoints == null ? JSONObject.NULL : dto.storyPoints);
        obj.put("mvp", dto.mvp);
        obj.put("sprintReady", dto.sprintReady);
        obj.put("project", new JSONObject().put("id", ProjectSession.getProjectId()));

        if (!"None".equals(sprintDropdown.getValue())) {
            obj.put("sprint", new JSONObject().put("id", sprintMap.get(sprintDropdown.getValue())));
        } else obj.put("sprint", JSONObject.NULL);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/userstories/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() < 200 || resp.statusCode() >= 300)
            throw new RuntimeException("Backend update failed: " + resp.statusCode());
    }

    private void deleteStory(long id, Stage stage) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete this task?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);

        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            new Thread(() -> {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/userstories/" + id))
                            .DELETE().build();

                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Task deleted");
                    Platform.runLater(stage::close);

                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                }
            }).start();
        }
    }

    private void loadCommentsAsync(ListView<CommentDto> list) {
        if (editingId <= 0) return;
        new Thread(() -> {
            try {
                List<CommentDto> items = loadCommentsFromBackend(editingId);
                Platform.runLater(() -> list.getItems().setAll(items));
            } catch (Exception ignored) {}
        }).start();
    }

    private List<CommentDto> loadCommentsFromBackend(long storyId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/userstories/" + storyId + "/comments"))
                .GET().build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        JSONArray arr = new JSONArray(resp.body());
        List<CommentDto> list = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);

            CommentDto c = new CommentDto();
            c.id = o.getLong("id");
            c.storyId = o.getLong("storyId");
            c.author = o.optString("author", "");
            c.body = o.optString("body", "");
            c.createdAt = o.optString("createdAt", "");

            list.add(c);
        }

        return list;
    }

    private void addComment(ListView<CommentDto> list, TextArea area) {
        if (editingId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid story ID");
            return;
        }

        String body = Optional.ofNullable(area.getText()).orElse("").trim();
        if (body.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Comment cannot be empty");
            return;
        }

        String author = Optional.ofNullable(UserSession.getCurrentUser()).orElse("Anonymous");

        new Thread(() -> {
            try {
                JSONObject obj = new JSONObject();
                obj.put("author", author);
                obj.put("body", body);

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/userstories/" + editingId + "/comments"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                        .build();

                httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(area::clear);
                loadCommentsAsync(list);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    public static class CreateStoryDto {
        public String title;
        public String description;
        public String acceptanceCriteria;
        public String assignee;
        public String estimateType;
        public Integer storyPoints;
        public String size;
        public String timeEstimate;
        public String priority;
        public String status;
        public Boolean mvp;
        public Boolean sprintReady;
    }

    public static class CommentDto {
        public long id;
        public long storyId;
        public String author;
        public String body;
        public String createdAt;
    }

    private ToggleButton buildToggle(boolean initialOn) {
        ToggleButton toggle = new ToggleButton();
        toggle.setPrefWidth(52);
        toggle.setPrefHeight(28);
        toggle.setText("");

        StackPane knob = new StackPane(new Circle(11, Color.WHITE));
        toggle.setGraphic(knob);

        Runnable update = () -> {
            if (toggle.isSelected()) {
                toggle.setStyle("-fx-background-color:#4ade80; -fx-background-radius:14;");
                knob.setTranslateX(10);
            } else {
                toggle.setStyle("-fx-background-color:#d1d5db; -fx-background-radius:14;");
                knob.setTranslateX(-10);
            }
        };

        toggle.selectedProperty().addListener((a, b, c) -> update.run());
        toggle.setSelected(initialOn);
        update.run();

        return toggle;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
