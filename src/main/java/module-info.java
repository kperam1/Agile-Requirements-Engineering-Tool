module com.example.agile_re_tool {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.context;
    requires spring.beans;
    requires java.net.http;
    requires java.sql;

    opens com.example.agile_re_tool to javafx.fxml, spring.core, spring.beans, spring.context;
    opens com.example.agile_re_tool.controller to spring.web, spring.core, spring.beans, spring.context;
    opens com.example.agile_re_tool.ui to javafx.graphics, javafx.fxml;
    opens com.example.ideaboard.controllers to javafx.fxml, javafx.graphics;
    opens com.example.ideaboard.util to javafx.fxml;

    exports com.example.agile_re_tool;
    exports com.example.agile_re_tool.ui;
    exports com.example.ideaboard.controllers;
}
