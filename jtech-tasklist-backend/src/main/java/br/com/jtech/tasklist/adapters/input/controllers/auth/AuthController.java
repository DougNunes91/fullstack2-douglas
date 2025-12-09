/*
*  @(#)AuthController.java
*
*  Copyright (c) J-Tech Solucoes em Informatica.
*  All Rights Reserved.
*
*  This software is the confidential and proprietary information of J-Tech.
*  ("Confidential Information"). You shall not disclose such Confidential
*  Information and shall use it only in accordance with the terms of the
*  license agreement you entered into with J-Tech.
*
*/
package br.com.jtech.tasklist.adapters.input.controllers.auth;

import br.com.jtech.tasklist.adapters.input.protocols.auth.AuthResponse;
import br.com.jtech.tasklist.adapters.input.protocols.auth.LoginRequest;
import br.com.jtech.tasklist.adapters.input.protocols.auth.RefreshTokenRequest;
import br.com.jtech.tasklist.adapters.input.protocols.auth.RegisterRequest;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.core.usecases.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller.
 * Handles user registration, login, and token refresh.
 * Follows Single Responsibility Principle (SRP) - handles HTTP concerns only.
 * 
 * @author jtech-team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     * 
     * @param request the registration request
     * @return authentication response with tokens
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication tokens")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user.
     * 
     * @param request the login request
     * @return authentication response with tokens
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns access and refresh tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refreshes access token.
     * 
     * @param request the refresh token request
     * @return new authentication response with tokens
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Received token refresh request");
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Gets current authenticated user.
     * 
     * @return current user information
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    public ResponseEntity<User> getCurrentUser() {
        log.info("Received request for current user information");
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
