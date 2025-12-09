/*
*  @(#)CustomUserDetailsService.java
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
package br.com.jtech.tasklist.config.security;

import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Custom UserDetailsService implementation.
 * Follows Single Responsibility Principle (SRP) - loads user details for authentication.
 * Follows Dependency Inversion Principle (DIP) - depends on UserRepository abstraction.
 * 
 * @author jtech-team
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user by username (email).
     * Called by Spring Security during authentication.
     * 
     * @param username the username (email)
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findActiveByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with email: " + username
                ));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>()) // Can be extended with roles/authorities
                .accountExpired(false)
                .accountLocked(!user.getActive())
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }

    /**
     * Loads user by ID.
     * Useful for token-based operations.
     * 
     * @param userId the user ID as string
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        UserEntity user = userRepository.findActiveById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with id: " + userId
                ));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .accountExpired(false)
                .accountLocked(!user.getActive())
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }
}
