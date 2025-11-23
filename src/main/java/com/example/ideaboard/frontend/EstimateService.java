package com.example.ideaboard.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EstimateService {
    private final HttpClient http = HttpClient.newHttpClient();
    private final String baseUrl;

    public EstimateService() {
        this.baseUrl = System.getProperty("api.base", "http://localhost:8080/api");
    }

    public String addEstimate(long storyId, int points, String by) throws IOException, InterruptedException {
        String json = String.format("{\"points\":%d,\"estimatedBy\":\"%s\"}", points, escape(by));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/userstories/" + storyId + "/estimate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

    public String getLatest(long storyId) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/userstories/" + storyId + "/estimate"))
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

    public String getHistory(long storyId) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/userstories/" + storyId + "/estimates"))
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
