package com.example.agile_re_tool.ui;

public class WorkspaceNavigator {

    private static Workspace workspace;

    public static void register(Workspace ws) {
        workspace = ws;
    }

    public static void goToDashboard(long projectId) {
        // Use the updated static method in Workspace
        Workspace.setProjectAndGoToDashboard(projectId);
    }
}
