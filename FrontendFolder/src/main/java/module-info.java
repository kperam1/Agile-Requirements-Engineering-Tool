module com.taiga.storyapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.taiga.storyapp to javafx.fxml;
    exports com.taiga.storyapp;
}
