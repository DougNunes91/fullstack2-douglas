/*
*  @(#)AuthService.java
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
package br.com.jtech.tasklist.application.core.usecases.auth;

import br.com.jtech.tasklist.adapters.input.protocols.auth.AuthResponse;
import br.com.jtech.tasklist.adapters.input.protocols.auth.LoginRequest;
import br.com.jtech.tasklist.adapters.input.protocols.auth.RegisterRequest;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import br.com.jtech.tasklist.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service.
 * Follows Single Responsibility Principle (SRP) - handles authentication logic only.
 * Follows Dependency Inversion Principle (DIP) - depends on abstractions.
 * 
 * @author jtech-team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     * Validates email uniqueness and encrypts password.
     * Follows Open/Closed Principle (OCP) - extensible validation.
     * 
     * @param request the registration request
     * @return authentication response with tokens
     * @throws BusinessException if email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }

        // Create new user entity
        UserEntity userEntity = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .build();

        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate tokens
        String accessToken = tokenProvider.generateTokenFromUsername(savedUser.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getEmail());

        // Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(savedUser.getId().toString())
                        .name(savedUser.getName())
                        .email(savedUser.getEmail())
                        .build())
                .build();
    }

    /**
     * Authenticates a user.
     * Validates credentials and generates JWT tokens.
     * 
     * @param request the login request
     * @return authentication response with tokens
     * @throws BusinessException if authentication fails
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        UserEntity user = userRepository.findActiveByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found or inactive"));

        // Generate tokens
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        log.info("User authenticated successfully: {}", user.getEmail());

        // Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId().toString())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    /**
     * Refreshes access token using refresh token.
     * Validates refresh token and generates new access token.
     * 
     * @param refreshToken the refresh token
     * @return new authentication response with tokens
     * @throws BusinessException if refresh token is invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");

        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }

        // Extract username from token
        String email = tokenProvider.getUsernameFromToken(refreshToken);

        // Get user
        UserEntity user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found or inactive"));

        // Generate new tokens
        String newAccessToken = tokenProvider.generateTokenFromUsername(user.getEmail());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        log.info("Access token refreshed successfully for user: {}", user.getEmail());

        // Build response
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId().toString())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    /**
     * Gets current authenticated user.
     * 
     * @return current user
     * @throws BusinessException if user not authenticated
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }

        String email = authentication.getName();
        UserEntity user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return User.of(user).withoutPassword();
    }
}
