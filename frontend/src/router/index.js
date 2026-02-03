import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import QuotesList from '../views/QuotesList.vue'
import QuoteSubmit from '../views/QuoteSubmit.vue'
import SubmissionsList from '../views/SubmissionsList.vue'
import PublicationsList from '../views/PublicationsList.vue'
import AdminPanel from '../views/AdminPanel.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    redirect: '/publications'
  },
  {
    path: '/quotes',
    name: 'quotes',
    component: QuotesList
  },
  {
    path: '/submissions',
    name: 'submissions',
    component: SubmissionsList
  },
  {
    path: '/publications',
    name: 'publications',
    component: PublicationsList,
    meta: { public: true }
  },
  {
    path: '/submit',
    name: 'submit',
    component: QuoteSubmit,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminPanel,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { guestOnly: true }
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
    meta: { guestOnly: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/publications')
  } else if (to.meta.guestOnly && authStore.isAuthenticated) {
    next('/publications')
  } else {
    next()
  }
})

export default router
