package com.example.agile_re_tool;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.json.JSONObject;



import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import org.json.JSONObject;


public class UC03CreateUserStory extends Application {


   private List<String> assignees = List.of();
   private List<String> estimateTypes = List.of();
   private List<String> tshirtSizes = List.of();
   private int defaultStoryPoints = 3;
   private String defaultPriority = "Medium";
   private String defaultStatus = "Backlog";


   private static final List<Integer> FIBONACCI_POINTS = List.of(1, 2, 3, 5, 8, 13, 21);


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
       acceptanceArea.setPromptText("Enter acceptance criteria");
       acceptanceArea.setPrefRowCount(2);


       ComboBox<String> assigneeCombo = new ComboBox<>();
       assigneeCombo.getItems().setAll(assignees);
       assigneeCombo.setPromptText("Select assignee");


       ComboBox<String> estimateTypeCombo = new ComboBox<>();
       estimateTypeCombo.getItems().setAll(estimateTypes);
       estimateTypeCombo.setPromptText("Estimate Type");
       if (!estimateTypes.isEmpty()) estimateTypeCombo.getSelectionModel().select(0);


       ComboBox<Integer> pointsCombo = new ComboBox<>();
       pointsCombo.getItems().setAll(FIBONACCI_POINTS);


       ComboBox<String> tshirtCombo = new ComboBox<>();
       tshirtCombo.getItems().setAll(tshirtSizes);


       TextField timeEstimateField = new TextField();
       timeEstimateField.setPromptText("e.g., 4 hours");


       HBox estimateControlsBox = new HBox(8);
       estimateControlsBox.setAlignment(Pos.CENTER_LEFT);
       updateEstimateControls(estimateControlsBox, estimateTypeCombo.getValue(), pointsCombo, tshirtCombo, timeEstimateField);


       estimateTypeCombo.setOnAction(e -> {
           updateEstimateControls(estimateControlsBox, estimateTypeCombo.getValue(), pointsCombo, tshirtCombo, timeEstimateField);
       });


       ComboBox<String> priorityCombo = new ComboBox<>();
       priorityCombo.getItems().addAll("Low", "Medium", "High");
       priorityCombo.getSelectionModel().select(defaultPriority);


       ComboBox<String> statusCombo = new ComboBox<>();
       statusCombo.getItems().addAll("Backlog", "To Do", "In Progress", "Testing", "Done");
       statusCombo.getSelectionModel().select(defaultStatus);


       ToggleButton mvpToggle = createToggle();
       HBox mvpBox = new HBox(12, new Label("MVP"), mvpToggle, new Label("Mark as MVP"));
       mvpBox.setAlignment(Pos.CENTER_LEFT);


       ToggleButton sprintToggle = createToggle();
       HBox sprintBox = new HBox(12, new Label("Sprint Ready"), sprintToggle, new Label("Ready for Sprint"));
       sprintBox.setAlignment(Pos.CENTER_LEFT);


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


       int r = 0;
       grid.add(new Label("Task Title *"), 0, r); grid.add(titleField, 1, r++);
       grid.add(new Label("Description"), 0, r); grid.add(descriptionArea, 1, r++);
       grid.add(new Label("Acceptance Criteria"), 0, r); grid.add(acceptanceArea, 1, r++);
       grid.add(new Label("Assignee"), 0, r); grid.add(assigneeCombo, 1, r++);
       grid.add(new Label("Estimate"), 0, r); grid.add(new VBox(8, estimateTypeCombo, estimateControlsBox), 1, r++);
       grid.add(new Label("Priority"), 0, r); grid.add(priorityCombo, 1, r++);
       grid.add(new Label("Status"), 0, r); grid.add(statusCombo, 1, r++);
       grid.add(new Label("MVP"), 0, r); grid.add(mvpBox, 1, r++);
       grid.add(new Label("Sprint Ready"), 0, r); grid.add(sprintBox, 1, r++);


       Button cancelBtn = new Button("Cancel");
       Button addBtn = new Button("Add Task");
       addBtn.setDefaultButton(true);


       HBox buttons = new HBox(10, cancelBtn, addBtn);
       buttons.setAlignment(Pos.CENTER_RIGHT);
       buttons.setPadding(new Insets(8, 0, 0, 0));


       Label heading = new Label("Add New Story");
       heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


       VBox root = new VBox(12, heading, grid, buttons);
       root.setPadding(new Insets(16));
       root.setPrefWidth(760);


       cancelBtn.setOnAction(e -> primaryStage.close());


       addBtn.setOnAction(e -> {
           if (titleField.getText().isBlank()) {
               showAlert(Alert.AlertType.ERROR, "Validation error", "Title is required");
               return;
           }


           JSONObject json = new JSONObject();
           json.put("title", titleField.getText());
           json.put("description", descriptionArea.getText());
           json.put("acceptanceCriteria", acceptanceArea.getText());
           json.put("assignedTo", assigneeCombo.getValue());
           json.put("priority", priorityCombo.getValue());
           json.put("status", statusCombo.getValue());
           json.put("mvp", mvpToggle.isSelected());
           json.put("sprintReady", sprintToggle.isSelected());


           String estType = estimateTypeCombo.getValue();
           int storyPoints = defaultStoryPoints;


           if ("Story Points".equals(estType) && pointsCombo.getValue() != null)
               storyPoints = pointsCombo.getValue();
           else if ("T-shirt Sizes".equals(estType) && tshirtCombo.getValue() != null) {
               switch (tshirtCombo.getValue()) {
                   case "XS" -> storyPoints = 1;
                   case "S" -> storyPoints = 2;
                   case "M" -> storyPoints = 3;
                   case "L" -> storyPoints = 5;
                   case "XL" -> storyPoints = 8;
               }
           }


           json.put("storyPoints", storyPoints);


           new Thread(() -> {
               try {
                   HttpClient client = HttpClient.newHttpClient();
                   HttpRequest request = HttpRequest.newBuilder()
                           .uri(URI.create("http://localhost:8080/api/userstories"))
                           .header("Content-Type", "application/json")
                           .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                           .build();


                   HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                   if (response.statusCode() >= 200 && response.statusCode() < 300) {
                       showAlert(Alert.AlertType.INFORMATION, "Success", "User story created.");
                       Platform.runLater(primaryStage::close);
                   } else {
                       showAlert(Alert.AlertType.ERROR, "Error", "Server error (" + response.statusCode() + ")");
                   }
               } catch (Exception ex) {
                   ex.printStackTrace();
                   showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
               }
           }).start();
       });


       Scene scene = new Scene(root);
       primaryStage.setScene(scene);
       primaryStage.show();
   }


   private ToggleButton createToggle() {
       ToggleButton t = new ToggleButton();
       t.setPrefWidth(50);
       t.setPrefHeight(24);
       t.setStyle("-fx-background-color: #d1d5db; -fx-background-radius: 20; -fx-padding: 0;");
       Pane knob = new Pane();
       knob.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
       knob.setPrefSize(22, 22);
       knob.setTranslateX(2);


       StackPane container = new StackPane(knob);
       container.setPrefSize(50, 24);
       t.setGraphic(container);


       t.selectedProperty().addListener((obs, oldV, newV) -> {
           if (newV) {
               t.setStyle("-fx-background-color: #4ade80; -fx-background-radius: 20;");
               knob.setTranslateX(26);
           } else {
               t.setStyle("-fx-background-color: #d1d5db; -fx-background-radius: 20;");
               knob.setTranslateX(2);
           }
       });


       return t;
   }


   private void updateEstimateControls(HBox container, String mode,
                                       ComboBox<Integer> pointsCombo,
                                       ComboBox<String> tshirtCombo,
                                       TextField timeEstimateField) {
       container.getChildren().clear();
       if (mode == null) return;
       switch (mode) {
           case "Story Points" -> container.getChildren().addAll(new Label("Story Points:"), pointsCombo);
           case "T-shirt Sizes" -> container.getChildren().addAll(new Label("Size:"), tshirtCombo);
           case "Time" -> container.getChildren().addAll(new Label("Time:"), timeEstimateField);
       }
   }


   private void loadConfig() {
       Properties p = new Properties();
       try (InputStream in = getClass().getResourceAsStream("/uc-config.properties")) {
           if (in != null) p.load(in);
       } catch (IOException ignored) {}


       String a = p.getProperty("assignees");
       if (a != null && !a.isBlank()) {
           assignees = Arrays.stream(a.split(",")).map(String::trim).toList();
       }
       if (assignees.isEmpty()) assignees = List.of("Alice", "Bob", "Charlie");


       String et = p.getProperty("estimate.types");
       estimateTypes = (et != null && !et.isBlank())
               ? Arrays.stream(et.split(",")).map(String::trim).toList()
               : List.of("Story Points", "T-shirt Sizes", "Time");


       String ts = p.getProperty("tshirt.sizes");
       tshirtSizes = (ts != null && !ts.isBlank())
               ? Arrays.stream(ts.split(",")).map(String::trim).toList()
               : List.of("XS", "S", "M", "L", "XL");
   }


   private void showAlert(Alert.AlertType t, String title, String body) {
       Platform.runLater(() -> {
           Alert a = new Alert(t, body, ButtonType.OK);
           a.setTitle(title);
           a.setHeaderText(null);
           a.showAndWait();
       });
   }


   public void openWindow() {
       Stage stage = new Stage();
       stage.setTitle("UC-03 - Create User Story");
       try { start(stage); } catch (Exception e) { e.printStackTrace(); }
   }


   public static void main(String[] args) {
       launch();
   }
}


