package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.model.User;
import com.example.ideaboard.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("/login")
    public Optional<User> login(@RequestParam String username, @RequestParam String password) {
        return userService.validate(username, password);
    }
}
