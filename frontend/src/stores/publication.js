import { defineStore } from 'pinia'
import { ref } from 'vue'
import { publicationService } from '../services/publicationService'

export const usePublicationStore = defineStore('publication', () => {
  const publications = ref([])
  const currentPage = ref(0)
  const totalPages = ref(0)
  const loading = ref(false)
  const error = ref(null)

  async function fetchPublications(page = 0) {
    loading.value = true
    error.value = null
    try {
      const response = await publicationService.getAll(page)

      if (response && response.data) {
        const responseData = response.data

        if (responseData.content && Array.isArray(responseData.content)) {
          publications.value = responseData.content

          // Handle pagination from nested 'page' object
          if (responseData.page) {
            currentPage.value = responseData.page.number || 0
            totalPages.value = responseData.page.totalPages || 1
          } else {
            // Fallback for old format
            currentPage.value = responseData.number || 0
            totalPages.value = responseData.totalPages || 1
          }
        } else {
          console.error('No content array found in response:', responseData)
          publications.value = []
        }
      }
    } catch (err) {
      // Use German error message from API interceptor
      error.value = err.message || 'Veröffentlichungen konnten nicht geladen werden'
      console.error('Error fetching publications:', err)
    } finally {
      loading.value = false
    }
  }

  async function likePublication(id) {
    try {
      const publication = publications.value.find(p => p.id === id)

      // If user already liked, remove the reaction (toggle off)
      if (publication?.selfReactionData?.reacted && publication?.selfReactionData?.reactionType === 'LIKE') {
        const response = await publicationService.removeReaction(id)
        if (response && response.data !== undefined) {
          const updatedPublication = response.data
          const index = publications.value.findIndex(p => p.id === id)
          if (index !== -1) {
            publications.value[index] = updatedPublication
          }
          return { success: true }
        }
      }
      // Otherwise just add/switch to like (backend handles switching automatically)
      else {
        const response = await publicationService.like(id)
        if (response && response.data !== undefined) {
          const updatedPublication = response.data
          const index = publications.value.findIndex(p => p.id === id)
          if (index !== -1) {
            publications.value[index] = updatedPublication
          }
          return { success: true }
        }
      }
      return { success: false, error: '"Gefällt mir" fehlgeschlagen' }
    } catch (err) {
      const errorMessage = err.message || '"Gefällt mir" fehlgeschlagen'
      return { success: false, error: errorMessage }
    }
  }

  async function dislikePublication(id) {
    try {
      const publication = publications.value.find(p => p.id === id)

      // If user already disliked, remove the reaction (toggle off)
      if (publication?.selfReactionData?.reacted && publication?.selfReactionData?.reactionType === 'DISLIKE') {
        const response = await publicationService.removeReaction(id)
        if (response && response.data !== undefined) {
          const updatedPublication = response.data
          const index = publications.value.findIndex(p => p.id === id)
          if (index !== -1) {
            publications.value[index] = updatedPublication
          }
          return { success: true }
        }
      }
      // Otherwise just add/switch to dislike (backend handles switching automatically)
      else {
        const response = await publicationService.dislike(id)
        if (response && response.data !== undefined) {
          const updatedPublication = response.data
          const index = publications.value.findIndex(p => p.id === id)
          if (index !== -1) {
            publications.value[index] = updatedPublication
          }
          return { success: true }
        }
      }
      return { success: false, error: '"Gefällt mir nicht" fehlgeschlagen' }
    } catch (err) {
      const errorMessage = err.message || '"Gefällt mir nicht" fehlgeschlagen'
      return { success: false, error: errorMessage }
    }
  }

  return {
    publications,
    currentPage,
    totalPages,
    loading,
    error,
    fetchPublications,
    likePublication,
    dislikePublication
  }
})
