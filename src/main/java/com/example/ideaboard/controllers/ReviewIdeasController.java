package com.example.ideaboard.controllers;

import com.example.agile_re_tool.session.ProjectSession;
import com.example.ideaboard.api.IdeaApiClient;
import com.example.ideaboard.model.IdeaDto;
import com.example.ideaboard.model.IdeaStatus;
import com.example.ideaboard.util.DialogHelper;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ReviewIdeasController {

    @FXML private TextField searchField;
    @FXML private ListView<IdeaDto> ideasListView;
    @FXML private Label totalIdeasLabel;
    @FXML private Label activeIdeasLabel;
    @FXML private VBox emptyState;
    @FXML private VBox detailsCard;
    @FXML private Text ideaTitleText;
    @FXML private Label ideaCategoryLabel;
    @FXML private Label ideaStatusBadge;
    @FXML private Label primaryActorLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label updatedAtLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;

    private final IdeaApiClient apiClient = new IdeaApiClient();
    private List<IdeaDto> allIdeas = new ArrayList<>();
    private IdeaDto selectedIdea;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    @FXML
    public void initialize() {
        System.out.println("ReviewIdeasController.initialize projectId = " + ProjectSession.getProjectId());
        setupListView();
        setupSearchFilter();
        loadIdeas();
        DialogHelper.setOnIdeaCreatedCallback(this::refreshIdeas);
    }

    private void setupListView() {
        ideasListView.setFixedCellSize(110);
        ideasListView.setCellFactory(lv -> new IdeaListCell());
        ideasListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) selectIdea(newVal);
                });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterIdeas(newVal));
    }

    private void loadIdeas() {
        long projectId = ProjectSession.getProjectId();

        if (projectId <= 0) {
            System.out.println("⚠️ No project selected — skipping idea load");

            ideasListView.getItems().clear();
            emptyState.setVisible(true);
            detailsCard.setVisible(false);
            totalIdeasLabel.setText("0 Ideas");
            activeIdeasLabel.setText("0 Active");

            return;
        }

        try {
            allIdeas = apiClient.listByProject(projectId);

            ideasListView.getItems().setAll(allIdeas);
            updateStats();

            if (!allIdeas.isEmpty()) {
                ideasListView.getSelectionModel().selectFirst();
                emptyState.setVisible(false);
                detailsCard.setVisible(true);
            } else {
                emptyState.setVisible(true);
                detailsCard.setVisible(false);
            }

        } catch (Exception e) {
            System.err.println("Error loading ideas: " + e.getMessage());
            e.printStackTrace();
            ideasListView.getItems().clear();
            totalIdeasLabel.setText("0 Ideas");
            activeIdeasLabel.setText("0 Active");
            emptyState.setVisible(true);
            detailsCard.setVisible(false);
        }
    }

    public void refreshIdeas() {
        loadIdeas();
    }

    private void filterIdeas(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            ideasListView.getItems().setAll(allIdeas);
        } else {
            String lower = searchText.toLowerCase();
            List<IdeaDto> filtered = allIdeas.stream()
                    .filter(idea ->
                            (idea.getTitle() != null && idea.getTitle().toLowerCase().contains(lower)) ||
                            (idea.getCategory() != null && idea.getCategory().toLowerCase().contains(lower)) ||
                            (idea.getDescription() != null && idea.getDescription().toLowerCase().contains(lower)) ||
                            (idea.getPrimaryActor() != null && idea.getPrimaryActor().toLowerCase().contains(lower))
                    )
                    .collect(Collectors.toList());

            ideasListView.getItems().setAll(filtered);
        }
        updateStats();
    }

    private void updateStats() {
        int total = ideasListView.getItems().size();
        long active = ideasListView.getItems().stream()
                .filter(idea ->
                        idea.getStatus() == IdeaStatus.NEW ||
                        idea.getStatus() == IdeaStatus.UNDER_REVIEW)
                .count();

        totalIdeasLabel.setText(total + (total == 1 ? " Idea" : " Ideas"));
        activeIdeasLabel.setText(active + " Active");
    }

    private void selectIdea(IdeaDto idea) {
        selectedIdea = idea;
        emptyState.setVisible(false);
        detailsCard.setVisible(true);

        ideaTitleText.setText(idea.getTitle());
        ideaCategoryLabel.setText(idea.getCategory());
        primaryActorLabel.setText(idea.getPrimaryActor());
        descriptionArea.setText(idea.getDescription());

        if (idea.getCreatedAt() != null)
            createdAtLabel.setText(idea.getCreatedAt().format(dateFormatter));
        else
            createdAtLabel.setText("-");

        if (idea.getUpdatedAt() != null)
            updatedAtLabel.setText(idea.getUpdatedAt().format(dateFormatter));
        else
            updatedAtLabel.setText("-");

        updateStatusBadge(idea.getStatus());

        approveButton.setDisable(idea.getStatus() == IdeaStatus.APPROVED);
        rejectButton.setDisable(idea.getStatus() == IdeaStatus.REJECTED);

        FadeTransition fade = new FadeTransition(Duration.millis(200), detailsCard);
        fade.setFromValue(0.7);
        fade.setToValue(1.0);
        fade.play();
    }

    private void updateStatusBadge(IdeaStatus status) {
        ideaStatusBadge.getStyleClass().removeIf(s -> s.startsWith("badge-"));
        switch (status) {
            case NEW -> {
                ideaStatusBadge.setText("New");
                ideaStatusBadge.getStyleClass().add("badge-new");
            }
            case UNDER_REVIEW -> {
                ideaStatusBadge.setText("Under Review");
                ideaStatusBadge.getStyleClass().add("badge-under-review");
            }
            case APPROVED -> {
                ideaStatusBadge.setText("Approved");
                ideaStatusBadge.getStyleClass().add("badge-approved");
            }
            case REJECTED -> {
                ideaStatusBadge.setText("Rejected");
                ideaStatusBadge.getStyleClass().add("badge-rejected");
            }
        }
    }

    @FXML
    private void handleApprove() {
        if (selectedIdea == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Idea");
        confirm.setHeaderText("Approve \"" + selectedIdea.getTitle() + "\"?");
        confirm.setContentText("This will mark the idea as approved.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    IdeaDto updated = apiClient.approve(selectedIdea.getId());
                    updateIdeaInList(updated);
                    updateStatusBadge(updated.getStatus());
                    approveButton.setDisable(true);
                    rejectButton.setDisable(false);
                    showToast("Idea Approved Successfully", false);
                } catch (Exception e) {
                    showToast("Failed to approve: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleReject() {
        if (selectedIdea == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reject Idea");
        confirm.setHeaderText("Reject \"" + selectedIdea.getTitle() + "\"?");
        confirm.setContentText("This will mark the idea as rejected.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    IdeaDto updated = apiClient.reject(selectedIdea.getId());
                    updateIdeaInList(updated);
                    updateStatusBadge(updated.getStatus());
                    approveButton.setDisable(false);
                    rejectButton.setDisable(true);
                    showToast("Idea Rejected", false);
                } catch (Exception e) {
                    showToast("Failed to reject: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateIdeaInList(IdeaDto updatedIdea) {
        for (int i = 0; i < allIdeas.size(); i++) {
            if (allIdeas.get(i).getId() == updatedIdea.getId()) {
                allIdeas.set(i, updatedIdea);
                break;
            }
        }
        for (int i = 0; i < ideasListView.getItems().size(); i++) {
            if (ideasListView.getItems().get(i).getId() == updatedIdea.getId()) {
                ideasListView.getItems().set(i, updatedIdea);
                break;
            }
        }
        ideasListView.refresh();
        updateStats();
    }

private void showToast(String message, boolean isError) {
    Pane root = (Pane) detailsCard.getScene().getRoot();

    HBox toast = new HBox();
    toast.setAlignment(Pos.CENTER);
    toast.getStyleClass().add("toast-notification");
    toast.getStyleClass().add(isError ? "toast-error" : "toast-success");

    Text text = new Text(message);
    text.getStyleClass().add("toast-text");
    toast.getChildren().add(text);

    // Wrap toast in a StackPane overlay so it can appear at bottom center
    StackPane overlay = new StackPane(toast);
    overlay.setPickOnBounds(false);

    StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
    StackPane.setMargin(toast, new Insets(0, 0, 40, 0));

    // Add overlay to root
    if (root instanceof BorderPane bp) {
        bp.getChildren().add(overlay);
    } else if (root instanceof StackPane sp) {
        sp.getChildren().add(overlay);
    } else if (root instanceof Pane p) {
        p.getChildren().add(overlay);
    }

    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    fadeIn.play();

    PauseTransition pause = new PauseTransition(Duration.seconds(3));
    pause.setOnFinished(e -> {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(evt -> {
            if (root instanceof BorderPane bp2) {
                bp2.getChildren().remove(overlay);
            } else if (root instanceof StackPane sp2) {
                sp2.getChildren().remove(overlay);
            } else if (root instanceof Pane p2) {
                p2.getChildren().remove(overlay);
            }
        });
        fadeOut.play();
    });
    pause.play();
}


    private class IdeaListCell extends ListCell<IdeaDto> {
        private final HBox rowContainer = new HBox(12);
        private final VBox contentBox = new VBox(5);
        private final Label titleLabel = new Label();
        private final Label categoryLabel = new Label();
        private final Label snippetLabel = new Label();
        private final Region spacer = new Region();
        private final Label statusBadge = new Label();

        public IdeaListCell() {
            rowContainer.getStyleClass().add("idea-row");
            rowContainer.setAlignment(Pos.TOP_LEFT);

            titleLabel.getStyleClass().add("row-title");
            categoryLabel.getStyleClass().add("row-category");
            snippetLabel.getStyleClass().add("row-snippet");
            statusBadge.getStyleClass().add("badge");

            contentBox.getChildren().addAll(titleLabel, categoryLabel, snippetLabel);
            HBox.setHgrow(spacer, Priority.ALWAYS);
            rowContainer.getChildren().addAll(contentBox, spacer, statusBadge);
        }

        @Override
        protected void updateItem(IdeaDto idea, boolean empty) {
            super.updateItem(idea, empty);
            if (empty || idea == null) {
                setGraphic(null);
            } else {
                titleLabel.setText(idea.getTitle());
                categoryLabel.setText(idea.getCategory());

                String desc = idea.getDescription();
                if (desc != null && desc.length() > 100) {
                    desc = desc.substring(0, 100) + "...";
                }

                snippetLabel.setText(desc);

                statusBadge.getStyleClass().removeIf(s -> s.startsWith("badge-"));
                statusBadge.getStyleClass().add("badge");

                switch (idea.getStatus()) {
                    case NEW -> {
                        statusBadge.setText("New");
                        statusBadge.getStyleClass().add("badge-new");
                    }
                    case UNDER_REVIEW -> {
                        statusBadge.setText("Review");
                        statusBadge.getStyleClass().add("badge-review");
                    }
                    case APPROVED -> {
                        statusBadge.setText("Approved");
                        statusBadge.getStyleClass().add("badge-approved");
                    }
                    case REJECTED -> {
                        statusBadge.setText("Rejected");
                        statusBadge.getStyleClass().add("badge-rejected");
                    }
                }

                setGraphic(rowContainer);
            }
        }
    }
}
