package com.example.agile_re_tool;

import com.example.agile_re_tool.session.UserSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class UC04EditUserStory extends Application {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final Runnable onStoryChanged;

    private List<String> assignees = List.of();
    private List<String> estimateTypes = List.of();
    private List<String> tshirtSizes = List.of();
    private int defaultStoryPoints = 3;
    private String defaultPriority = "Medium";
    private String defaultStatus = "To Do";

    private static final List<Integer> FIBONACCI_POINTS = List.of(1, 2, 3, 5, 8, 13, 21);

    private long editingId;

    public UC04EditUserStory() {
        this(0L, null);
    }

    public UC04EditUserStory(long storyId) {
        this(storyId, null);
    }

    public UC04EditUserStory(long storyId, Runnable onStoryChanged) {
        this.editingId = storyId;
        this.onStoryChanged = onStoryChanged;
    }

    public void openWindow() {
        Stage stage = new Stage();
        try {
            start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(editingId > 0 ? "Edit User Story #" + editingId
                : "UC-04 - Edit User Story");

        loadConfig();

        CreateStoryDto existing = loadExistingFromBackend(editingId)
                .orElseGet(() -> {
                    CreateStoryDto d = new CreateStoryDto();
                    d.title = "";
                    d.description = "";
                    d.acceptanceCriteria = "";
                    d.assignee = null;
                    d.estimateType = "Story Points";
                    d.storyPoints = defaultStoryPoints;
                    d.size = null;
                    d.timeEstimate = null;
                    d.priority = defaultPriority;
                    d.status = defaultStatus;
                    d.mvp = false;
                    d.sprintReady = false;
                    return d;
                });

        Label heading = new Label(
                editingId > 0 ? "Edit Story #" + editingId : "Edit Story"
        );
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #111827;");

        Label subText = new Label("Update task details and save your changes.");
        subText.setStyle("-fx-text-fill: #6b7280;");

        TextField titleField = new TextField(existing.title);
        titleField.setPromptText("Enter task title");
        titleField.setMaxWidth(Double.MAX_VALUE);

        TextArea descriptionArea = new TextArea(existing.description);
        descriptionArea.setPromptText("Enter task description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setMaxWidth(Double.MAX_VALUE);

        TextArea acceptanceArea = new TextArea(existing.acceptanceCriteria);
        acceptanceArea.setPromptText("Enter acceptance criteria (e.g., Given... When... Then...)");
        acceptanceArea.setPrefRowCount(2);
        acceptanceArea.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> assigneeCombo = new ComboBox<>();
        assigneeCombo.getItems().setAll(assignees);
        assigneeCombo.setPromptText("Select assignee");
        assigneeCombo.setVisibleRowCount(6);
        assigneeCombo.setMaxWidth(Double.MAX_VALUE);

        assigneeCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(assigneeCombo.getPromptText());
                    setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #111827; -fx-font-style: normal;");
                }
            }
        });

        assigneeCombo.getSelectionModel().clearSelection();
        if (existing.assignee != null && assignees.contains(existing.assignee)) {
            assigneeCombo.getSelectionModel().select(existing.assignee);
        }

        GridPane leftGrid = new GridPane();
        leftGrid.setHgap(12);
        leftGrid.setVgap(12);
        leftGrid.setPadding(new Insets(8, 12, 8, 12));

        ColumnConstraints lc0 = new ColumnConstraints();
        lc0.setPrefWidth(140);
        lc0.setHalignment(HPos.LEFT);

        ColumnConstraints lc1 = new ColumnConstraints();
        lc1.setHgrow(Priority.ALWAYS);

        leftGrid.getColumnConstraints().addAll(lc0, lc1);

        int r = 0;
        leftGrid.add(new Label("Task Title *"), 0, r); leftGrid.add(titleField, 1, r++);
        leftGrid.add(new Label("Description"), 0, r); leftGrid.add(descriptionArea, 1, r++);
        leftGrid.add(new Label("Acceptance Criteria"), 0, r); leftGrid.add(acceptanceArea, 1, r++);
        leftGrid.add(new Label("Assignee"), 0, r); leftGrid.add(assigneeCombo, 1, r++);

        VBox leftColumn = new VBox(10, heading, subText, leftGrid);
        leftColumn.setPadding(new Insets(12));
        leftColumn.setAlignment(Pos.TOP_LEFT);

        ComboBox<String> estimateTypeCombo = new ComboBox<>();
        estimateTypeCombo.getItems().setAll(estimateTypes);
        estimateTypeCombo.setMaxWidth(Double.MAX_VALUE);
        estimateTypeCombo.setPromptText("Estimate Type");
        estimateTypeCombo.getSelectionModel().select(
                Optional.ofNullable(existing.estimateType).orElse("Story Points")
        );

        ComboBox<Integer> pointsCombo = new ComboBox<>();
        pointsCombo.getItems().addAll(FIBONACCI_POINTS);
        pointsCombo.setPromptText("Story Points");
        pointsCombo.setMaxWidth(80);
        if (existing.storyPoints != null && FIBONACCI_POINTS.contains(existing.storyPoints)) {
            pointsCombo.getSelectionModel().select(existing.storyPoints);
        } else {
            pointsCombo.getSelectionModel().select(defaultStoryPoints);
        }

        ComboBox<String> tshirtCombo = new ComboBox<>();
        tshirtCombo.getItems().setAll(tshirtSizes);
        tshirtCombo.setMaxWidth(Double.MAX_VALUE);
        if (existing.size != null) tshirtCombo.getSelectionModel().select(existing.size);

        TextField timeEstimateField = new TextField();
        timeEstimateField.setPromptText("e.g., 4 hours, 2 days");
        timeEstimateField.setMaxWidth(Double.MAX_VALUE);
        if (existing.timeEstimate != null) timeEstimateField.setText(existing.timeEstimate);

        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.setMaxWidth(Double.MAX_VALUE);
        priorityCombo.getSelectionModel().select(
                Optional.ofNullable(existing.priority).orElse(defaultPriority)
        );

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Backlog", "To Do", "In Progress", "Testing", "Done");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.getSelectionModel().select(
                Optional.ofNullable(existing.status).orElse(defaultStatus)
        );

        VBox compactEstimateBox = new VBox(8);
        compactEstimateBox.setPadding(new Insets(8));

        HBox storyPointsRow = new HBox(8);
        storyPointsRow.getChildren().addAll(new Label("Story Points:"), pointsCombo);

        HBox tshirtRow = new HBox(8);
        tshirtRow.getChildren().addAll(new Label("Size:"), tshirtCombo);

        HBox timeRow = new HBox(8);
        timeRow.getChildren().addAll(new Label("Time:"), timeEstimateField);

        Runnable updateEstimateCompact = () -> {
            compactEstimateBox.getChildren().clear();
            String mode = estimateTypeCombo.getValue();
            if ("Story Points".equals(mode)) compactEstimateBox.getChildren().add(storyPointsRow);
            else if ("T-shirt Sizes".equals(mode)) compactEstimateBox.getChildren().add(tshirtRow);
            else if ("Time".equals(mode)) compactEstimateBox.getChildren().add(timeRow);
        };

        updateEstimateCompact.run();
        estimateTypeCombo.setOnAction(e -> updateEstimateCompact.run());

        Label assignedHeader = new Label("Assigned To");
        assignedHeader.setStyle("-fx-font-weight: 600; -fx-text-fill: #111827;");

        HBox assignedCard = new HBox(12);
        assignedCard.setPadding(new Insets(10));
        assignedCard.setStyle("-fx-background-color: #f3f8fb; -fx-background-radius: 10;");
        assignedCard.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle = new Circle(22, Color.web("#e6f4ff"));
        Text initials = new Text("");
        initials.setStyle("-fx-font-weight: 700; -fx-fill: #0b66c2;");
        StackPane avatarPane = new StackPane(avatarCircle, initials);

        VBox assignedText = new VBox(2);
        Label assignedNameLabel = new Label("");
        assignedNameLabel.setStyle("-fx-font-weight: 700;");
        Label assignedRoleLabel = new Label("Developer");
        assignedRoleLabel.setStyle("-fx-text-fill: #6b7280;");

        assignedText.getChildren().addAll(assignedNameLabel, assignedRoleLabel);
        assignedCard.getChildren().addAll(avatarPane, assignedText);
        assignedCard.setVisible(false);

        java.util.function.Consumer<String> updateAssigned = name -> {
            if (name == null || name.isBlank()) {
                assignedCard.setVisible(false);
            } else {
                assignedCard.setVisible(true);
                assignedNameLabel.setText(name);
                String[] parts = name.trim().split("\\s+");
                String inits = Arrays.stream(parts)
                        .map(s -> s.substring(0, 1).toUpperCase())
                        .limit(2).collect(Collectors.joining());
                initials.setText(inits);
            }
        };

        updateAssigned.accept(existing.assignee);
        assigneeCombo.setOnAction(e -> updateAssigned.accept(assigneeCombo.getValue()));

        VBox rightColumn = new VBox(16);
        rightColumn.setPadding(new Insets(12));
        rightColumn.setPrefWidth(320);
        rightColumn.getChildren().addAll(
                new Label("Estimate"), estimateTypeCombo, compactEstimateBox,
                new Label("Priority"), priorityCombo,
                new Label("Status"), statusCombo,
                assignedHeader, assignedCard
        );

        ToggleButton mvpToggle = buildToggle(Boolean.TRUE.equals(existing.mvp));
        Label mvpTitle = new Label("MVP");
        mvpTitle.setStyle("-fx-font-weight: 600;");
        Label mvpHint = new Label("Mark as MVP story");
        mvpHint.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");
        VBox mvpTextBox = new VBox(2, mvpTitle, mvpHint);
        HBox mvpRow = new HBox(12, mvpTextBox, mvpToggle);
        mvpRow.setAlignment(Pos.CENTER_LEFT);

        ToggleButton sprintToggle = buildToggle(Boolean.TRUE.equals(existing.sprintReady));
        Label srTitle = new Label("Sprint Ready");
        srTitle.setStyle("-fx-font-weight: 600;");
        Label srHint = new Label("Mark as ready to move into sprint");
        srHint.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");
        VBox srTextBox = new VBox(2, srTitle, srHint);
        HBox srRow = new HBox(12, srTextBox, sprintToggle);
        srRow.setAlignment(Pos.CENTER_LEFT);

        VBox flagsBox = new VBox(10, mvpRow, srRow);
        flagsBox.setPadding(new Insets(12));
        flagsBox.setStyle("-fx-background-color:#f9fafb; -fx-background-radius:10; -fx-border-color:#e5e7eb; -fx-border-radius:10;");

        Label commentsHeader = new Label("Comments");
        commentsHeader.setStyle("-fx-font-weight: 700; -fx-font-size: 14;");

        ListView<CommentDto> commentsList = new ListView<>();
        commentsList.setPrefHeight(260);
        commentsList.setCellFactory(lv -> new ListCell<>() {
            private final VBox container = new VBox(4);
            private final Label meta = new Label();
            private final Label bodyLabel = new Label();
            private final HBox actions = new HBox(8);
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button saveBtn = new Button("Save");
            private final Button cancelBtn = new Button("Cancel");
            private TextArea editorArea;
            private CommentDto current;

            {
                meta.setStyle("-fx-font-size:11; -fx-text-fill:#555;");
                bodyLabel.setWrapText(true);
                editBtn.setStyle("-fx-background-color:#3b82f6; -fx-text-fill:white; -fx-font-size:11; -fx-padding:4 8; -fx-background-radius:6;");
                deleteBtn.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-font-size:11; -fx-padding:4 8; -fx-background-radius:6;");
                saveBtn.setStyle("-fx-background-color:#10b981; -fx-text-fill:white; -fx-font-size:11; -fx-padding:4 8; -fx-background-radius:6;");
                cancelBtn.setStyle("-fx-background-color:#6b7280; -fx-text-fill:white; -fx-font-size:11; -fx-padding:4 8; -fx-background-radius:6;");
                actions.getChildren().addAll(editBtn, deleteBtn);
                actions.setVisible(false);
                container.getChildren().addAll(meta, bodyLabel, actions);
                container.setPadding(new Insets(8));
            }

            private void enterEditMode() {
                if (current == null) return;
                editorArea = new TextArea(current.body);
                editorArea.setPrefRowCount(3);
                container.getChildren().set(1, editorArea);
                actions.getChildren().setAll(saveBtn, cancelBtn);
            }

            private void exitEditMode(boolean refresh) {
                container.getChildren().set(1, bodyLabel);
                actions.getChildren().setAll(editBtn, deleteBtn);
                if (refresh) bodyLabel.setText(current.body);
            }

            @Override
            protected void updateItem(CommentDto item, boolean empty) {
                super.updateItem(item, empty);
                current = item;
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                String author = Optional.ofNullable(item.author).filter(a -> !a.isBlank()).orElse("Anonymous");
                String time = Optional.ofNullable(item.createdAt).orElse("");
                meta.setText(author + (time.isBlank() ? "" : " • " + time));
                bodyLabel.setText(Optional.ofNullable(item.body).orElse(""));

                String currentUser = Optional.ofNullable(UserSession.getCurrentUser()).orElse("");
                boolean mine = !currentUser.isBlank() && currentUser.equals(item.author);
                actions.setVisible(mine);
                setGraphic(container);

                editBtn.setOnAction(e -> enterEditMode());
                cancelBtn.setOnAction(e -> exitEditMode(false));
                saveBtn.setOnAction(e -> {
                    String newBody = Optional.ofNullable(editorArea.getText()).orElse("").trim();
                    if (newBody.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Validation", "Comment cannot be empty.");
                        return;
                    }
                    new Thread(() -> {
                        try {
                            updateCommentOnBackend(item.id, newBody);
                            current.body = newBody;
                            Platform.runLater(() -> exitEditMode(true));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + ex.getMessage());
                        }
                    }).start();
                });
                deleteBtn.setOnAction(e -> {
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete this comment?", ButtonType.YES, ButtonType.NO);
                    a.setHeaderText(null);
                    Optional<ButtonType> r = a.showAndWait();
                    if (r.isPresent() && r.get() == ButtonType.YES) {
                        new Thread(() -> {
                            try {
                                deleteCommentOnBackend(item.id);
                                Platform.runLater(() ->
                                        ((ListView<CommentDto>) getListView()).getItems().remove(item)
                                );
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Error", "Delete failed: " + ex.getMessage());
                            }
                        }).start();
                    }
                });
            }
        });

        TextArea newCommentArea = new TextArea();
        newCommentArea.setPromptText("Write a comment...");
        newCommentArea.setPrefRowCount(2);

        Button addCommentBtn = new Button("Add Comment");
        addCommentBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding:6 12;");

        Runnable refreshComments = () -> {
            if (editingId <= 0) {
                Platform.runLater(() -> {
                    commentsList.getItems().clear();
                    newCommentArea.setDisable(true);
                    addCommentBtn.setDisable(true);
                });
                return;
            }
            new Thread(() -> {
                try {
                    List<CommentDto> items = loadCommentsFromBackend(editingId);
                    Platform.runLater(() -> {
                        newCommentArea.setDisable(false);
                        addCommentBtn.setDisable(false);
                        commentsList.getItems().setAll(items);
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        };

        refreshComments.run();

        addCommentBtn.setOnAction(e -> {
            if (editingId <= 0) {
                showAlert(Alert.AlertType.WARNING, "Save First", "This story does not have a valid id.");
                return;
            }
            String body = Optional.ofNullable(newCommentArea.getText()).orElse("").trim();
            if (body.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation", "Comment cannot be empty.");
                return;
            }
            String author = Optional.ofNullable(UserSession.getCurrentUser())
                    .filter(s -> !s.isBlank())
                    .orElse("Anonymous");

            new Thread(() -> {
                try {
                    addCommentOnBackend(editingId, author, body);
                    Platform.runLater(() -> newCommentArea.clear());
                    refreshComments.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add comment: " + ex.getMessage());
                }
            }).start();
        });

        VBox commentsBox = new VBox(8,
                commentsHeader,
                commentsList,
                new Label("Add a comment"),
                newCommentArea,
                addCommentBtn
        );
        commentsBox.setPadding(new Insets(12));
        commentsBox.setStyle("-fx-background-color:#f9fafb; -fx-background-radius:10; -fx-border-color:#e5e7eb; -fx-border-radius:10;");

        Button deleteBtn = new Button("Delete Task");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #f87171; -fx-text-fill: #ef4444; -fx-background-radius:8; -fx-border-radius:8; -fx-padding:8 14;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #cbd5e1; -fx-text-fill: #374151; -fx-background-radius:8; -fx-border-radius:8; -fx-padding:8 14;");

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: linear-gradient(#2563eb, #1e40af); -fx-text-fill: white; -fx-background-radius:8; -fx-padding:8 14;");

        HBox buttonsRow = new HBox(12, deleteBtn, cancelBtn, saveBtn);
        buttonsRow.setPadding(new Insets(12));
        buttonsRow.setAlignment(Pos.CENTER_RIGHT);

        VBox leftWithComments = new VBox(16, leftColumn, flagsBox, commentsBox);
        leftWithComments.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setCenter(leftWithComments);
        root.setRight(rightColumn);
        root.setBottom(buttonsRow);

        cancelBtn.setOnAction(e -> primaryStage.close());

        deleteBtn.setOnAction(e -> {
            if (editingId <= 0) {
                showAlert(Alert.AlertType.WARNING, "Cannot delete", "This story does not have a valid id.");
                return;
            }
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete this task?", ButtonType.YES, ButtonType.NO);
            a.setHeaderText(null);
            Optional<ButtonType> res = a.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        deleteStory(editingId);
                        if (onStoryChanged != null) {
                            Platform.runLater(onStoryChanged);
                        }
                        showAlert(Alert.AlertType.INFORMATION, "Deleted", "Task deleted.");
                        Platform.runLater(primaryStage::close);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Delete failed: " + ex.getMessage());
                    }
                }).start();
            }
        });

        saveBtn.setOnAction(e -> {
            if (titleField.getText().isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Validation error", "Title is required");
                return;
            }

            CreateStoryDto dto = new CreateStoryDto();
            dto.title = titleField.getText().trim();
            dto.description = emptyToNull(descriptionArea.getText());
            dto.acceptanceCriteria = emptyToNull(acceptanceArea.getText());
            dto.assignee = assigneeCombo.getValue();
            dto.estimateType = estimateTypeCombo.getValue();

            if ("Story Points".equals(dto.estimateType)) dto.storyPoints = pointsCombo.getValue();
            else dto.storyPoints = null;

            if ("T-shirt Sizes".equals(dto.estimateType)) dto.size = tshirtCombo.getValue();
            else dto.size = null;

            if ("Time".equals(dto.estimateType)) dto.timeEstimate = emptyToNull(timeEstimateField.getText());
            else dto.timeEstimate = null;

            dto.priority = priorityCombo.getValue();
            dto.status = statusCombo.getValue();
            dto.mvp = mvpToggle.isSelected();
            dto.sprintReady = sprintToggle.isSelected();

            new Thread(() -> {
                try {
                    if (editingId > 0) {
                        updateStory(editingId, dto);
                        if (onStoryChanged != null) {
                            Platform.runLater(onStoryChanged);
                        }
                        showAlert(Alert.AlertType.INFORMATION, "Success", "User story updated (id=" + editingId + ")");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Warning", "No valid id – cannot update backend.");
                    }
                    Platform.runLater(primaryStage::close);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + ex.getMessage());
                }
            }).start();
        });

        Scene scene = new Scene(root, 1120, 720);

        try {
            String css = Optional.ofNullable(getClass().getResource("/uc03-style.css"))
                    .map(u -> u.toExternalForm()).orElse(null);
            if (css != null) scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ToggleButton buildToggle(boolean initialOn) {
        ToggleButton toggle = new ToggleButton();
        toggle.setPrefWidth(52);
        toggle.setPrefHeight(28);
        toggle.setFocusTraversable(false);
        toggle.setText("");
        StackPane knob = new StackPane();
        Circle c = new Circle(11, Color.WHITE);
        knob.getChildren().add(c);
        knob.setMaxSize(22, 22);
        knob.setMinSize(22, 22);
        toggle.setGraphic(knob);
        toggle.setGraphicTextGap(0);

        Runnable applyStyle = () -> {
            if (toggle.isSelected()) {
                toggle.setStyle("-fx-background-color: #4ade80; -fx-background-radius: 14; -fx-padding: 0 4 0 0;");
                knob.setTranslateX(10);
            } else {
                toggle.setStyle("-fx-background-color: #d1d5db; -fx-background-radius: 14; -fx-padding: 0 0 0 4;");
                knob.setTranslateX(-10);
            }
        };

        toggle.selectedProperty().addListener((obs, ov, nv) -> applyStyle.run());
        toggle.setSelected(initialOn);
        applyStyle.run();
        return toggle;
    }

    private Optional<CreateStoryDto> loadExistingFromBackend(long id) {
        if (id <= 0) return Optional.empty();
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/userstories/" + id))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                JSONObject obj = new JSONObject(resp.body());
                CreateStoryDto d = new CreateStoryDto();
                d.title = obj.optString("title", "");
                d.description = obj.optString("description", "");
                d.acceptanceCriteria = obj.optString("acceptanceCriteria", "");
                d.assignee = obj.isNull("assignedTo") ? null : obj.optString("assignedTo", null);
                d.estimateType = "Story Points";
                int sp = obj.optInt("storyPoints", 0);
                d.storyPoints = sp == 0 ? null : sp;
                d.size = null;
                d.timeEstimate = null;
                d.priority = obj.optString("priority", defaultPriority);
                d.status = obj.optString("status", defaultStatus);
                d.mvp = obj.has("mvp") && !obj.isNull("mvp") && obj.getBoolean("mvp");
                d.sprintReady = obj.has("sprintReady") && !obj.isNull("sprintReady") && obj.getBoolean("sprintReady");
                return Optional.of(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void updateStory(long id, CreateStoryDto dto) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("title", dto.title);
        obj.put("description", dto.description);
        obj.put("acceptanceCriteria", dto.acceptanceCriteria);
        obj.put("assignedTo", dto.assignee);
        obj.put("priority", dto.priority);
        obj.put("status", dto.status);
        if (dto.storyPoints != null) obj.put("storyPoints", dto.storyPoints);
        else obj.put("storyPoints", JSONObject.NULL);
        obj.put("mvp", dto.mvp);
        obj.put("sprintReady", dto.sprintReady);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/userstories/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Backend update failed: " + resp.statusCode());
        }
    }

    private void deleteStory(long id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/userstories/" + id))
                .DELETE()
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Backend delete failed: " + resp.statusCode());
        }
    }

    private List<CommentDto> loadCommentsFromBackend(long storyId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/userstories/" + storyId + "/comments"))
                .GET()
                .build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Failed to load comments: " + resp.statusCode());
        }
        JSONArray arr = new JSONArray(resp.body());
        List<CommentDto> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            CommentDto c = new CommentDto();
            c.id = o.getLong("id");
            c.storyId = o.getLong("storyId");
            c.author = o.optString("author", null);
            c.body = o.optString("body", "");
            c.createdAt = o.optString("createdAt", "");
            list.add(c);
        }
        return list;
    }

    private void addCommentOnBackend(long storyId, String author, String body) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("author", author);
        obj.put("body", body);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/userstories/" + storyId + "/comments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Failed to add comment: " + resp.statusCode());
        }
    }

    private void updateCommentOnBackend(long commentId, String body) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("body", body);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/comments/" + commentId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(obj.toString()))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Failed to update comment: " + resp.statusCode());
        }
    }

    private void deleteCommentOnBackend(long commentId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/comments/" + commentId))
                .DELETE()
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Failed to delete comment: " + resp.statusCode());
        }
    }

    private void loadConfig() {
        Properties p = new Properties();

        try (InputStream in = getClass().getResourceAsStream("/uc-config.properties")) {
            if (in != null) {
                p.load(in);

                String a = p.getProperty("assignees");
                if (a != null && !a.isBlank()) assignees = Arrays.stream(a.split(","))
                        .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

                String et = p.getProperty("estimate.types");
                if (et != null && !et.isBlank())
                    estimateTypes = Arrays.stream(et.split(",")).map(String::trim).collect(Collectors.toList());
                else estimateTypes = List.of("Story Points", "T-shirt Sizes", "Time");

                String ts = p.getProperty("tshirt.sizes");
                if (ts != null && !ts.isBlank())
                    tshirtSizes = Arrays.stream(ts.split(",")).map(String::trim).collect(Collectors.toList());
                else tshirtSizes = List.of("XS", "S", "M", "L", "XL", "XXL");

                String sp = p.getProperty("default.story.points");
                if (sp != null) {
                    try { defaultStoryPoints = Integer.parseInt(sp.trim()); } catch (NumberFormatException ignored) {}
                }

                defaultPriority = Optional.ofNullable(p.getProperty("default.priority")).orElse(defaultPriority);
                defaultStatus = Optional.ofNullable(p.getProperty("default.status")).orElse(defaultStatus);
            }
        } catch (IOException ignored) {}
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private void showAlert(Alert.AlertType t, String title, String body) {
        Platform.runLater(() -> {
            Alert a = new Alert(t, body, ButtonType.OK);
            a.setTitle(title);
            a.setHeaderText(null);
            a.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
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
}
