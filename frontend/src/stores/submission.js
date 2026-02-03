import { defineStore } from 'pinia'
import { ref } from 'vue'
import { submissionService } from '../services/submissionService'

export const useSubmissionStore = defineStore('submission', () => {
  const submissions = ref([])
  const currentPage = ref(0)
  const totalPages = ref(0)
  const loading = ref(false)
  const error = ref(null)

  async function fetchSubmissions(page = 0) {
    loading.value = true
    error.value = null
    try {
      const response = await submissionService.getAll(page)

      if (response && response.data) {
        const responseData = response.data

        if (responseData.content && Array.isArray(responseData.content)) {
          submissions.value = responseData.content

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
          submissions.value = []
        }
      }
    } catch (err) {
      // Use German error message from API interceptor
      error.value = err.message || 'Einreichungen konnten nicht geladen werden'
      console.error('Error fetching submissions:', err)
    } finally {
      loading.value = false
    }
  }

  async function createSubmission(submissionData) {
    loading.value = true
    error.value = null
    try {
      const response = await submissionService.create(submissionData)
      if (response && response.data !== undefined) {
        return { success: true }
      }
      return { success: false, error: 'Einreichung fehlgeschlagen' }
    } catch (err) {
      // Use German error message from API interceptor
      const errorMessage = err.message || 'Einreichung fehlgeschlagen'
      return { success: false, error: errorMessage }
    } finally {
      loading.value = false
    }
  }

  async function voteSubmission(id) {
    try {
      const response = await submissionService.vote(id)
      // response may be: axios response (with data), or body directly.
      // Normalize to rawBody which should contain the updated submission or nested 'data'
      const raw = (response && response.data !== undefined) ? response.data : response

      // The actual updated submission might live directly in raw or under raw.data/raw.body
      const updatedSubmission = raw && raw.data ? raw.data : (raw && raw.body ? raw.body : raw)

      // Extract published flag robustly
      const published = !!(
        (updatedSubmission && updatedSubmission.published) ||
        (raw && raw.published) ||
        (raw && raw.data && raw.data.published) ||
        (raw && raw.body && raw.body.published) ||
        (raw && raw.submissionPublishingStatus && raw.submissionPublishingStatus.published) ||
        (raw && raw.data && raw.data.submissionPublishingStatus && raw.data.submissionPublishingStatus.published) ||
        (raw && raw.body && raw.body.submissionPublishingStatus && raw.body.submissionPublishingStatus.published)
      )

      if (updatedSubmission) {
        const index = submissions.value.findIndex(s => s.id === id)
        if (index !== -1) {
          if (published) {
            submissions.value.splice(index, 1)
          } else {
            submissions.value[index] = updatedSubmission
          }
        }
        return { success: true, published, data: raw }
      }
      return { success: false, error: 'Abstimmung fehlgeschlagen' }
    } catch (err) {
      const errorMessage = err.message || 'Abstimmung fehlgeschlagen'
      return { success: false, error: errorMessage }
    }
  }

  return {
    submissions,
    currentPage,
    totalPages,
    loading,
    error,
    fetchSubmissions,
    createSubmission,
    voteSubmission
  }
})
