/**
 * Tasks Store
 * Manages tasks within lists using Pinia
 * Follows Single Responsibility Principle (SRP)
 * Implements complete CRUD operations with validation
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Task, TaskRequest } from '@/types/task'

export const useTaskStore = defineStore('tasks', () => {
  // State
  const tasks = ref<Task[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const allTasks = computed(() => tasks.value)
  
  const tasksByList = computed(() => (listName: string) => 
    tasks.value.filter(task => task.listName === listName)
  )

  const completedTasks = computed(() => 
    tasks.value.filter(task => task.completed)
  )

  const pendingTasks = computed(() => 
    tasks.value.filter(task => !task.completed)
  )

  const tasksByListAndStatus = computed(() => (listName: string, completed: boolean) =>
    tasks.value.filter(task => task.listName === listName && task.completed === completed)
  )

  const taskCount = computed(() => tasks.value.length)

  const taskCountByList = computed(() => (listName: string) =>
    tasks.value.filter(task => task.listName === listName).length
  )

  // Actions
  function createTask(request: TaskRequest): Task | null {
    // Validation
    if (!request.title.trim()) {
      error.value = 'Task title is required'
      return null
    }

    if (!request.listName.trim()) {
      error.value = 'List name is required'
      return null
    }

    // Check for duplicate title in same list
    const duplicate = tasks.value.find(
      task => task.listName === request.listName && 
              task.title.toLowerCase() === request.title.toLowerCase()
    )

    if (duplicate) {
      error.value = `Task with title "${request.title}" already exists in this list`
      return null
    }

    // Create new task
    const newTask: Task = {
      id: Date.now().toString(),
      title: request.title.trim(),
      description: request.description?.trim(),
      listName: request.listName,
      completed: request.completed || false,
      createdAt: new Date().toISOString()
    }

    tasks.value.push(newTask)
    error.value = null
    return newTask
  }

  function updateTask(id: string, request: Partial<TaskRequest>): boolean {
    const index = tasks.value.findIndex(task => task.id === id)
    if (index === -1) {
      error.value = 'Task not found'
      return false
    }

    const task = tasks.value[index]

    // Validate title if being updated
    if (request.title !== undefined && !request.title.trim()) {
      error.value = 'Task title is required'
      return false
    }

    // Check for duplicate if title or list is changing
    if (request.title || request.listName) {
      const newTitle = (request.title || task.title).toLowerCase()
      const newListName = request.listName || task.listName

      const duplicate = tasks.value.find(
        t => t.id !== id && 
             t.listName === newListName && 
             t.title.toLowerCase() === newTitle
      )

      if (duplicate) {
        error.value = `Task with title "${request.title}" already exists in this list`
        return false
      }
    }

    // Update task
    if (request.title !== undefined) task.title = request.title.trim()
    if (request.description !== undefined) task.description = request.description?.trim()
    if (request.listName !== undefined) task.listName = request.listName
    if (request.completed !== undefined) task.completed = request.completed
    
    task.updatedAt = new Date().toISOString()
    error.value = null
    return true
  }

  function toggleTaskCompletion(id: string): boolean {
    const task = tasks.value.find(t => t.id === id)
    if (!task) {
      error.value = 'Task not found'
      return false
    }

    task.completed = !task.completed
    task.updatedAt = new Date().toISOString()
    error.value = null
    return true
  }

  function deleteTask(id: string): boolean {
    const index = tasks.value.findIndex(task => task.id === id)
    if (index === -1) {
      error.value = 'Task not found'
      return false
    }

    tasks.value.splice(index, 1)
    error.value = null
    return true
  }

  function deleteTasksByList(listName: string): number {
    const initialLength = tasks.value.length
    tasks.value = tasks.value.filter(task => task.listName !== listName)
    return initialLength - tasks.value.length
  }

  function getTaskById(id: string): Task | null {
    return tasks.value.find(task => task.id === id) || null
  }

  function clearError() {
    error.value = null
  }

  function clearAllTasks() {
    tasks.value = []
    error.value = null
  }

  return {
    // State
    tasks,
    isLoading,
    error,
    // Getters
    allTasks,
    tasksByList,
    completedTasks,
    pendingTasks,
    tasksByListAndStatus,
    taskCount,
    taskCountByList,
    // Actions
    createTask,
    updateTask,
    toggleTaskCompletion,
    deleteTask,
    deleteTasksByList,
    getTaskById,
    clearError,
    clearAllTasks
  }
}, {
  persist: true
})
