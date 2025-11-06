package com.agile.requirements.api;

import com.agile.requirements.dto.AuthResponse;
import com.agile.requirements.dto.LoginRequest;
import com.agile.requirements.dto.SignupRequest;
import com.agile.requirements.dto.UserDto;
import com.agile.requirements.model.User;
import com.agile.requirements.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST API Controller for Authentication
 * Handles login and signup endpoints
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/auth/signup
     * Register a new user
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        try {
            // Validate request
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(false, "Username is required", null));
            }

            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(false, "Password must be at least 6 characters", null));
            }

            if (request.getEmail() == null || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(false, "Invalid email address", null));
            }

            // Create user
            User newUser = new User();
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setEmail(request.getEmail().toLowerCase());
            newUser.setUsername(request.getUsername());
            newUser.setPassword(request.getPassword());
            newUser.setRole(request.getRole());

            // Save user
            User savedUser = userService.registerUser(newUser);

            // Convert to DTO
            UserDto userDto = convertToDto(savedUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(true, "Account created successfully", userDto));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(false, "An error occurred during signup", null));
        }
    }

    /**
     * POST /api/auth/login
     * Authenticate a user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            // Validate request
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(false, "Username is required", null));
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(false, "Password is required", null));
            }

            // Authenticate
            Optional<User> authenticatedUser = userService.authenticate(
                    request.getUsername(), 
                    request.getPassword()
            );

            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();

                if (!user.isActive()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new AuthResponse(false, "Account is deactivated", null));
                }

                // Convert to DTO
                UserDto userDto = convertToDto(user);

                return ResponseEntity.ok()
                        .body(new AuthResponse(true, "Login successful", userDto));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(false, "Invalid username or password", null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(false, "An error occurred during login", null));
        }
    }

    /**
     * GET /api/auth/check-username/{username}
     * Check if username is available
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<AuthResponse> checkUsername(@PathVariable String username) {
        boolean exists = userService.findByUsername(username).isPresent();
        if (exists) {
            return ResponseEntity.ok()
                    .body(new AuthResponse(false, "Username already taken", null));
        } else {
            return ResponseEntity.ok()
                    .body(new AuthResponse(true, "Username available", null));
        }
    }

    /**
     * GET /api/auth/check-email/{email}
     * Check if email is available
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<AuthResponse> checkEmail(@PathVariable String email) {
        boolean exists = userService.findByEmail(email).isPresent();
        if (exists) {
            return ResponseEntity.ok()
                    .body(new AuthResponse(false, "Email already registered", null));
        } else {
            return ResponseEntity.ok()
                    .body(new AuthResponse(true, "Email available", null));
        }
    }

    /**
     * Convert User entity to UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
