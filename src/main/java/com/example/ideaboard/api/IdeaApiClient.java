package com.example.ideaboard.api;

import com.example.ideaboard.model.IdeaDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class IdeaApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/ideas";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public IdeaApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // -------------------------------------------------------------------------
    // GET /api/projects/{projectId}/ideas
    // -------------------------------------------------------------------------
    public List<IdeaDto> listByProject(long projectId) throws Exception {

        String url = "http://localhost:8080/api/projects/" + projectId + "/ideas";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch ideas for project " + projectId
                    + ". Status: " + response.statusCode());
        }

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<IdeaDto>>() {}
        );
    }

    // GET all ideas (not filtered)
    public List<IdeaDto> list() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch ideas. Status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(),
                new TypeReference<List<IdeaDto>>() {});
    }

    // GET /api/ideas/{id}
    public IdeaDto get(long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            throw new RuntimeException("Idea not found with id: " + id);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch idea. Status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), IdeaDto.class);
    }

    // POST /api/projects/{projectId}/ideas
    public IdeaDto create(long projectId, IdeaDto idea) throws Exception {

        String url = "http://localhost:8080/api/projects/" + projectId + "/ideas";

        String json = objectMapper.writeValueAsString(idea);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException(
                    "Failed to create idea. Server returned: " + response.statusCode()
            );
        }

        return objectMapper.readValue(response.body(), IdeaDto.class);
    }

    // PATCH /api/ideas/{id}/approve
    public IdeaDto approve(long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/approve"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            throw new RuntimeException("Idea not found with id: " + id);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to approve idea. Status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), IdeaDto.class);
    }

    // PATCH /api/ideas/{id}/reject
    public IdeaDto reject(long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/reject"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            throw new RuntimeException("Idea not found with id: " + id);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to reject idea. Status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), IdeaDto.class);
    }
}
