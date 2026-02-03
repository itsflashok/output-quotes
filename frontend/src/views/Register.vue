<template>
  <div class="register">
    <h1>Registrieren</h1>

    <form @submit.prevent="handleRegister">
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
          autocomplete="new-password"
        />
      </div>

      <div class="form-group">
        <label for="confirmPassword">Passwort bestätigen:</label>
        <input
          type="password"
          id="confirmPassword"
          v-model="confirmPassword"
          required
          autocomplete="new-password"
        />
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? 'Wird registriert...' : 'Registrieren' }}
      </button>
    </form>

    <p>
      Bereits ein Konto?
      <router-link to="/login">Hier anmelden</router-link>
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
const confirmPassword = ref('')
const loading = ref(false)

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    error('Passwörter stimmen nicht überein', 'Validierungsfehler')
    return
  }

  loading.value = true

  const result = await authStore.register(username.value, password.value)

  loading.value = false

  if (result.success) {
    success('Registrierung erfolgreich! Weiterleitung zur Anmeldung...')
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } else {
    error(result.error || 'Registrierung fehlgeschlagen', 'Registrierung fehlgeschlagen')
  }
}
</script>
