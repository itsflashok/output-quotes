import { defineStore } from 'pinia'
import { ref } from 'vue'
import { quoteService } from '../services/quoteService'

export const useQuoteStore = defineStore('quote', () => {
  const quotes = ref([])
  const currentPage = ref(0)
  const totalPages = ref(0)
  const loading = ref(false)
  const error = ref(null)

  async function fetchQuotes(page = 0) {
    loading.value = true
    error.value = null
    try {
      const response = await quoteService.getAll(page)

      if (response && response.data) {
        const responseData = response.data

        if (responseData.content && Array.isArray(responseData.content)) {
          quotes.value = responseData.content

          // Handle pagination from nested 'page' object
          if (responseData.page) {
            currentPage.value = responseData.page.number || 0
            totalPages.value = responseData.page.totalPages || 1
          } else {
            // Fallback for old format (flat structure)
            currentPage.value = responseData.number || 0
            totalPages.value = responseData.totalPages || 1
          }
        } else {
          console.error('No content array found in response:', responseData)
          quotes.value = []
        }
      } else {
        console.error('No data in response:', response)
        quotes.value = []
      }
    } catch (err) {
      // Use German error message from API interceptor
      error.value = err.message || 'Zitate konnten nicht geladen werden'
      console.error('Error fetching quotes:', err)
    } finally {
      loading.value = false
    }
  }

  async function submitQuote(quoteData) {
    loading.value = true
    error.value = null
    try {
      const response = await quoteService.submitQuote(quoteData)

      // Normalize response: quoteService may return axios response.data (body) or the body directly
      const raw = (response && response.data !== undefined) ? response.data : response

      if (raw) {
        // published might be in different places depending on backend wrapper
        const published = !!(
          raw.published ||
          (raw.data && raw.data.published) ||
          (raw.body && raw.body.published)
        )

        return { success: true, published, data: raw }
      }

      return { success: false, error: 'Zitat-Einreichung fehlgeschlagen' }
    } catch (err) {
      // Use German error message from API interceptor
      const errorMessage = err.message || 'Zitat-Einreichung fehlgeschlagen'
      return { success: false, error: errorMessage }
    } finally {
      loading.value = false
    }
  }

  return {
    quotes,
    currentPage,
    totalPages,
    loading,
    error,
    fetchQuotes,
    submitQuote
  }
})
