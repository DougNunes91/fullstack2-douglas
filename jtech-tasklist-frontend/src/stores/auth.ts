/**
 * Authentication Store
 * Manages user authentication state using Pinia
 * Follows Single Responsibility Principle (SRP)
 * Implements mock authentication as per requirements
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, AuthResponse } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value && !!user.value)
  const currentUser = computed(() => user.value)

  // Actions
  function setAuth(authResponse: AuthResponse) {
    user.value = authResponse.user
    accessToken.value = authResponse.accessToken
    refreshToken.value = authResponse.refreshToken
    error.value = null
  }

  function clearAuth() {
    user.value = null
    accessToken.value = null
    refreshToken.value = null
    error.value = null
  }

  /**
   * Mock login - accepts any non-empty credentials
   * Simulates authentication without backend call
   */
  async function login(email: string, password: string): Promise<boolean> {
    isLoading.value = true
    error.value = null

    try {
      // Validate non-empty fields
      if (!email.trim() || !password.trim()) {
        error.value = 'Email and password are required'
        return false
      }

      // Simulate API delay
      await new Promise(resolve => setTimeout(resolve, 500))

      // Mock successful authentication
      const mockResponse: AuthResponse = {
        accessToken: `mock-access-token-${Date.now()}`,
        refreshToken: `mock-refresh-token-${Date.now()}`,
        tokenType: 'Bearer',
        user: {
          id: `user-${Date.now()}`,
          name: email.split('@')[0].charAt(0).toUpperCase() + email.split('@')[0].slice(1),
          email: email
        }
      }

      setAuth(mockResponse)
      return true
    } catch (err) {
      error.value = 'Authentication failed'
      return false
    } finally {
      isLoading.value = false
    }
  }

  /**
   * Mock register - accepts any valid data
   */
  async function register(name: string, email: string, password: string): Promise<boolean> {
    isLoading.value = true
    error.value = null

    try {
      // Validate fields
      if (!name.trim() || !email.trim() || !password.trim()) {
        error.value = 'All fields are required'
        return false
      }

      if (password.length < 6) {
        error.value = 'Password must be at least 6 characters'
        return false
      }

      // Simulate API delay
      await new Promise(resolve => setTimeout(resolve, 500))

      // Mock successful registration
      const mockResponse: AuthResponse = {
        accessToken: `mock-access-token-${Date.now()}`,
        refreshToken: `mock-refresh-token-${Date.now()}`,
        tokenType: 'Bearer',
        user: {
          id: `user-${Date.now()}`,
          name: name,
          email: email
        }
      }

      setAuth(mockResponse)
      return true
    } catch (err) {
      error.value = 'Registration failed'
      return false
    } finally {
      isLoading.value = false
    }
  }

  function logout() {
    clearAuth()
  }

  return {
    // State
    user,
    accessToken,
    refreshToken,
    isLoading,
    error,
    // Getters
    isAuthenticated,
    currentUser,
    // Actions
    login,
    register,
    logout,
    setAuth,
    clearAuth
  }
}, {
  persist: true // Persist state to localStorage
})
