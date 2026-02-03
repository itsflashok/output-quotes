import api from './api'

export const quoteService = {
  async getAll(page = 0) {
    const response = await api.get('/quotes', {
      params: { page }
    })
    return response.data
  },

  async submitQuote(quoteData) {
    const response = await api.post('/submissions', quoteData)
    return response.data
  }
}
