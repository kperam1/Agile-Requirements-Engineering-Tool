package com.example.agile_re_tool.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public final class UCConfig {
    private static final String RESOURCE = "/uc-config.properties";
    private static final Properties props = new Properties();
    private static boolean loaded = false;

    private UCConfig() {}

    private static synchronized void load() {
        if (loaded) return;
        try (InputStream in = UCConfig.class.getResourceAsStream(RESOURCE)) {
            if (in != null) {
                props.load(in);
            } else {
                System.out.println("Warning: " + RESOURCE + " not found on classpath; using sensible defaults.");
            }
        } catch (IOException e) {
            System.out.println("Failed to read " + RESOURCE + ": " + e.getMessage());
        }
        loaded = true;
    }

    public static List<String> getAssignees() {
        load();
        String a = props.getProperty("assignees");
        if (a == null || a.isBlank()) return List.of();
        return Arrays.stream(a.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public static List<String> getEstimateTypes() {
        load();
        String v = props.getProperty("estimate.types");
        if (v == null || v.isBlank()) return List.of("Story Points", "T-shirt Sizes", "Time");
        return Arrays.stream(v.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    public static List<String> getTshirtSizes() {
        load();
        String v = props.getProperty("tshirt.sizes");
        if (v == null || v.isBlank()) return List.of("XS","S","M","L","XL","XXL");
        return Arrays.stream(v.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    public static int getDefaultStoryPoints() {
        load();
        String v = props.getProperty("default.story.points");
        if (v == null || v.isBlank()) return 3;
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException ex) { return 3; }
    }

    public static String getDefaultPriority() {
        load();
        return props.getProperty("default.priority", "Medium");
    }

    public static String getDefaultStatus() {
        load();
        return props.getProperty("default.status", "To Do");
    }
}

