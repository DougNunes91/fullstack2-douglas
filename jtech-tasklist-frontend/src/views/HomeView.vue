<template>
  <v-app>
    <!-- App Bar -->
    <v-app-bar color="primary" elevation="2">
      <v-app-bar-title class="font-weight-bold">
        <v-icon class="mr-2">mdi-checkbox-marked-circle</v-icon>
        JTech TaskList
      </v-app-bar-title>

      <v-spacer />

      <v-chip class="mr-4" color="white" variant="outlined">
        <v-icon start>mdi-account-circle</v-icon>
        {{ authStore.currentUser?.name }}
      </v-chip>

      <v-btn icon @click="handleLogout">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <!-- Navigation Drawer -->
    <v-navigation-drawer permanent width="280" color="grey-lighten-5">
      <v-list>
        <v-list-item class="px-4 mb-2">
          <v-list-item-title class="text-h6 font-weight-bold">
            My Lists
          </v-list-item-title>
        </v-list-item>

        <v-divider class="mb-2" />

        <v-list-item
          v-for="list in listStore.allLists"
          :key="list.id"
          :active="listStore.currentListId === list.id"
          @click="selectList(list.id)"
          rounded="xl"
          class="mx-2 mb-1"
        >
          <template v-slot:prepend>
            <v-icon>mdi-format-list-bulleted</v-icon>
          </template>

          <v-list-item-title>{{ list.name }}</v-list-item-title>

          <template v-slot:append>
            <v-badge
              :content="taskStore.taskCountByList(list.name)"
              color="primary"
              inline
            />
            <v-menu>
              <template v-slot:activator="{ props }">
                <v-btn
                  icon="mdi-dots-vertical"
                  size="small"
                  variant="text"
                  v-bind="props"
                  @click.stop
                />
              </template>
              <v-list>
                <v-list-item @click="openEditListDialog(list)">
                  <template v-slot:prepend>
                    <v-icon>mdi-pencil</v-icon>
                  </template>
                  <v-list-item-title>Rename</v-list-item-title>
                </v-list-item>
                <v-list-item @click="openDeleteListDialog(list)">
                  <template v-slot:prepend>
                    <v-icon color="error">mdi-delete</v-icon>
                  </template>
                  <v-list-item-title class="text-error">Delete</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </template>
        </v-list-item>
      </v-list>

      <template v-slot:append>
        <div class="pa-4">
          <v-btn
            block
            color="primary"
            prepend-icon="mdi-plus"
            @click="showNewListDialog = true"
          >
            New List
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <!-- Main Content -->
    <v-main>
      <v-container fluid class="pa-6">
        <div v-if="listStore.currentList">
          <!-- List Header -->
          <v-row class="mb-6">
            <v-col>
              <h1 class="text-h3 font-weight-bold mb-2">
                {{ listStore.currentList.name }}
              </h1>
              <p class="text-body-1 text-grey-darken-1">
                {{ currentTasks.length }} tasks total
                <span class="mx-2">•</span>
                {{ completedCount }} completed
                <span class="mx-2">•</span>
                {{ pendingCount }} pending
              </p>
            </v-col>
          </v-row>

          <!-- Add Task Form -->
          <v-card class="mb-6" elevation="2">
            <v-card-text>
              <v-form @submit.prevent="addTask">
                <v-row>
                  <v-col cols="12" md="8">
                    <v-text-field
                      v-model="newTaskTitle"
                      label="Task title"
                      placeholder="What needs to be done?"
                      prepend-inner-icon="mdi-plus-circle-outline"
                      variant="outlined"
                      density="comfortable"
                      hide-details
                    />
                  </v-col>
                  <v-col cols="12" md="4">
                    <v-btn
                      type="submit"
                      color="primary"
                      size="large"
                      block
                      :disabled="!newTaskTitle.trim()"
                    >
                      Add Task
                    </v-btn>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
          </v-card>

          <!-- Filter Tabs -->
          <v-tabs v-model="filterTab" color="primary" class="mb-4">
            <v-tab value="all">All ({{ currentTasks.length }})</v-tab>
            <v-tab value="pending">Pending ({{ pendingCount }})</v-tab>
            <v-tab value="completed">Completed ({{ completedCount }})</v-tab>
          </v-tabs>

          <!-- Task List -->
          <v-card elevation="2">
            <v-list v-if="filteredTasks.length > 0">
              <v-list-item
                v-for="task in filteredTasks"
                :key="task.id"
                class="py-2"
              >
                <template v-slot:prepend>
                  <v-checkbox-btn
                    :model-value="task.completed"
                    @update:model-value="toggleTask(task.id)"
                    color="success"
                  />
                </template>

                <v-list-item-title
                  :class="{ 'text-decoration-line-through text-grey': task.completed }"
                >
                  {{ task.title }}
                </v-list-item-title>

                <v-list-item-subtitle v-if="task.description">
                  {{ task.description }}
                </v-list-item-subtitle>

                <template v-slot:append>
                  <v-btn
                    icon="mdi-pencil"
                    size="small"
                    variant="text"
                    @click="openEditTaskDialog(task)"
                  />
                  <v-btn
                    icon="mdi-delete"
                    size="small"
                    variant="text"
                    color="error"
                    @click="openDeleteTaskDialog(task)"
                  />
                </template>
              </v-list-item>
            </v-list>

            <v-card-text v-else class="text-center py-12">
              <v-icon size="64" color="grey-lighten-1" class="mb-4">
                mdi-checkbox-marked-circle-outline
              </v-icon>
              <p class="text-h6 text-grey">
                {{ filterTab === 'all' ? 'No tasks yet' : `No ${filterTab} tasks` }}
              </p>
            </v-card-text>
          </v-card>
        </div>

        <div v-else class="text-center py-12">
          <v-icon size="120" color="grey-lighten-1" class="mb-4">
            mdi-format-list-bulleted
          </v-icon>
          <h2 class="text-h4 text-grey mb-4">No list selected</h2>
          <p class="text-body-1 text-grey mb-6">Create a list to get started</p>
          <v-btn
            color="primary"
            size="large"
            prepend-icon="mdi-plus"
            @click="showNewListDialog = true"
          >
            Create Your First List
          </v-btn>
        </div>
      </v-container>
    </v-main>

    <!-- New List Dialog -->
    <v-dialog v-model="showNewListDialog" max-width="500">
      <v-card>
        <v-card-title>Create New List</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="newListName"
            label="List name"
            placeholder="e.g., Work, Personal, Studies"
            variant="outlined"
            autofocus
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showNewListDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="createList">Create</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Edit List Dialog -->
    <v-dialog v-model="showEditListDialog" max-width="500">
      <v-card>
        <v-card-title>Rename List</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="editListName"
            label="List name"
            variant="outlined"
            autofocus
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showEditListDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="updateList">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete List Dialog -->
    <v-dialog v-model="showDeleteListDialog" max-width="500">
      <v-card>
        <v-card-title class="text-error">Delete List</v-card-title>
        <v-card-text>
          <p>Are you sure you want to delete <strong>{{ selectedList?.name }}</strong>?</p>
          <v-alert type="warning" variant="tonal" class="mt-4">
            This will also delete all {{ taskStore.taskCountByList(selectedList?.name || '') }} tasks in this list.
          </v-alert>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showDeleteListDialog = false">Cancel</v-btn>
          <v-btn color="error" @click="deleteList">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Edit Task Dialog -->
    <v-dialog v-model="showEditTaskDialog" max-width="600">
      <v-card>
        <v-card-title>Edit Task</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="editTaskTitle"
            label="Task title"
            variant="outlined"
            class="mb-4"
          />
          <v-textarea
            v-model="editTaskDescription"
            label="Description (optional)"
            variant="outlined"
            rows="3"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showEditTaskDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="updateTask">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Task Dialog -->
    <v-dialog v-model="showDeleteTaskDialog" max-width="500">
      <v-card>
        <v-card-title class="text-error">Delete Task</v-card-title>
        <v-card-text>
          Are you sure you want to delete <strong>{{ selectedTask?.title }}</strong>?
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showDeleteTaskDialog = false">Cancel</v-btn>
          <v-btn color="error" @click="deleteTask">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar for notifications -->
    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      :timeout="3000"
    >
      {{ snackbarMessage }}
    </v-snackbar>
  </v-app>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useListStore } from '@/stores/lists'
import { useTaskStore } from '@/stores/tasks'
import type { Task, TaskList } from '@/types/task'

const router = useRouter()
const authStore = useAuthStore()
const listStore = useListStore()
const taskStore = useTaskStore()

// Task form
const newTaskTitle = ref('')
const filterTab = ref('all')

// List dialogs
const showNewListDialog = ref(false)
const newListName = ref('')
const showEditListDialog = ref(false)
const editListName = ref('')
const showDeleteListDialog = ref(false)
const selectedList = ref<TaskList | null>(null)

// Task dialogs
const showEditTaskDialog = ref(false)
const editTaskTitle = ref('')
const editTaskDescription = ref('')
const showDeleteTaskDialog = ref(false)
const selectedTask = ref<Task | null>(null)

// Snackbar
const showSnackbar = ref(false)
const snackbarMessage = ref('')
const snackbarColor = ref('success')

// Computed
const currentTasks = computed(() => {
  if (!listStore.currentList) return []
  return taskStore.tasksByList(listStore.currentList.name)
})

const completedCount = computed(() => 
  currentTasks.value.filter(t => t.completed).length
)

const pendingCount = computed(() => 
  currentTasks.value.filter(t => !t.completed).length
)

const filteredTasks = computed(() => {
  if (filterTab.value === 'completed') {
    return currentTasks.value.filter(t => t.completed)
  } else if (filterTab.value === 'pending') {
    return currentTasks.value.filter(t => !t.completed)
  }
  return currentTasks.value
})

// Methods
function selectList(listId: string) {
  listStore.setCurrentList(listId)
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}

function addTask() {
  if (!newTaskTitle.value.trim() || !listStore.currentList) return

  const result = taskStore.createTask({
    title: newTaskTitle.value,
    listName: listStore.currentList.name,
    completed: false
  })

  if (result) {
    newTaskTitle.value = ''
    showNotification('Task added successfully', 'success')
  } else {
    showNotification(taskStore.error || 'Failed to add task', 'error')
  }
}

function toggleTask(taskId: string) {
  taskStore.toggleTaskCompletion(taskId)
}

function createList() {
  if (!newListName.value.trim()) return

  const result = listStore.createList(newListName.value)
  if (result) {
    showNewListDialog.value = false
    newListName.value = ''
    showNotification('List created successfully', 'success')
  } else {
    showNotification(listStore.error || 'Failed to create list', 'error')
  }
}

function openEditListDialog(list: TaskList) {
  selectedList.value = list
  editListName.value = list.name
  showEditListDialog.value = true
}

function updateList() {
  if (!selectedList.value || !editListName.value.trim()) return

  const success = listStore.updateList(selectedList.value.id, editListName.value)
  if (success) {
    showEditListDialog.value = false
    showNotification('List renamed successfully', 'success')
  } else {
    showNotification(listStore.error || 'Failed to rename list', 'error')
  }
}

function openDeleteListDialog(list: TaskList) {
  selectedList.value = list
  showDeleteListDialog.value = true
}

function deleteList() {
  if (!selectedList.value) return

  taskStore.deleteTasksByList(selectedList.value.name)
  listStore.deleteList(selectedList.value.id)
  showDeleteListDialog.value = false
  showNotification('List deleted successfully', 'success')
}

function openEditTaskDialog(task: Task) {
  selectedTask.value = task
  editTaskTitle.value = task.title
  editTaskDescription.value = task.description || ''
  showEditTaskDialog.value = true
}

function updateTask() {
  if (!selectedTask.value || !editTaskTitle.value.trim()) return

  const success = taskStore.updateTask(selectedTask.value.id, {
    title: editTaskTitle.value,
    description: editTaskDescription.value
  })

  if (success) {
    showEditTaskDialog.value = false
    showNotification('Task updated successfully', 'success')
  } else {
    showNotification(taskStore.error || 'Failed to update task', 'error')
  }
}

function openDeleteTaskDialog(task: Task) {
  selectedTask.value = task
  showDeleteTaskDialog.value = true
}

function deleteTask() {
  if (!selectedTask.value) return

  taskStore.deleteTask(selectedTask.value.id)
  showDeleteTaskDialog.value = false
  showNotification('Task deleted successfully', 'success')
}

function showNotification(message: string, color: string) {
  snackbarMessage.value = message
  snackbarColor.value = color
  showSnackbar.value = true
}
</script>

<style scoped>
.v-app {
  background-color: #f5f5f5 !important;
}

.v-main {
  background-color: #f5f5f5 !important;
  min-height: 100vh;
}

.v-container {
  max-width: 1400px;
}
</style>
