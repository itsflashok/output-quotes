<template>
  <div class="admin-panel">
    <h1>Verwaltungsbereich</h1>

    <div class="admin-section">
      <h2>Neuen Autor erstellen</h2>

      <form @submit.prevent="handleCreateAuthor">
        <div class="form-group">
          <label for="firstName">Vorname:</label>
          <input
            type="text"
            id="firstName"
            v-model="authorForm.firstName"
            required
          />
        </div>

        <div class="form-group">
          <label for="lastName">Nachname:</label>
          <input
            type="text"
            id="lastName"
            v-model="authorForm.lastName"
            required
          />
        </div>

        <button type="submit" :disabled="creating">
          {{ creating ? 'Wird erstellt...' : 'Autor erstellen' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { administrationService } from '../services/administrationService'
import { useToast } from '../composables/useToast'

const { error, success } = useToast()

const authorForm = ref({
  firstName: '',
  lastName: ''
})

const creating = ref(false)

const handleCreateAuthor = async () => {
  creating.value = true

  try {
    const response = await administrationService.createAuthor(authorForm.value)

    if (response && response.data !== undefined) {
      success('Autor erfolgreich erstellt!')
      // Formular zurücksetzen
      authorForm.value = {
        firstName: '',
        lastName: ''
      }
    } else {
      error('Autor konnte nicht erstellt werden', 'Erstellung fehlgeschlagen')
    }
  } catch (err) {
    const errors = err.response?.data?.errors || []
    const errorMessage = errors.length > 0
      ? (errors[0].message || errors[0].title || 'Autor konnte nicht erstellt werden')
      : (err.message || 'Autor konnte nicht erstellt werden')
    error(errorMessage, 'Erstellung fehlgeschlagen')
  } finally {
    creating.value = false
  }
}
</script>
