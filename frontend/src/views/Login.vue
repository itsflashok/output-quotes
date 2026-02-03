<template>
  <div class="login">
    <h1>Anmelden</h1>

    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="username">Benutzername:</label>
        <input
          type="text"
          id="username"
          v-model="username"
          required
          autocomplete="username"
        />
      </div>

      <div class="form-group">
        <label for="password">Passwort:</label>
        <input
          type="password"
          id="password"
          v-model="password"
          required
          autocomplete="current-password"
        />
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? 'Wird angemeldet...' : 'Anmelden' }}
      </button>
    </form>

    <p>
      Noch kein Konto?
      <router-link to="/register">Hier registrieren</router-link>
    </p>

    <p>
      <router-link to="/publications">← Zurück zur Startseite</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useToast } from '../composables/useToast'

const router = useRouter()
const authStore = useAuthStore()
const { error, success } = useToast()

const username = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true

  const result = await authStore.login(username.value, password.value)

  loading.value = false

  if (result.success) {
    success('Erfolgreich angemeldet!')
    router.push('/')
  } else {
    error(result.error || 'Anmeldung fehlgeschlagen', 'Anmeldung fehlgeschlagen')
  }
}
</script>
