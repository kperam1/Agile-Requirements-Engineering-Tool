package com.example.agile_re_tool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class UC03CreateUserStory extends Application {

    private final UserStoryDao dao = new UserStoryDao();

    
    private List<String> assignees = List.of();
    private List<String> estimateTypes = List.of();
    private List<String> tshirtSizes = List.of();
    private int defaultStoryPoints = 3;
    private String defaultPriority = "Medium";
    private String defaultStatus = "To Do";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UC-03 - Create User Story");

        
        loadConfig();

        
        TextField titleField = new TextField();
        titleField.setPromptText("Enter task title");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter task description");
        descriptionArea.setPrefRowCount(3);

        TextArea acceptanceArea = new TextArea();
        acceptanceArea.setPromptText("Enter acceptance criteria (e.g., Given... When... Then...)");
        acceptanceArea.setPrefRowCount(2);

        
        ComboBox<String> assigneeCombo = new ComboBox<>();
        assigneeCombo.getItems().setAll(assignees);
        assigneeCombo.setPromptText("Select assignee"); 
        
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
        assigneeCombo.setValue(null);

        ComboBox<String> estimateTypeCombo = new ComboBox<>();
        estimateTypeCombo.getItems().setAll(estimateTypes);
        if (!estimateTypes.isEmpty()) estimateTypeCombo.getSelectionModel().select(0);

        
        Spinner<Integer> pointsSpinner = new Spinner<>(0, 100, defaultStoryPoints);
        pointsSpinner.setEditable(true);

        ComboBox<String> tshirtCombo = new ComboBox<>();
        tshirtCombo.getItems().setAll(tshirtSizes);

        TextField timeEstimateField = new TextField();
        timeEstimateField.setPromptText("e.g., 4 hours, 2 days");

        
        HBox estimateControlsBox = new HBox(8);
        estimateControlsBox.setAlignment(Pos.CENTER_LEFT);
        updateEstimateControls(estimateControlsBox, estimateTypeCombo.getValue(), pointsSpinner, tshirtCombo, timeEstimateField);

        estimateTypeCombo.setOnAction(e -> {
            String sel = estimateTypeCombo.getValue();
            updateEstimateControls(estimateControlsBox, sel, pointsSpinner, tshirtCombo, timeEstimateField);
        });

        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.getSelectionModel().select(defaultPriority);

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("To Do", "In Progress", "Done");
        statusCombo.getSelectionModel().select(defaultStatus);

        
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));

        
        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPrefWidth(150);
        c0.setHalignment(HPos.LEFT);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        c1.setFillWidth(true);

        grid.getColumnConstraints().addAll(c0, c1);

        
        titleField.setMaxWidth(Double.MAX_VALUE);
        descriptionArea.setMaxWidth(Double.MAX_VALUE);
        acceptanceArea.setMaxWidth(Double.MAX_VALUE);
        assigneeCombo.setMaxWidth(Double.MAX_VALUE);
        estimateTypeCombo.setMaxWidth(Double.MAX_VALUE);
        tshirtCombo.setMaxWidth(Double.MAX_VALUE);
        timeEstimateField.setMaxWidth(Double.MAX_VALUE);
        pointsSpinner.setMaxWidth(Double.MAX_VALUE);
        priorityCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.setMaxWidth(Double.MAX_VALUE);

        int r = 0;
        grid.add(new Label("Task Title *"), 0, r); grid.add(titleField, 1, r++);
        grid.add(new Label("Description"), 0, r); grid.add(descriptionArea, 1, r++);
        grid.add(new Label("Acceptance Criteria"), 0, r); grid.add(acceptanceArea, 1, r++);
        grid.add(new Label("Assignee"), 0, r); grid.add(assigneeCombo, 1, r++);
        grid.add(new Label("Estimate"), 0, r);
        VBox estimateVBox = new VBox(8, estimateTypeCombo, estimateControlsBox);
        grid.add(estimateVBox, 1, r++);
        grid.add(new Label("Priority"), 0, r); grid.add(priorityCombo, 1, r++);
        grid.add(new Label("Status"), 0, r); grid.add(statusCombo, 1, r++);

        
        Button cancelBtn = new Button("Cancel");
        Button addBtn = new Button("Add Task");
        
        cancelBtn.getStyleClass().add("button-cancel");
        addBtn.getStyleClass().add("button-primary");

        
        addBtn.setDefaultButton(true);
        cancelBtn.setCancelButton(true);

        HBox buttons = new HBox(10, cancelBtn, addBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        Label heading = new Label("Add New Story");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subText = new Label("Create a new story for the selected idea. Fill in the details below.");
        subText.getStyleClass().add("subtext");

        VBox root = new VBox(12, heading, subText, grid, buttons);
        root.setPadding(new Insets(16));
        root.setPrefWidth(760);

        cancelBtn.setOnAction(e -> primaryStage.close());

        addBtn.setOnAction(e -> {
            String title = titleField.getText();
            if (title == null || title.isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Validation error", "Title is required");
                return;
            }

            CreateStoryDto dto = new CreateStoryDto();
            dto.title = title.trim();
            dto.description = emptyToNull(descriptionArea.getText());
            dto.acceptanceCriteria = emptyToNull(acceptanceArea.getText());
            dto.assignee = assigneeCombo.getValue();
            dto.estimateType = estimateTypeCombo.getValue();

            
            if ("Story Points".equals(dto.estimateType)) {
                try {
                    dto.storyPoints = pointsSpinner.getValue();
                } catch (Exception ex) {
                    dto.storyPoints = defaultStoryPoints;
                }
            } else {
                dto.storyPoints = null;
            }
            if ("T-shirt Sizes".equals(dto.estimateType)) {
                dto.size = tshirtCombo.getValue();
            } else {
                dto.size = null;
            }
            if ("Time".equals(dto.estimateType)) {
                dto.timeEstimate = emptyToNull(timeEstimateField.getText());
            } else {
                dto.timeEstimate = null;
            }

            dto.priority = priorityCombo.getValue();
            dto.status = statusCombo.getValue();

            try {
                long id = dao.create(dto);
                showAlert(Alert.AlertType.INFORMATION, "Success", "User story created (id=" + id + ")");
                
                titleField.clear();
                descriptionArea.clear();
                acceptanceArea.clear();
                
                assigneeCombo.getSelectionModel().clearSelection();
                assigneeCombo.setValue(null);
                pointsSpinner.getValueFactory().setValue(defaultStoryPoints);
                tshirtCombo.getSelectionModel().clearSelection();
                timeEstimateField.clear();
                priorityCombo.getSelectionModel().select(defaultPriority);
                statusCombo.getSelectionModel().select(defaultStatus);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "DB Error", ex.getMessage());
            }
        });

        Scene scene = new Scene(root);
        
        try {
            String css = Optional.ofNullable(getClass().getResource("/uc03-style.css"))
                    .map(u -> u.toExternalForm()).orElse(null);
            if (css != null) scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateEstimateControls(HBox container, String mode, Spinner<Integer> pointsSpinner, ComboBox<String> tshirtCombo, TextField timeEstimateField) {
        container.getChildren().clear();
        if (mode == null) return;
        switch (mode) {
            case "Story Points":
                Label spLabel = new Label("Story Points:");
                spLabel.setMinWidth(90);
                container.getChildren().addAll(spLabel, pointsSpinner);
                break;
            case "T-shirt Sizes":
                Label sizeLabel = new Label("Size:");
                sizeLabel.setMinWidth(90);
                container.getChildren().addAll(sizeLabel, tshirtCombo);
                break;
            case "Time":
                Label timeLabel = new Label("Time Estimate:");
                timeLabel.setMinWidth(90);
                container.getChildren().addAll(timeLabel, timeEstimateField);
                break;
            default:
                
                break;
        }
    }

    private void loadConfig() {
        Properties p = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/uc03-config.properties")) {
            if (in != null) {
                p.load(in);
                String a = p.getProperty("assignees");
                if (a != null && !a.isBlank()) {
                    assignees = Arrays.stream(a.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                }
                String et = p.getProperty("estimate.types");
                if (et != null && !et.isBlank()) {
                    estimateTypes = Arrays.stream(et.split(",")).map(String::trim).collect(Collectors.toList());
                } else {
                    estimateTypes = List.of("Story Points", "T-shirt Sizes", "Time");
                }
                String ts = p.getProperty("tshirt.sizes");
                if (ts != null && !ts.isBlank()) {
                    tshirtSizes = Arrays.stream(ts.split(",")).map(String::trim).collect(Collectors.toList());
                } else {
                    tshirtSizes = List.of("XS","S","M","L","XL","XXL");
                }
                String sp = p.getProperty("default.story.points");
                if (sp != null) {
                    try { defaultStoryPoints = Integer.parseInt(sp.trim()); } catch (NumberFormatException ignored) {}
                }
                defaultPriority = Optional.ofNullable(p.getProperty("default.priority")).orElse(defaultPriority);
                defaultStatus = Optional.ofNullable(p.getProperty("default.status")).orElse(defaultStatus);
            } else {
                
                System.out.println("uc03-config.properties not found on classpath; using defaults.");
            }
        } catch (IOException e) {
            System.out.println("Failed to read uc03-config.properties: " + e.getMessage());
        }
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
        UserStoryDao.initDatabase();
        launch();
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
    }
}
