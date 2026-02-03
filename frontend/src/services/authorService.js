import api from './api'

export const authorService = {
  async getAll() {
    const response = await api.get('/authors')
    return response.data
  },

  async getById(id) {
    const response = await api.get(`/authors/${id}`)
    return response.data
  }
}
