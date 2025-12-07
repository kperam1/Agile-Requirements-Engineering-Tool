package com.example.agile_re_tool.session;

public class ProjectSession {
    private static long projectId = -1;

    public static void setProjectId(long id) { projectId = id; }

    public static long getProjectId() { return projectId; }

    public static boolean hasProjectSelected() { return projectId > 0; }
}
