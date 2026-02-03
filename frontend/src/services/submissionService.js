import api from './api'

export const submissionService = {
  async getAll(page = 0) {
    const response = await api.get('/submissions', {
      params: { page }
    })
    return response.data
  },

  async create(submissionData) {
    const response = await api.post('/submissions', submissionData)
    return response.data
  },

  async vote(id) {
    const response = await api.post(`/submissions/${id}/vote`)
    // Log raw response for debugging publication flag
    try {
      console.debug('submissionService.vote raw response:', response)
    } catch (err) {
      // ignore
    }
    return response.data
  }
}
