package com.agile.requirements.service;

import com.agile.requirements.dto.AuthResponse;
import com.agile.requirements.dto.LoginRequest;
import com.agile.requirements.dto.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * API Service
 * Handles HTTP requests to the REST API
 */
@Service
public class ApiService {

    private final String BASE_URL = "http://localhost:8080/api/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // For Java 8 date/time support
    }

    /**
     * POST /api/auth/signup
     * Send signup request to API
     */
    public AuthResponse signup(SignupRequest request) throws Exception {
        String requestBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/signup"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), AuthResponse.class);
    }

    /**
     * POST /api/auth/login
     * Send login request to API
     */
    public AuthResponse login(LoginRequest request) throws Exception {
        String requestBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), AuthResponse.class);
    }

    /**
     * GET /api/auth/check-username/{username}
     * Check if username is available
     */
    public boolean isUsernameAvailable(String username) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/check-username/" + username))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        AuthResponse authResponse = objectMapper.readValue(response.body(), AuthResponse.class);

        return authResponse.isSuccess();
    }

    /**
     * GET /api/auth/check-email/{email}
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/check-email/" + email))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        AuthResponse authResponse = objectMapper.readValue(response.body(), AuthResponse.class);

        return authResponse.isSuccess();
    }
}
