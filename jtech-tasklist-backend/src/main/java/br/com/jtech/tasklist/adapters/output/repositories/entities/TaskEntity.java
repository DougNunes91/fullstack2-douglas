/*
*  @(#)TaskEntity.java
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Task in the system.
 * Follows Single Responsibility Principle (SRP) - manages task data persistence only.
 * 
 * @author jtech-team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_user", columnList = "user_id"),
    @Index(name = "idx_task_list_name", columnList = "list_name"),
    @Index(name = "idx_task_completed", columnList = "completed")
})
public class TaskEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "List name is mandatory")
    @Size(min = 1, max = 100, message = "List name must be between 1 and 100 characters")
    @Column(name = "list_name", nullable = false, length = 100)
    private String listName;

    @NotNull(message = "Completed status is mandatory")
    @Column(name = "completed", nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Many-to-One relationship with UserEntity.
     * Follows Dependency Inversion Principle (DIP) - depends on abstraction (JPA annotations).
     * FetchType.LAZY for performance optimization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_user"))
    @NotNull(message = "User is mandatory")
    private UserEntity user;

    @PrePersist
    protected void onCreate() {
        if (completed == null) {
            completed = false;
        }
    }

    /**
     * Validates that the task belongs to a specific user.
     * Follows Interface Segregation Principle (ISP) - specific validation method.
     * 
     * @param userId the user ID to validate against
     * @return true if the task belongs to the user, false otherwise
     */
    public boolean belongsToUser(UUID userId) {
        return user != null && user.getId().equals(userId);
    }

    /**
     * Toggles the completed status of the task.
     * Encapsulates business logic within the entity.
     */
    public void toggleCompleted() {
        this.completed = !this.completed;
    }
}
