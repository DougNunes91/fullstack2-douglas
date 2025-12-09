/*
*  @(#)TaskServiceTest.java
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService.
 * Tests task management business logic.
 * 
 * @author jtech-team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private UUID userId;
    private UserEntity userEntity;
    private TaskEntity taskEntity;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        userEntity = UserEntity.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .active(true)
                .build();

        taskEntity = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Test Description")
                .listName("Work")
                .completed(false)
                .user(userEntity)
                .build();

        taskRequest = TaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .listName("Personal")
                .completed(false)
                .build();

        // Mock security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() {
        // Given
        when(taskRepository.existsByUserIdAndListNameAndTitle(any(), anyString(), anyString()))
                .thenReturn(false);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // When
        Task result = taskService.createTask(taskRequest);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).existsByUserIdAndListNameAndTitle(userId, "Personal", "New Task");
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate task")
    void shouldThrowExceptionWhenCreatingDuplicateTask() {
        // Given
        when(taskRepository.existsByUserIdAndListNameAndTitle(any(), anyString(), anyString()))
                .thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> taskService.createTask(taskRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should get all tasks for user")
    void shouldGetAllTasksForUser() {
        // Given
        List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
        when(taskRepository.findByUserId(any(UUID.class))).thenReturn(taskEntities);

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(1);
        verify(taskRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should get tasks by list name")
    void shouldGetTasksByListName() {
        // Given
        List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
        when(taskRepository.findByUserIdAndListName(any(UUID.class), anyString()))
                .thenReturn(taskEntities);

        // When
        List<Task> result = taskService.getTasksByListName("Work");

        // Then
        assertThat(result).hasSize(1);
        verify(taskRepository).findByUserIdAndListName(userId, "Work");
    }

    @Test
    @DisplayName("Should get task by ID")
    void shouldGetTaskById() {
        // Given
        String taskId = taskEntity.getId().toString();
        when(taskRepository.findByIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(taskEntity));

        // When
        Task result = taskService.getTaskById(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).findByIdAndUserId(UUID.fromString(taskId), userId);
    }

    @Test
    @DisplayName("Should throw exception when task not found")
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        String taskId = UUID.randomUUID().toString();
        when(taskRepository.findByIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        // Given
        String taskId = taskEntity.getId().toString();
        when(taskRepository.findByIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(taskEntity));
        when(taskRepository.existsByUserIdAndListNameAndTitle(any(), anyString(), anyString()))
                .thenReturn(false);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // When
        Task result = taskService.updateTask(taskId, taskRequest);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).findByIdAndUserId(UUID.fromString(taskId), userId);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    @DisplayName("Should toggle task completion")
    void shouldToggleTaskCompletion() {
        // Given
        String taskId = taskEntity.getId().toString();
        when(taskRepository.findByIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        // When
        Task result = taskService.toggleTaskCompletion(taskId);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).findByIdAndUserId(UUID.fromString(taskId), userId);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() {
        // Given
        String taskId = taskEntity.getId().toString();
        when(taskRepository.findByIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(taskEntity));

        // When
        taskService.deleteTask(taskId);

        // Then
        verify(taskRepository).findByIdAndUserId(UUID.fromString(taskId), userId);
        verify(taskRepository).delete(taskEntity);
    }

    @Test
    @DisplayName("Should get distinct list names")
    void shouldGetDistinctListNames() {
        // Given
        List<String> listNames = Arrays.asList("Work", "Personal", "Studies");
        when(taskRepository.findDistinctListNamesByUserId(any(UUID.class)))
                .thenReturn(listNames);

        // When
        List<String> result = taskService.getListNames();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("Work", "Personal", "Studies");
        verify(taskRepository).findDistinctListNamesByUserId(userId);
    }
}
