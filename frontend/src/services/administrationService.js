import api from './api'

export const administrationService = {
  async createAuthor(authorData) {
    const response = await api.post('/administration/author', authorData)
    return response.data
  }
}
