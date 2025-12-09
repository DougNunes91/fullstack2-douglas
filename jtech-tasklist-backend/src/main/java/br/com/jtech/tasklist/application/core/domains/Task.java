/*
*  @(#)Task.java
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
package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a Task.
 * Follows Single Responsibility Principle (SRP) - encapsulates task business logic.
 * 
 * @author jtech-team
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private String id;
    private String title;
    private String description;
    private String listName;
    @Builder.Default
    private Boolean completed = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;

    /**
     * Converts domain model to entity.
     * Follows Dependency Inversion Principle (DIP).
     */
    public TaskEntity toEntity() {
        TaskEntity entity = TaskEntity.builder()
            .id(id != null ? UUID.fromString(id) : null)
            .title(title)
            .description(description)
            .listName(listName)
            .completed(completed)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        if (userId != null) {
            UserEntity userEntity = UserEntity.builder()
                .id(UUID.fromString(userId))
                .build();
            entity.setUser(userEntity);
        }

        return entity;
    }

    /**
     * Creates domain model from entity.
     * Factory method pattern.
     */
    public static Task of(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        return Task.builder()
            .id(entity.getId().toString())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .listName(entity.getListName())
            .completed(entity.getCompleted())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .userId(entity.getUser() != null ? entity.getUser().getId().toString() : null)
            .build();
    }

    /**
     * Creates domain models from entities list.
     */
    public static List<Task> of(List<TaskEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
            .map(Task::of)
            .toList();
    }

    /**
     * Validates that the task belongs to a specific user.
     * Business rule validation.
     */
    public boolean belongsToUser(String userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * Toggles the completed status.
     * Business logic encapsulation.
     */
    public void toggleCompleted() {
        this.completed = !this.completed;
    }

    /**
     * Marks task as completed.
     */
    public void markAsCompleted() {
        this.completed = true;
    }

    /**
     * Marks task as not completed.
     */
    public void markAsNotCompleted() {
        this.completed = false;
    }

    /**
     * Validates if task has required fields.
     */
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() 
            && listName != null && !listName.trim().isEmpty()
            && userId != null;
    }
}
