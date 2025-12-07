package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.User;
import com.example.ideaboard.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. Update user role
    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateRole(
            @PathVariable Long id,
            @RequestBody String newRole) {

        return userRepository.findById(id)
                .map(user -> {
                    user.setRole(newRole.replace("\"", ""));  // clean quotes
                    userRepository.save(user);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Delete user
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    return userRepository.findById(id)
            .map(user -> {
                userRepository.delete(user);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().<Void>build());
}

}
