package com.example.agile_re_tool.ui;

public class SprintNavigator {

    private static Workspace workspace;

    public static void register(Workspace ws) {
        workspace = ws;
    }

    public static void goToSprintBoard(long sprintId) {
        if (workspace != null) {
            workspace.switchToSprintBoard(sprintId);
        }
    }
}
