/*
*  @(#)TaskRepository.java
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

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TaskEntity.
 * Follows Interface Segregation Principle (ISP) - specific methods only.
 * 
 * @author jtech-team
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    /**
     * Finds all tasks for a specific user.
     * Follows security principle - user can only access their own tasks.
     * 
     * @param userId the user's ID
     * @return list of tasks belonging to the user
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<TaskEntity> findByUserId(@Param("userId") UUID userId);

    /**
     * Finds tasks by user and list name.
     * Allows filtering tasks by category/list.
     * 
     * @param userId the user's ID
     * @param listName the list/category name
     * @return list of tasks in the specified list
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND t.listName = :listName ORDER BY t.createdAt DESC")
    List<TaskEntity> findByUserIdAndListName(@Param("userId") UUID userId, @Param("listName") String listName);

    /**
     * Finds a specific task by ID and user ID.
     * Ensures user can only access their own tasks.
     * 
     * @param id the task ID
     * @param userId the user's ID
     * @return Optional containing the task if found and belongs to user
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id AND t.user.id = :userId")
    Optional<TaskEntity> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    /**
     * Gets distinct list names for a user.
     * Used to display available lists/categories.
     * 
     * @param userId the user's ID
     * @return list of distinct list names
     */
    @Query("SELECT DISTINCT t.listName FROM TaskEntity t WHERE t.user.id = :userId ORDER BY t.listName")
    List<String> findDistinctListNamesByUserId(@Param("userId") UUID userId);

    /**
     * Finds completed tasks for a user.
     * 
     * @param userId the user's ID
     * @return list of completed tasks
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND t.completed = true ORDER BY t.updatedAt DESC")
    List<TaskEntity> findCompletedByUserId(@Param("userId") UUID userId);

    /**
     * Finds pending (not completed) tasks for a user.
     * 
     * @param userId the user's ID
     * @return list of pending tasks
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND t.completed = false ORDER BY t.createdAt DESC")
    List<TaskEntity> findPendingByUserId(@Param("userId") UUID userId);

    /**
     * Counts tasks in a specific list for a user.
     * 
     * @param userId the user's ID
     * @param listName the list name
     * @return count of tasks
     */
    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.user.id = :userId AND t.listName = :listName")
    long countByUserIdAndListName(@Param("userId") UUID userId, @Param("listName") String listName);

    /**
     * Checks if a task with the same title exists in a list for a user.
     * Prevents duplicate task titles within the same list.
     * 
     * @param userId the user's ID
     * @param listName the list name
     * @param title the task title
     * @return true if duplicate exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TaskEntity t " +
           "WHERE t.user.id = :userId AND t.listName = :listName AND LOWER(t.title) = LOWER(:title)")
    boolean existsByUserIdAndListNameAndTitle(@Param("userId") UUID userId, 
                                               @Param("listName") String listName, 
                                               @Param("title") String title);
}
