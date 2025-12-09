/**
 * Task and TaskList related types
 */

export interface Task {
  id: string
  title: string
  description?: string
  listName: string
  completed: boolean
  createdAt: string
  updatedAt?: string
}

export interface TaskList {
  id: string
  name: string
  createdAt: string
}

export interface TaskRequest {
  title: string
  description?: string
  listName: string
  completed?: boolean
}

export interface TaskUpdateRequest extends Partial<TaskRequest> {
  id: string
}
