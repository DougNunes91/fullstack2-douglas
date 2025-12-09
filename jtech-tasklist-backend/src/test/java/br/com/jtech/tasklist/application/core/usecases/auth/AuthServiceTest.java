/*
*  @(#)AuthServiceTest.java
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
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import br.com.jtech.tasklist.config.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 * Tests authentication business logic following best practices.
 * 
 * @author jtech-team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("$2a$12$encodedPassword")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(tokenProvider.generateTokenFromUsername(anyString())).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(anyString())).thenReturn("refresh-token");

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
        verify(tokenProvider).generateTokenFromUsername("test@example.com");
        verify(tokenProvider).generateRefreshToken("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findActiveByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(anyString())).thenReturn("refresh-token");

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findActiveByEmail("test@example.com");
        verify(tokenProvider).generateToken(authentication);
        verify(tokenProvider).generateRefreshToken("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found during login")
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findActiveByEmail(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findActiveByEmail("test@example.com");
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.isRefreshToken(anyString())).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(anyString())).thenReturn("test@example.com");
        when(userRepository.findActiveByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(tokenProvider.generateTokenFromUsername(anyString())).thenReturn("new-access-token");
        when(tokenProvider.generateRefreshToken(anyString())).thenReturn("new-refresh-token");

        // When
        AuthResponse response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

        verify(tokenProvider).validateToken(refreshToken);
        verify(tokenProvider).isRefreshToken(refreshToken);
        verify(tokenProvider).getUsernameFromToken(refreshToken);
        verify(userRepository).findActiveByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception for invalid refresh token")
    void shouldThrowExceptionForInvalidRefreshToken() {
        // Given
        String invalidToken = "invalid-token";
        when(tokenProvider.validateToken(anyString())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authService.refreshToken(invalidToken))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid refresh token");

        verify(tokenProvider).validateToken(invalidToken);
        verify(userRepository, never()).findActiveByEmail(anyString());
    }
}
