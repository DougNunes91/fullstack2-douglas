/*
*  @(#)TaskService.java
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
package br.com.jtech.tasklist.application.core.usecases.task;

import br.com.jtech.tasklist.adapters.input.protocols.task.TaskRequest;
import br.com.jtech.tasklist.adapters.output.repositories.TaskRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Task Service.
 * Follows Single Responsibility Principle (SRP) - handles task business logic only.
 * Follows Dependency Inversion Principle (DIP) - depends on abstractions.
 * 
 * @author jtech-team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new task for the authenticated user.
     * Validates ownership and prevents duplicates.
     * 
     * @param request the task creation request
     * @return created task
     */
    @Transactional
    public Task createTask(TaskRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Creating task for user: {}", userId);

        // Validate duplicate title in same list
        if (taskRepository.existsByUserIdAndListNameAndTitle(userId, request.getListName(), request.getTitle())) {
            throw new BusinessException("Task with title '" + request.getTitle() + "' already exists in list '" + request.getListName() + "'");
        }

        // Get user entity
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Create task entity
        TaskEntity taskEntity = TaskEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .listName(request.getListName())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .user(user)
                .build();

        TaskEntity savedTask = taskRepository.save(taskEntity);
        log.info("Task created successfully with ID: {}", savedTask.getId());

        return Task.of(savedTask);
    }

    /**
     * Gets all tasks for the authenticated user.
     * 
     * @return list of tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        UUID userId = getCurrentUserId();
        log.info("Fetching all tasks for user: {}", userId);

        List<TaskEntity> tasks = taskRepository.findByUserId(userId);
        return Task.of(tasks);
    }

    /**
     * Gets tasks by list name for the authenticated user.
     * 
     * @param listName the list name
     * @return list of tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByListName(String listName) {
        UUID userId = getCurrentUserId();
        log.info("Fetching tasks for user: {} in list: {}", userId, listName);

        List<TaskEntity> tasks = taskRepository.findByUserIdAndListName(userId, listName);
        return Task.of(tasks);
    }

    /**
     * Gets a specific task by ID.
     * Validates ownership.
     * 
     * @param taskId the task ID
     * @return the task
     */
    @Transactional(readOnly = true)
    public Task getTaskById(String taskId) {
        UUID userId = getCurrentUserId();
        log.info("Fetching task: {} for user: {}", taskId, userId);

        TaskEntity task = taskRepository.findByIdAndUserId(UUID.fromString(taskId), userId)
                .orElseThrow(() -> new BusinessException("Task not found or access denied"));

        return Task.of(task);
    }

    /**
     * Updates a task.
     * Validates ownership.
     * 
     * @param taskId the task ID
     * @param request the update request
     * @return updated task
     */
    @Transactional
    public Task updateTask(String taskId, TaskRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Updating task: {} for user: {}", taskId, userId);

        TaskEntity task = taskRepository.findByIdAndUserId(UUID.fromString(taskId), userId)
                .orElseThrow(() -> new BusinessException("Task not found or access denied"));

        // Check for duplicate if title or listName changed
        if (!task.getTitle().equals(request.getTitle()) || !task.getListName().equals(request.getListName())) {
            if (taskRepository.existsByUserIdAndListNameAndTitle(userId, request.getListName(), request.getTitle())) {
                throw new BusinessException("Task with title '" + request.getTitle() + "' already exists in list '" + request.getListName() + "'");
            }
        }

        // Update fields
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setListName(request.getListName());
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }

        TaskEntity updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", taskId);

        return Task.of(updatedTask);
    }

    /**
     * Toggles task completion status.
     * 
     * @param taskId the task ID
     * @return updated task
     */
    @Transactional
    public Task toggleTaskCompletion(String taskId) {
        UUID userId = getCurrentUserId();
        log.info("Toggling completion for task: {} for user: {}", taskId, userId);

        TaskEntity task = taskRepository.findByIdAndUserId(UUID.fromString(taskId), userId)
                .orElseThrow(() -> new BusinessException("Task not found or access denied"));

        task.toggleCompleted();
        TaskEntity updatedTask = taskRepository.save(task);
        log.info("Task completion toggled: {}", taskId);

        return Task.of(updatedTask);
    }

    /**
     * Deletes a task.
     * Validates ownership.
     * 
     * @param taskId the task ID
     */
    @Transactional
    public void deleteTask(String taskId) {
        UUID userId = getCurrentUserId();
        log.info("Deleting task: {} for user: {}", taskId, userId);

        TaskEntity task = taskRepository.findByIdAndUserId(UUID.fromString(taskId), userId)
                .orElseThrow(() -> new BusinessException("Task not found or access denied"));

        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", taskId);
    }

    /**
     * Gets distinct list names for the authenticated user.
     * 
     * @return list of list names
     */
    @Transactional(readOnly = true)
    public List<String> getListNames() {
        UUID userId = getCurrentUserId();
        log.info("Fetching list names for user: {}", userId);

        return taskRepository.findDistinctListNamesByUserId(userId);
    }

    /**
     * Gets completed tasks for the authenticated user.
     * 
     * @return list of completed tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getCompletedTasks() {
        UUID userId = getCurrentUserId();
        log.info("Fetching completed tasks for user: {}", userId);

        List<TaskEntity> tasks = taskRepository.findCompletedByUserId(userId);
        return Task.of(tasks);
    }

    /**
     * Gets pending tasks for the authenticated user.
     * 
     * @return list of pending tasks
     */
    @Transactional(readOnly = true)
    public List<Task> getPendingTasks() {
        UUID userId = getCurrentUserId();
        log.info("Fetching pending tasks for user: {}", userId);

        List<TaskEntity> tasks = taskRepository.findPendingByUserId(userId);
        return Task.of(tasks);
    }

    /**
     * Gets current user ID from security context.
     * 
     * @return user ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }

        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return user.getId();
    }
}
