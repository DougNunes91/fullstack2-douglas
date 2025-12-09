<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container fluid class="fill-height pa-0">
        <v-row no-gutters class="fill-height">
          <v-col cols="12" md="6" class="d-none d-md-flex bg-primary align-center justify-center">
            <div class="text-center pa-8">
              <v-icon size="120" color="white" class="mb-6">mdi-checkbox-marked-circle-outline</v-icon>
              <h1 class="text-h3 text-white mb-4 font-weight-bold">JTech TaskList</h1>
              <p class="text-h6 text-white">
                Organize your tasks efficiently with multiple lists
              </p>
            </div>
          </v-col>

          <v-col cols="12" md="6" class="d-flex align-center justify-center">
            <v-card class="pa-8" width="500" elevation="8">
              <v-card-title class="text-h4 text-center mb-4 font-weight-bold">
                Welcome Back
              </v-card-title>
              <v-card-subtitle class="text-center mb-6">
                Sign in to access your tasks
              </v-card-subtitle>

              <v-alert
                v-if="authStore.error"
                type="error"
                variant="tonal"
                closable
                class="mb-4"
                @click:close="authStore.error = null"
              >
                {{ authStore.error }}
              </v-alert>

              <v-form @submit.prevent="handleLogin" ref="formRef">
                <v-text-field
                  v-model="email"
                  label="Email"
                  type="email"
                  prepend-inner-icon="mdi-email"
                  :rules="emailRules"
                  required
                  variant="outlined"
                  class="mb-4"
                />

                <v-text-field
                  v-model="password"
                  label="Password"
                  :type="showPassword ? 'text' : 'password'"
                  prepend-inner-icon="mdi-lock"
                  :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  @click:append-inner="showPassword = !showPassword"
                  :rules="passwordRules"
                  required
                  variant="outlined"
                  class="mb-6"
                />

                <v-btn
                  type="submit"
                  color="primary"
                  size="large"
                  block
                  :loading="authStore.isLoading"
                  class="mb-4"
                >
                  Sign In
                </v-btn>

                <v-divider class="my-6" />

                <div class="text-center">
                  <span class="text-body-2">Don't have an account?</span>
                  <v-btn
                    variant="text"
                    color="primary"
                    @click="$router.push('/register')"
                    class="ml-2"
                  >
                    Sign Up
                  </v-btn>
                </div>
              </v-form>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref()
const email = ref('')
const password = ref('')
const showPassword = ref(false)

const emailRules = [
  (v: string) => !!v || 'Email is required',
  (v: string) => /.+@.+\..+/.test(v) || 'Email must be valid',
]

const passwordRules = [
  (v: string) => !!v || 'Password is required',
]

async function handleLogin() {
  const { valid } = await formRef.value.validate()
  
  if (valid) {
    const success = await authStore.login(email.value, password.value)
    if (success) {
      router.push('/')
    }
  }
}
</script>
