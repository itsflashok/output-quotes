<template>
  <div id="app">
    <div v-if="authStore.isValidating" class="loading">Validiere Sitzung...</div>
    <template v-else>
      <!-- Main Navigation Bar -->
      <nav class="navbar">
        <div class="navbar-container">
          <!-- Left: Logo and Brand -->
          <div class="navbar-brand">
            <img src="./assets/logo_brand.png" alt="Output Center Logo" class="brand-logo">
            <div class="brand-name">
              <span class="brand-output">output</span>
              <span class="brand-dot">.</span>
              <span class="brand-center">center</span>
            </div>
          </div>

          <div class="navbar-spacer"></div>

          <!-- Right: Navigation Links -->
          <div class="navbar-links">
            <router-link to="/publications" class="nav-link" active-class="active">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M3 12L5 10M5 10L12 3L19 10M5 10V20C5 20.5523 5.44772 21 6 21H9M19 10L21 12M19 10V20C19 20.5523 18.5523 21 18 21H15M9 21C9.55228 21 10 20.5523 10 20V16C10 15.4477 10.4477 15 11 15H13C13.5523 15 14 15.4477 14 16V20C14 20.5523 14.4477 21 15 21M9 21H15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Startseite</span>
            </router-link>
            <router-link to="/submissions" class="nav-link" active-class="active">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M9 5H7C5.89543 5 5 5.89543 5 7V19C5 20.1046 5.89543 21 7 21H17C18.1046 21 19 20.1046 19 19V7C19 5.89543 18.1046 5 17 5H15M9 5C9 6.10457 9.89543 7 11 7H13C14.1046 7 15 6.10457 15 5M9 5C9 3.89543 9.89543 3 11 3H13C14.1046 3 15 3.89543 15 5M12 12H15M12 16H15M9 12H9.01M9 16H9.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              <span>Einreichungen</span>
            </router-link>
            <!-- Submit Button (inline with nav, only when authenticated) -->
            <router-link to="/submit" v-if="authStore.isAuthenticated" class="nav-link submit-link" active-class="active">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M12 4V20M4 12H20" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              </svg>
              <span>Zitat einreichen</span>
            </router-link>
          </div>

          <!-- Right: Profile or Auth Links -->
          <div class="navbar-right">
            <div class="profile-section" v-if="authStore.isAuthenticated">
              <button class="profile-trigger" @click="toggleProfileMenu" ref="profileRef">
                <div class="profile-avatar">
                  {{ getUserInitials }}
                </div>
                <span class="profile-name">{{ authStore.username }}</span>
                <svg class="profile-arrow" :class="{ 'rotate': isProfileMenuOpen }" width="16" height="16" viewBox="0 0 20 20" fill="none">
                  <path d="M5 7.5L10 12.5L15 7.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>

              <transition name="dropdown">
                <div v-if="isProfileMenuOpen" class="profile-menu">
                  <router-link to="/admin" v-if="authStore.isAdmin" class="menu-item" @click="closeProfileMenu">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <path d="M10.325 4.317C10.751 2.561 13.249 2.561 13.675 4.317C13.7389 4.5808 13.8642 4.82578 14.0407 5.032C14.2172 5.23822 14.4399 5.39985 14.6907 5.50375C14.9414 5.60764 15.2132 5.65085 15.4838 5.62987C15.7544 5.60889 16.0162 5.5243 16.248 5.383C17.791 4.443 19.558 6.209 18.618 7.753C18.4769 7.98466 18.3924 8.24634 18.3715 8.51677C18.3506 8.78721 18.3938 9.05877 18.4975 9.30938C18.6013 9.55999 18.7627 9.78258 18.9687 9.95905C19.1747 10.1355 19.4194 10.2609 19.683 10.325C21.439 10.751 21.439 13.249 19.683 13.675C19.4192 13.7389 19.1742 13.8642 18.968 14.0407C18.7618 14.2172 18.6001 14.4399 18.4963 14.6907C18.3924 14.9414 18.3491 15.2132 18.3701 15.4838C18.3911 15.7544 18.4757 16.0162 18.617 16.248C19.557 17.791 17.791 19.558 16.247 18.618C16.0153 18.4769 15.7537 18.3924 15.4832 18.3715C15.2128 18.3506 14.9412 18.3938 14.6906 18.4975C14.44 18.6013 14.2174 18.7627 14.0409 18.9687C13.8645 19.1747 13.7391 19.4194 13.675 19.683C13.249 21.439 10.751 21.439 10.325 19.683C10.2611 19.4192 10.1358 19.1742 9.95929 18.968C9.7828 18.7618 9.56011 18.6001 9.30935 18.4963C9.05859 18.3924 8.78683 18.3491 8.51621 18.3701C8.24559 18.3911 7.98375 18.4757 7.752 18.617C6.209 19.557 4.442 17.791 5.382 16.247C5.5231 16.0153 5.60755 15.7537 5.62848 15.4832C5.64942 15.2128 5.60624 14.9412 5.50247 14.6906C5.3987 14.44 5.23726 14.2174 5.03127 14.0409C4.82529 13.8645 4.58056 13.7391 4.317 13.675C2.561 13.249 2.561 10.751 4.317 10.325C4.5808 10.2611 4.82578 10.1358 5.032 9.95929C5.23822 9.7828 5.39985 9.56011 5.50375 9.30935C5.60764 9.05859 5.65085 8.78683 5.62987 8.51621C5.60889 8.24559 5.5243 7.98375 5.383 7.752C4.443 6.209 6.209 4.442 7.753 5.382C8.753 5.99 10.049 5.452 10.325 4.317Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    Verwaltung
                  </router-link>
                  <div class="menu-divider" v-if="authStore.isAdmin"></div>
                  <button class="menu-item logout" @click="handleLogout">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                      <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9M16 17L21 12M21 12L16 7M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    Abmelden
                  </button>
                </div>
              </transition>
            </div>

            <!-- Auth links for non-authenticated users -->
            <div class="auth-links" v-else>
              <router-link to="/login" class="auth-link">Anmelden</router-link>
              <router-link to="/register" class="auth-link">Registrieren</router-link>
            </div>
          </div>
        </div>
      </nav>

      <main>
        <router-view />
      </main>
    </template>

    <Toast />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from './stores/auth'
import { useRouter } from 'vue-router'
import { useToast } from './composables/useToast'
import Toast from './components/Toast.vue'

const authStore = useAuthStore()
const router = useRouter()
const { warning } = useToast()

const isProfileMenuOpen = ref(false)
const profileRef = ref(null)

const getUserInitials = computed(() => {
  if (!authStore.username) return '?'
  const parts = authStore.username.split(' ')
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }
  return authStore.username.substring(0, 2).toUpperCase()
})

const toggleProfileMenu = () => {
  isProfileMenuOpen.value = !isProfileMenuOpen.value
}

const closeProfileMenu = () => {
  isProfileMenuOpen.value = false
}

const handleClickOutside = (event) => {
  if (profileRef.value && !profileRef.value.contains(event.target)) {
    isProfileMenuOpen.value = false
  }
}

const handleLogout = async () => {
  closeProfileMenu()
  const result = await authStore.logout()
  if (!result.success) {
    warning(result.error, 'Abmeldewarnung')
  }
  router.push('/login')
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
