package com.taiga.storyapp;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AcceptanceCriteriaController {

    @FXML private VBox criteriaContainer;
    @FXML private Button saveBtn;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Start with one empty row
        addRow(null, null, null);
        validate();
    }

    @FXML
    private void onAddRow() {
        addRow(null, null, null);
        validate();
    }

    @FXML
    private void onClearAll() {
        criteriaContainer.getChildren().clear();
        addRow(null, null, null);
        errorLabel.setText("");
        validate();
    }

    @FXML
    private void onCopyAll() {
        String text = getCriteriaAsText();
        if (!text.isBlank()) {
            final ClipboardContent content = new ClipboardContent();
            content.putString(text);
            Clipboard.getSystemClipboard().setContent(content);
            errorLabel.setText("Copied!");
        } else {
            errorLabel.setText("Nothing to copy.");
        }
    }

    @FXML
    private void onSave() {
        // For now we just show the compiled text; later we’ll POST to backend.
        String compiled = getCriteriaAsText();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Acceptance Criteria");
        a.setHeaderText("Preview");
        a.setContentText(compiled.isBlank() ? "(No criteria)" : compiled);
        a.showAndWait();
    }

    // ------ Helpers ------

    private void addRow(String given, String when, String then) {
        TextField givenTf = new TextField();
        givenTf.setPromptText("Given ...");
        TextField whenTf = new TextField();
        whenTf.setPromptText("When ...");
        TextField thenTf = new TextField();
        thenTf.setPromptText("Then ...");

        givenTf.textProperty().addListener((obs, o, n) -> validate());
        whenTf.textProperty().addListener((obs, o, n) -> validate());
        thenTf.textProperty().addListener((obs, o, n) -> validate());

        if (given != null) givenTf.setText(given);
        if (when != null) whenTf.setText(when);
        if (then != null) thenTf.setText(then);

        Button removeBtn = new Button("✕");
        removeBtn.setOnAction(e -> {
            criteriaContainer.getChildren().removeIf(node -> node == removeBtn.getParent());
            if (criteriaContainer.getChildren().isEmpty()) addRow(null, null, null);
            validate();
        });

        HBox row = new HBox(8, givenTf, whenTf, thenTf, removeBtn);
        row.setPadding(new Insets(4, 0, 4, 0));

        // Make textfields expand
        HBox.setHgrow(givenTf, Priority.ALWAYS);
        HBox.setHgrow(whenTf, Priority.ALWAYS);
        HBox.setHgrow(thenTf, Priority.ALWAYS);

        criteriaContainer.getChildren().add(row);
    }

    private void validate() {
        List<Row> rows = getRows();
        boolean anyInvalid = rows.stream().anyMatch(r ->
                r.given.isBlank() || r.when.isBlank() || r.thenPart.isBlank());
        int nonEmpty = (int) rows.stream().filter(r -> !r.isAllBlank()).count();

        if (nonEmpty == 0) {
            errorLabel.setText("Add at least one criterion.");
            saveBtn.setDisable(true);
        } else if (anyInvalid) {
            errorLabel.setText("Complete Given, When, Then for each non-empty row.");
            saveBtn.setDisable(true);
        } else {
            errorLabel.setText("");
            saveBtn.setDisable(false);
        }
    }

    private List<Row> getRows() {
        List<Row> rows = new ArrayList<>();
        for (var node : criteriaContainer.getChildren()) {
            if (node instanceof HBox h) {
                TextField given = (TextField) h.getChildren().get(0);
                TextField when = (TextField) h.getChildren().get(1);
                TextField thenTf = (TextField) h.getChildren().get(2);
                rows.add(new Row(given.getText().trim(), when.getText().trim(), thenTf.getText().trim()));
            }
        }
        return rows;
    }

    private String getCriteriaAsText() {
        return getRows().stream()
                .filter(r -> !r.isAllBlank())
                .map(r -> String.format("• Given %s, When %s, Then %s", r.given, r.when, r.thenPart))
                .collect(Collectors.joining("\n"));
    }

    private record Row(String given, String when, String thenPart) {
        boolean isAllBlank() {
            return given.isBlank() && when.isBlank() && thenPart.isBlank();
        }
    }
}
