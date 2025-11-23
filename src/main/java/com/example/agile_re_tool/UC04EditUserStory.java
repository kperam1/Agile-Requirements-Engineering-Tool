package com.example.agile_re_tool;

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

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.example.agile_re_tool.session.UserSession;

public class UC04EditUserStory extends Application {

    private final UserStoryDao dao = new UserStoryDao();

    private List<String> assignees = List.of();
    private List<String> estimateTypes = List.of();
    private List<String> tshirtSizes = List.of();
    private int defaultStoryPoints = 3;
    private String defaultPriority = "Medium";
    private String defaultStatus = "To Do";

    private static final List<Integer> FIBONACCI_POINTS = List.of(1, 2, 3, 5, 8, 13, 21);

    private long editingId;

    public UC04EditUserStory() {}

    public UC04EditUserStory(long storyId) {
        this.editingId = storyId;
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
        primaryStage.setTitle("UC-04 - Edit User Story");

        loadConfig();

        Optional<CreateStoryDto> existingOpt = dao.findById(editingId);
        CreateStoryDto existing = existingOpt.orElseGet(() -> {
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
            return d;
        });

        Label heading = new Label("Edit Story");
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
        estimateTypeCombo.getSelectionModel().select(Optional.ofNullable(existing.estimateType).orElse("Story Points"));

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
        priorityCombo.getSelectionModel().select(Optional.ofNullable(existing.priority).orElse(defaultPriority));

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("To Do", "In Progress", "Testing", "Done");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.getSelectionModel().select(Optional.ofNullable(existing.status).orElse(defaultStatus));

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

        // Comments Section
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
                String time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(item.createdAt.atZone(ZoneId.systemDefault()));
                String author = Optional.ofNullable(item.author).filter(a -> !a.isBlank()).orElse("Anonymous");
                meta.setText(author + " • " + time);
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
                    try {
                        dao.updateComment(item.id, newBody);
                        current.body = newBody;
                        exitEditMode(true);
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + ex.getMessage());
                    }
                });
                deleteBtn.setOnAction(e -> {
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete this comment?", ButtonType.YES, ButtonType.NO);
                    a.setHeaderText(null);
                    Optional<ButtonType> r = a.showAndWait();
                    if (r.isPresent() && r.get() == ButtonType.YES) {
                        try {
                            dao.deleteComment(item.id);
                            ((ListView<CommentDto>) getListView()).getItems().remove(item);
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Delete failed: " + ex.getMessage());
                        }
                    }
                });
            }
        });

        TextArea newCommentArea = new TextArea();
        newCommentArea.setPromptText("Write a comment...");
        newCommentArea.setPrefRowCount(2);

        Button addCommentBtn = new Button("Add Comment");
        addCommentBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding:6 12;");

        Runnable refreshComments = () -> Platform.runLater(() -> {
            if (editingId <= 0) {
                commentsList.getItems().clear();
                newCommentArea.setDisable(true);
                addCommentBtn.setDisable(true);
                return;
            }
            newCommentArea.setDisable(false);
            addCommentBtn.setDisable(false);
            List<CommentDto> items = dao.listComments(editingId);
            commentsList.getItems().setAll(items);
        });

        refreshComments.run();

        addCommentBtn.setOnAction(e -> {
            if (editingId <= 0) {
                showAlert(Alert.AlertType.WARNING, "Save First", "Please save the story before adding comments.");
                return;
            }
            String body = Optional.ofNullable(newCommentArea.getText()).orElse("").trim();
            if (body.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation", "Comment cannot be empty.");
                return;
            }
            String author = Optional.ofNullable(UserSession.getCurrentUser()).filter(s -> !s.isBlank()).orElse("Anonymous");
            try {
                dao.addComment(editingId, author, body);
                newCommentArea.clear();
                refreshComments.run();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add comment: " + ex.getMessage());
                ex.printStackTrace();
            }
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

        // Layout: left column includes form + comments; right column is meta
        VBox leftWithComments = new VBox(16, leftColumn, commentsBox);
        leftWithComments.setPadding(new Insets(12));

        BorderPane root = new BorderPane();
        root.setCenter(leftWithComments);
        root.setRight(rightColumn);
        root.setBottom(buttonsRow);

        cancelBtn.setOnAction(e -> primaryStage.close());

        deleteBtn.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete this task?", ButtonType.YES, ButtonType.NO);
            a.setHeaderText(null);
            Optional<ButtonType> res = a.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.YES) {
                try {
                    dao.delete(editingId);
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Task deleted.");
                    primaryStage.close();
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Delete failed: " + ex.getMessage());
                }
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

            try {
                dao.update(editingId, dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "User story updated (id=" + editingId + ")");
                primaryStage.close();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "DB Error", ex.getMessage());
            }
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
            a.showAndWait();
        });
    }

    public static void main(String[] args) {
        UserStoryDao.initDatabase();
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
    }

    public static class UserStoryDao {
        private static final String JDBC_URL = "jdbc:h2:./data/agile;AUTO_SERVER=TRUE";

        static {
            try {
                Class.forName("org.h2.Driver");
                initDatabase();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("H2 Driver not found", e);
            }
        }

        static void initDatabase() {
            try (Connection c = DriverManager.getConnection(JDBC_URL)) {
                try (Statement s = c.createStatement()) {
                    s.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS user_story (" +
                            "id IDENTITY PRIMARY KEY, " +
                            "title VARCHAR(255) NOT NULL, " +
                            "description CLOB, " +
                            "acceptance_criteria CLOB, " +
                            "assignee VARCHAR(120), " +
                            "estimate_type VARCHAR(50), " +
                            "story_points INT, " +
                            "size VARCHAR(20), " +
                            "time_estimate VARCHAR(120), " +
                            "priority VARCHAR(50), " +
                            "status VARCHAR(50), " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
                    );
                    s.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS comment (" +
                            "id IDENTITY PRIMARY KEY, " +
                            "story_id BIGINT NOT NULL, " +
                            "author VARCHAR(120), " +
                            "body CLOB NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
                    );
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to init DB: " + e.getMessage(), e);
            }
        }

        public long create(CreateStoryDto dto) throws SQLException {
            String sql = "INSERT INTO user_story (title, description, acceptance_criteria, assignee, estimate_type, story_points, size, time_estimate, priority, status, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, dto.title);
                ps.setString(2, dto.description);
                ps.setString(3, dto.acceptanceCriteria);
                ps.setString(4, dto.assignee);
                ps.setString(5, dto.estimateType);
                if (dto.storyPoints == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, dto.storyPoints);
                ps.setString(7, dto.size);
                ps.setString(8, dto.timeEstimate);
                ps.setString(9, dto.priority);
                ps.setString(10, dto.status);
                ps.setTimestamp(11, Timestamp.from(Instant.now()));

                int affected = ps.executeUpdate();
                if (affected == 0) throw new SQLException("Insert failed, no rows affected.");
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                    throw new SQLException("Insert succeeded but no ID obtained.");
                }
            }
        }

        public Optional<CreateStoryDto> findById(long id) {
            String sql = "SELECT title, description, acceptance_criteria, assignee, estimate_type, story_points, size, time_estimate, priority, status FROM user_story WHERE id = ?";
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        CreateStoryDto d = new CreateStoryDto();
                        d.title = rs.getString("title");
                        d.description = rs.getString("description");
                        d.acceptanceCriteria = rs.getString("acceptance_criteria");
                        d.assignee = rs.getString("assignee");
                        d.estimateType = rs.getString("estimate_type");
                        int sp = rs.getInt("story_points");
                        d.storyPoints = rs.wasNull() ? null : sp;
                        d.size = rs.getString("size");
                        d.timeEstimate = rs.getString("time_estimate");
                        d.priority = rs.getString("priority");
                        d.status = rs.getString("status");
                        return Optional.of(d);
                    } else return Optional.empty();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        }

        public void update(long id, CreateStoryDto dto) throws SQLException {
            String sql = "UPDATE user_story SET title=?, description=?, acceptance_criteria=?, assignee=?, estimate_type=?, story_points=?, size=?, time_estimate=?, priority=?, status=? WHERE id=?";
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, dto.title);
                ps.setString(2, dto.description);
                ps.setString(3, dto.acceptanceCriteria);
                ps.setString(4, dto.assignee);
                ps.setString(5, dto.estimateType);
                if (dto.storyPoints == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, dto.storyPoints);
                ps.setString(7, dto.size);
                ps.setString(8, dto.timeEstimate);
                ps.setString(9, dto.priority);
                ps.setString(10, dto.status);
                ps.setLong(11, id);

                ps.executeUpdate();
            }
        }

        public void delete(long id) throws SQLException {
            String sql = "DELETE FROM user_story WHERE id = ?";
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        }

        // Comments DAO
        public List<CommentDto> listComments(long storyId) {
            if (storyId <= 0) return Collections.emptyList();
            String sql = "SELECT id, author, body, created_at FROM comment WHERE story_id = ? ORDER BY created_at ASC";
            List<CommentDto> out = new ArrayList<>();
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, storyId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CommentDto d = new CommentDto();
                        d.id = rs.getLong("id");
                        d.storyId = storyId;
                        d.author = rs.getString("author");
                        d.body = rs.getString("body");
                        Timestamp ts = rs.getTimestamp("created_at");
                        d.createdAt = ts == null ? Instant.now() : ts.toInstant();
                        out.add(d);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return out;
        }

        public void addComment(long storyId, String author, String body) throws SQLException {
            if (storyId <= 0) throw new IllegalArgumentException("Invalid story id");
            String sql = "INSERT INTO comment (story_id, author, body, created_at) VALUES (?, ?, ?, ?)";
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, storyId);
                ps.setString(2, author);
                ps.setString(3, body);
                ps.setTimestamp(4, Timestamp.from(Instant.now()));
                ps.executeUpdate();
            }
        }

        public void updateComment(long id, String body) throws SQLException {
            String sql = "UPDATE comment SET body=? WHERE id=?";
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, body);
                ps.setLong(2, id);
                ps.executeUpdate();
            }
        }

        public void deleteComment(long id) throws SQLException {
            String sql = "DELETE FROM comment WHERE id=?";
            try (Connection c = DriverManager.getConnection(JDBC_URL);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        }
    }

    public static class CommentDto {
        public long id;
        public long storyId;
        public String author;
        public String body;
        public Instant createdAt;
    }
}