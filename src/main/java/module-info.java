module com.example.ideaboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens com.example.ideaboard to javafx.fxml;
    opens com.example.ideaboard.controllers to javafx.fxml;
    
    exports com.example.ideaboard;
    exports com.example.ideaboard.controllers;
    exports com.example.ideaboard.util;
}
