import api from './api'

export const authService = {
  async login(username, password) {
    const response = await api.post('/authentication/login', {
      username,
      password
    })
    return response.data
  },

  async register(username, password) {
    const response = await api.post('/authentication/register', {
      username,
      password
    })
    return response.data
  },

  async logout() {
    const response = await api.post('/authentication/logout')
    return response.data
  },

  async getCurrentUser() {
    const response = await api.get('/authentication/me')
    return response.data
  }
}
