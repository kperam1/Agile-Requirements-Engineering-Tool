package com.example.agile_re_tool.ui;

import com.example.agile_re_tool.session.ProjectSession;
import com.example.ideaboard.util.DialogHelper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Workspace extends Application {

    private static Workspace instance;

    public Workspace() {
        instance = this;
        SprintNavigator.register(this);
        WorkspaceNavigator.register(this);
    }

    private ComboBox<ProjectItem> projectSelector;
    private BorderPane root;
    private String currentViewName = "dashboard";
    private Pane currentViewPane;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Agile RE Tool - Workspace");

        root = new BorderPane();
        VBox sidebar = buildSidebar();
        HBox topBar = buildTopBar();

        currentViewPane = new DashboardView().getView();
        currentViewName = "dashboard";

        root.setCenter(currentViewPane);
        root.setLeft(sidebar);
        root.setTop(topBar);

        Scene scene = new Scene(root, 1300, 750);
        scene.getStylesheets().add(
                getClass().getResource("/styles/workspace.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
        loadProjectsIntoDropdown();
    }

    public void switchToSprintBoard(long sprintId) {
        SprintBoardView view = new SprintBoardView(sprintId);
        switchView(view.getView(), "sprint");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 15, 15, 15));
        sidebar.setPrefWidth(200);
        sidebar.getStyleClass().add("sidebar");

        Button projectsBtn = new Button("Projects");
        Button dashboardBtn = new Button("Dashboard");
        Button ideationBtn = new Button("Ideation Board");
        Button backlogBtn = new Button("Backlog");
        Button sprintBtn = new Button("Sprint Board");
        Button sprintMgmtBtn = new Button("Manage Sprints");

        // ⭐ NEW BUTTON
        Button releasePlanBtn = new Button("Release Plan");

        Button reportsBtn = new Button("Reports");
        Button settingsBtn = new Button("Team Settings");

        sidebar.getChildren().addAll(
                projectsBtn, dashboardBtn, ideationBtn, backlogBtn,
                sprintBtn, sprintMgmtBtn, releasePlanBtn, reportsBtn, settingsBtn
        );

        projectsBtn.setOnAction(e -> switchView(new ProjectsView().getView(), "projects"));
        dashboardBtn.setOnAction(e -> switchView(new DashboardView().getView(), "dashboard"));

        ideationBtn.setOnAction(e -> {
            try {
                ProjectItem selected = projectSelector.getValue();
                if (selected == null) {
                    showProjectNeeded();
                    return;
                }
                ProjectSession.setProjectId(selected.id());
                refreshCurrentView();
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/example/ideaboard/views/review_ideas.fxml")
                );
                switchView(loader.load(), "ideation");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backlogBtn.setOnAction(e -> {
            if (!ProjectSession.hasProjectSelected()) {
                showProjectNeeded();
                return;
            }
            switchView(new BacklogView().getView(), "backlog");
        });

        sprintBtn.setOnAction(e -> {
            if (!ProjectSession.hasProjectSelected()) {
                showProjectNeeded();
                return;
            }
            switchView(new SprintBoardView().getView(), "sprint");
        });

        sprintMgmtBtn.setOnAction(e -> {
            if (!ProjectSession.hasProjectSelected()) {
                showProjectNeeded();
                return;
            }
            switchView(new SprintManagementView().getView(), "sprintMgmt");
        });

        // ⭐ NEW NAVIGATION HANDLER
        releasePlanBtn.setOnAction(e -> {
            if (!ProjectSession.hasProjectSelected()) {
                showProjectNeeded();
                return;
            }
            switchView(new ReleasePlanView().getView(), "releasePlan");
        });

        reportsBtn.setOnAction(e -> switchView(new ReportsView().getView(), "reports"));
        settingsBtn.setOnAction(e -> switchView(new TeamSettingsView().getView(), "settings"));

        return sidebar;
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("top-bar");

        Label headerTitle = new Label("Agile RE Tool");
        headerTitle.getStyleClass().add("header-title");

        projectSelector = new ComboBox<>();
        projectSelector.setPromptText("Select Project");
        projectSelector.setPrefWidth(220);

        projectSelector.setOnAction(e -> {
            ProjectItem selected = projectSelector.getValue();
            if (selected == null) return;

            ProjectSession.setProjectId(selected.id());
            refreshCurrentView();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button createBtn = new Button("+ Create Idea");
        createBtn.getStyleClass().add("create-btn");

        createBtn.setOnAction(e -> {
            if (!ProjectSession.hasProjectSelected()) {
                showProjectNeeded();
                return;
            }
            try { DialogHelper.openCreateIdeaDialog(); }
            catch (Exception ex) { ex.printStackTrace(); }
        });

        topBar.getChildren().addAll(headerTitle, projectSelector, spacer, createBtn);
        return topBar;
    }

    private void switchView(Pane newView, String name) {
        currentViewPane = newView;
        currentViewName = name;
        root.setCenter(newView);
    }

    private void refreshCurrentView() {
        switch (currentViewName) {
            case "dashboard" -> switchView(new DashboardView().getView(), "dashboard");
            case "backlog" -> switchView(new BacklogView().getView(), "backlog");
            case "sprint" -> switchView(new SprintBoardView().getView(), "sprint");
            case "sprintMgmt" -> switchView(new SprintManagementView().getView(), "sprintMgmt");
            case "reports" -> switchView(new ReportsView().getView(), "reports");
            case "projects" -> switchView(new ProjectsView().getView(), "projects");
            case "ideation" -> {
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/com/example/ideaboard/views/review_ideas.fxml")
                    );
                    switchView(loader.load(), "ideation");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // ⭐ REFRESH FOR RELEASE PLAN
            case "releasePlan" -> switchView(new ReleasePlanView().getView(), "releasePlan");
        }
    }

    private void loadProjectsIntoDropdown() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/projects"))
                        .GET()
                        .build();

                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200) return;

                JSONArray arr = new JSONArray(resp.body());

                Platform.runLater(() -> {
                    projectSelector.getItems().clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        ProjectItem item = new ProjectItem(o.getLong("id"), o.getString("name"));
                        projectSelector.getItems().add(item);

                        if (ProjectSession.getProjectId() == item.id()) {
                            projectSelector.setValue(item);
                        }
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void refreshProjectsDropdown() {
        if (instance != null) {
            instance.loadProjectsIntoDropdown();
            if (instance.currentViewName.equals("projects")) {
                Platform.runLater(() ->
                        instance.switchView(new ProjectsView().getView(), "projects")
                );
            }
        }
    }

    public static void setProjectAndGoToDashboard(long projectId) {
        if (instance == null) return;

        ProjectSession.setProjectId(projectId);

        Platform.runLater(() -> {
            instance.projectSelector.getItems().forEach(item -> {
                if (item.id() == projectId)
                    instance.projectSelector.setValue(item);
            });
            instance.switchView(new DashboardView().getView(), "dashboard");
        });
    }

    private void showProjectNeeded() {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText("Please select a project first.");
        a.showAndWait();
    }

    private record ProjectItem(long id, String name) {
        public String toString() { return name; }
    }

    public static void main(String[] args) { launch(); }
}
