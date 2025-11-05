import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/ideaboard/views/create_idea.fxml"));
        Scene scene = new Scene(root, 640, 620);
        scene.getStylesheets().add(getClass().getResource("/com/example/ideaboard/styles/app.css").toExternalForm());
        stage.setTitle("Create New Idea");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
