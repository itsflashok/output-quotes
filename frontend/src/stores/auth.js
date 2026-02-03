import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../services/authService'
import api from '../services/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isValidating = ref(true) // Start as validating
  const isAuthenticated = computed(() => !!user.value)
  const username = computed(() => user.value?.username || '')
  const isAdmin = computed(() => {
    return user.value?.roles?.includes('ADMIN') || false
  })

  // Validate session by calling the /me endpoint on page load
  const validateSession = async () => {
    console.log('validateSession: Starting validation')
    isValidating.value = true

    try {
      // Call /me endpoint to check if session is valid
      console.log('validateSession: Calling /api/authentication/me')
      const response = await api.get('/authentication/me')

      console.log('validateSession: Got response:', response)
      console.log('validateSession: Response data:', response.data)
      console.log('validateSession: Response status:', response.status)

      // If successful, session is valid
      if (response.data?.data?.roles) {
        console.log('validateSession: Session valid, setting user')
        // Get username from localStorage if available
        const storedUser = localStorage.getItem('user')
        console.log('validateSession: Stored user in localStorage:', storedUser)
        const parsedUser = storedUser ? JSON.parse(storedUser) : null

        user.value = {
          username: parsedUser?.username || 'User',
          roles: response.data.data.roles
        }
        // Update localStorage with fresh data
        localStorage.setItem('user', JSON.stringify(user.value))
        console.log('validateSession: Set user to:', user.value)
      } else {
        console.log('validateSession: Invalid response format, response.data =', response.data)
        localStorage.removeItem('user')
        user.value = null
      }
    } catch (error) {
      // If we get 401 or any error, session is invalid
      console.log('validateSession: Session invalid -', error.response?.status || error.message)
      localStorage.removeItem('user')
      user.value = null
    } finally {
      console.log('validateSession: Validation complete, user =', user.value)
      isValidating.value = false
    }
  }

  // Validate session on store creation
  validateSession()

  // Listen for custom auth-cleared event from API interceptor
  if (typeof window !== 'undefined') {
    window.addEventListener('auth-cleared', () => {
      console.log('Auth state sync: Clearing user state')
      user.value = null
    })
  }


  async function login(username, password) {
    try {
      const response = await authService.login(username, password)
      // Backend returns: { data: { roles: ["ADMIN", "USER"] } }
      if (response && response.data) {
        const loginResponse = response.data
        user.value = {
          username,
          roles: loginResponse.roles || []
        }
        // Persist to localStorage
        localStorage.setItem('user', JSON.stringify(user.value))
        return { success: true }
      }
      return { success: false, error: 'Anmeldung fehlgeschlagen' }
    } catch (error) {
      // Use German error message from API interceptor
      const errorMessage = error.message || 'Anmeldung fehlgeschlagen'
      return { success: false, error: errorMessage }
    }
  }

  async function register(username, password) {
    try {
      const response = await authService.register(username, password)
      // response is already the body { data: {...} }
      if (response && response.data !== undefined) {
        return { success: true }
      }
      return { success: false, error: 'Registrierung fehlgeschlagen' }
    } catch (error) {
      // Use German error message from API interceptor
      const errorMessage = error.message || 'Registrierung fehlgeschlagen'
      return { success: false, error: errorMessage }
    }
  }

  async function logout() {
    try {
      console.log('Calling logout endpoint...')
      await authService.logout()
      console.log('Logout successful, clearing state')
      user.value = null
      localStorage.removeItem('user')
      return { success: true }
    } catch (error) {
      console.error('Logout failed:', error)
      // Even if logout fails on backend, clear frontend state
      user.value = null
      localStorage.removeItem('user')
      // Rückgabe von Fehlerinformationen, damit der Aufrufer eine Benachrichtigung anzeigen kann
      const errorMsg = error.response?.status === 404
        ? 'Abmelde-Endpunkt nicht gefunden (404) - Frontend-Status gelöscht'
        : `Abmeldung fehlgeschlagen: ${error.message} - Frontend-Status gelöscht`
      return { success: false, error: errorMsg }
    }
  }

  async function checkAuth() {
    // This function is kept for compatibility but not used
    // Auth state is persisted via localStorage
    return { success: !!user.value }
  }

  return {
    user,
    isAuthenticated,
    isValidating,
    username,
    isAdmin,
    login,
    register,
    logout,
    checkAuth
  }
})
