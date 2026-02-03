import api from './api'

export const publicationService = {
  async getAll(page = 0) {
    const response = await api.get('/publications', {
      params: { page }
    })
    return response.data
  },

  async like(id) {
    const response = await api.post(`/publications/${id}/react`, {
      reactionType: 'LIKE'
    })
    return response.data
  },

  async dislike(id) {
    const response = await api.post(`/publications/${id}/react`, {
      reactionType: 'DISLIKE'
    })
    return response.data
  },

  async removeReaction(id) {
    const response = await api.delete(`/publications/${id}/react`)
    return response.data
  }
}
