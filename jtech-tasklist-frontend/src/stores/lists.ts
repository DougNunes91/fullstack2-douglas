/**
 * Task Lists Store
 * Manages task lists (categories) using Pinia
 * Follows Single Responsibility Principle (SRP)
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { TaskList } from '@/types/task'

export const useListStore = defineStore('lists', () => {
  // State
  const lists = ref<TaskList[]>([
    { id: '1', name: 'Personal', createdAt: new Date().toISOString() },
    { id: '2', name: 'Work', createdAt: new Date().toISOString() },
    { id: '3', name: 'Studies', createdAt: new Date().toISOString() }
  ])
  const currentListId = ref<string | null>('1')
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const allLists = computed(() => lists.value)
  const currentList = computed(() => 
    lists.value.find(list => list.id === currentListId.value) || null
  )
  const listCount = computed(() => lists.value.length)

  // Actions
  function createList(name: string): TaskList | null {
    if (!name.trim()) {
      error.value = 'List name is required'
      return null
    }

    // Check for duplicate
    if (lists.value.some(list => list.name.toLowerCase() === name.toLowerCase())) {
      error.value = 'A list with this name already exists'
      return null
    }

    const newList: TaskList = {
      id: Date.now().toString(),
      name: name.trim(),
      createdAt: new Date().toISOString()
    }

    lists.value.push(newList)
    error.value = null
    return newList
  }

  function updateList(id: string, name: string): boolean {
    if (!name.trim()) {
      error.value = 'List name is required'
      return false
    }

    const index = lists.value.findIndex(list => list.id === id)
    if (index === -1) {
      error.value = 'List not found'
      return false
    }

    // Check for duplicate (excluding current list)
    if (lists.value.some(list => list.id !== id && list.name.toLowerCase() === name.toLowerCase())) {
      error.value = 'A list with this name already exists'
      return false
    }

    lists.value[index].name = name.trim()
    error.value = null
    return true
  }

  function deleteList(id: string): boolean {
    const index = lists.value.findIndex(list => list.id === id)
    if (index === -1) {
      error.value = 'List not found'
      return false
    }

    lists.value.splice(index, 1)
    
    // If current list was deleted, switch to first available
    if (currentListId.value === id) {
      currentListId.value = lists.value.length > 0 ? lists.value[0].id : null
    }

    error.value = null
    return true
  }

  function setCurrentList(id: string) {
    if (lists.value.some(list => list.id === id)) {
      currentListId.value = id
      error.value = null
    } else {
      error.value = 'List not found'
    }
  }

  function getListById(id: string): TaskList | null {
    return lists.value.find(list => list.id === id) || null
  }

  function clearError() {
    error.value = null
  }

  return {
    // State
    lists,
    currentListId,
    isLoading,
    error,
    // Getters
    allLists,
    currentList,
    listCount,
    // Actions
    createList,
    updateList,
    deleteList,
    setCurrentList,
    getListById,
    clearError
  }
}, {
  persist: true
})
