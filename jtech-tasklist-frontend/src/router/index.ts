import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/lists/:listName',
      name: 'list',
      component: () => import('../views/ListView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      redirect: '/'
    }
  ],
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth
  const hideForAuth = to.meta.hideForAuth

  // Redirect authenticated users away from login/register pages
  if (hideForAuth && authStore.isAuthenticated) {
    next({ name: 'home' })
    return
  }

  // Redirect unauthenticated users to login
  if (requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
    return
  }

  next()
})

export default router

