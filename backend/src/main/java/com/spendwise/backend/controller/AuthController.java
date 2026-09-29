package com.spendwise.backend.controller;

import com.spendwise.backend.dto.AuthRequest;
import com.spendwise.backend.model.User;
import com.spendwise.backend.service.AuthService;
import com.spendwise.backend.dto.UserResponse;
import com.spendwise.backend.dto.LoginRequest;
import com.spendwise.backend.dto.LoginResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return authService.getCurrentUser(authentication.getName());
    }
}