import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authorService } from '../services/authorService'

export const useAuthorStore = defineStore('author', () => {
  const authors = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchAuthors() {
    loading.value = true
    error.value = null
    try {
      const response = await authorService.getAll()
      if (response && response.data) {
        const page = response.data
        if (page.content && Array.isArray(page.content)) {
          authors.value = page.content
        } else {
          console.error('No content array found in authors response:', page)
          authors.value = []
        }
      }
    } catch (err) {
      // Use German error message from API interceptor
      error.value = err.message || 'Autoren konnten nicht geladen werden'
      console.error('Fehler beim Laden der Autoren:', err)
    } finally {
      loading.value = false
    }
  }

  return {
    authors,
    loading,
    error,
    fetchAuthors
  }
})
