/*
*  @(#)UserEntity.java
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
package br.com.jtech.tasklist.adapters.output.repositories.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a User in the system.
 * Follows Single Responsibility Principle (SRP) - manages user data persistence only.
 * 
 * @author jtech-team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true)
})
public class UserEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * One-to-Many relationship with TaskEntity.
     * Follows Open/Closed Principle (OCP) - can be extended without modification.
     * CascadeType.ALL ensures proper lifecycle management.
     * orphanRemoval ensures tasks are deleted when removed from user.
     */
    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<TaskEntity> tasks = new ArrayList<>();

    /**
     * Helper method to maintain bidirectional relationship consistency.
     * Follows Liskov Substitution Principle (LSP) - maintains invariants.
     */
    public void addTask(TaskEntity task) {
        tasks.add(task);
        task.setUser(this);
    }

    /**
     * Helper method to maintain bidirectional relationship consistency.
     */
    public void removeTask(TaskEntity task) {
        tasks.remove(task);
        task.setUser(null);
    }

    @PrePersist
    protected void onCreate() {
        if (active == null) {
            active = true;
        }
    }
}
