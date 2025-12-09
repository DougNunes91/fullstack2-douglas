/*
*  @(#)TaskController.java
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
package br.com.jtech.tasklist.adapters.input.controllers.task;

import br.com.jtech.tasklist.adapters.input.protocols.task.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.task.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.usecases.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Task Controller.
 * Handles task CRUD operations with ownership validation.
 * Follows Single Responsibility Principle (SRP) - handles HTTP concerns only.
 * 
 * @author jtech-team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new task.
     * 
     * @param request the task creation request
     * @return created task
     */
    @PostMapping
    @Operation(summary = "Create task", description = "Creates a new task for the authenticated user")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        log.info("Received request to create task: {}", request.getTitle());
        Task task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.of(task));
    }

    /**
     * Gets all tasks for the authenticated user.
     * 
     * @return list of tasks
     */
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks for the authenticated user")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("Received request to get all tasks");
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(TaskResponse.of(tasks));
    }

    /**
     * Gets tasks by list name.
     * 
     * @param listName the list name
     * @return list of tasks
     */
    @GetMapping("/list/{listName}")
    @Operation(summary = "Get tasks by list", description = "Retrieves tasks for a specific list")
    public ResponseEntity<List<TaskResponse>> getTasksByListName(@PathVariable String listName) {
        log.info("Received request to get tasks for list: {}", listName);
        List<Task> tasks = taskService.getTasksByListName(listName);
        return ResponseEntity.ok(TaskResponse.of(tasks));
    }

    /**
     * Gets a specific task by ID.
     * 
     * @param id the task ID
     * @return the task
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        log.info("Received request to get task: {}", id);
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(TaskResponse.of(task));
    }

    /**
     * Updates a task.
     * 
     * @param id the task ID
     * @param request the update request
     * @return updated task
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates an existing task")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String id, 
                                                    @Valid @RequestBody TaskRequest request) {
        log.info("Received request to update task: {}", id);
        Task task = taskService.updateTask(id, request);
        return ResponseEntity.ok(TaskResponse.of(task));
    }

    /**
     * Toggles task completion status.
     * 
     * @param id the task ID
     * @return updated task
     */
    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle task completion", description = "Toggles the completion status of a task")
    public ResponseEntity<TaskResponse> toggleTaskCompletion(@PathVariable String id) {
        log.info("Received request to toggle completion for task: {}", id);
        Task task = taskService.toggleTaskCompletion(id);
        return ResponseEntity.ok(TaskResponse.of(task));
    }

    /**
     * Deletes a task.
     * 
     * @param id the task ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        log.info("Received request to delete task: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets distinct list names.
     * 
     * @return list of list names
     */
    @GetMapping("/lists")
    @Operation(summary = "Get list names", description = "Retrieves all distinct list names for the user")
    public ResponseEntity<List<String>> getListNames() {
        log.info("Received request to get list names");
        List<String> listNames = taskService.getListNames();
        return ResponseEntity.ok(listNames);
    }

    /**
     * Gets completed tasks.
     * 
     * @return list of completed tasks
     */
    @GetMapping("/completed")
    @Operation(summary = "Get completed tasks", description = "Retrieves all completed tasks for the user")
    public ResponseEntity<List<TaskResponse>> getCompletedTasks() {
        log.info("Received request to get completed tasks");
        List<Task> tasks = taskService.getCompletedTasks();
        return ResponseEntity.ok(TaskResponse.of(tasks));
    }

    /**
     * Gets pending tasks.
     * 
     * @return list of pending tasks
     */
    @GetMapping("/pending")
    @Operation(summary = "Get pending tasks", description = "Retrieves all pending tasks for the user")
    public ResponseEntity<List<TaskResponse>> getPendingTasks() {
        log.info("Received request to get pending tasks");
        List<Task> tasks = taskService.getPendingTasks();
        return ResponseEntity.ok(TaskResponse.of(tasks));
    }
}
