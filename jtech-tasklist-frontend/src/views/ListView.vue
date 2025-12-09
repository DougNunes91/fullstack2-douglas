<template>
  <div>
    <h1>{{ listName }}</h1>
    <!-- This is a placeholder for list-specific view -->
    <!-- The main HomeView handles all functionality -->
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue'

const route = useRoute()
const router = useRouter()
const listName = route.params.listName as string

onMounted(() => {
  // Redirect to home - all functionality is in HomeView
  router.push('/')
})
</script>
