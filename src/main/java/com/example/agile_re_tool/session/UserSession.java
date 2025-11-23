package com.example.agile_re_tool.session;

public final class UserSession {
    private static volatile String currentUsername;

    private UserSession() {}

    public static void setCurrentUser(String username) {
        currentUsername = username;
    }

    public static String getCurrentUser() {
        return currentUsername;
    }
}
