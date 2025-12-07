package com.example.agile_re_tool.ui;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;

public class ReportsView {

    public BorderPane getView() {
        BorderPane pane = new BorderPane();
        pane.setCenter(new Label("Reports View"));
        return pane;
    }
}
