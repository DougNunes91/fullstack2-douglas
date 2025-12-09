/*
*  @(#)UserRepository.java
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
package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for UserEntity.
 * Follows Interface Segregation Principle (ISP) - specific methods only.
 * Follows Dependency Inversion Principle (DIP) - depends on abstraction.
 * 
 * @author jtech-team
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Finds a user by email.
     * Used for authentication and uniqueness validation.
     * 
     * @param email the user's email
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     * 
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds an active user by email.
     * 
     * @param email the user's email
     * @return Optional containing the active user if found
     */
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.active = true")
    Optional<UserEntity> findActiveByEmail(String email);

    /**
     * Finds a user by ID only if active.
     * 
     * @param id the user ID
     * @return Optional containing the active user if found
     */
    @Query("SELECT u FROM UserEntity u WHERE u.id = :id AND u.active = true")
    Optional<UserEntity> findActiveById(UUID id);
}
