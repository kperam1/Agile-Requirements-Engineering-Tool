package com.example.ideaboard.frontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ReportServiceFX {

    private final HttpClient http = HttpClient.newHttpClient();
    private final String baseUrl;

    public ReportServiceFX() {
        this.baseUrl = System.getProperty("api.base", "http://localhost:8080/api");
    }

    public String getVelocity(int sprintId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/reports/velocity?sprintId=" + sprintId))
                .GET()
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    public String getBurndown(int sprintId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/reports/burndown?sprintId=" + sprintId))
                .GET()
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }
}
