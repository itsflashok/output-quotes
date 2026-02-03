import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// German translations for backend error codes
const errorCodeTranslations = {
  // Authentication errors
  'INVALID_CREDENTIALS': 'Ungültige Anmeldedaten - Benutzername oder Passwort falsch',
  'USERNAME_ALREADY_TAKEN': 'Dieser Benutzername ist bereits vergeben',
  'UNAUTHORIZED': 'Nicht autorisiert - Bitte melden Sie sich an',
  'FORBIDDEN': 'Zugriff verweigert - Sie haben keine Berechtigung für diese Aktion',

  // Submission errors
  'SUBMISSION_NOT_FOUND': 'Die angegebene Einreichung wurde nicht gefunden',
  'ALREADY_VOTED': 'Sie haben bereits für diese Einreichung abgestimmt',
  'AUTHOR_NOT_FOUND': 'Der angegebene Autor wurde nicht gefunden',
  'INVALID_SAID_AT': 'Das Datum liegt in der Zukunft - Es muss in der Vergangenheit liegen',

  // Validation errors
  'INVALID_VALUE': 'Ungültiger Wert - Bitte überprüfen Sie Ihre Eingabe',
  'MALFORMED_REQUEST': 'Fehlerhafte Anfrage - Die Daten konnten nicht verarbeitet werden',

  // General errors
  'NOT_FOUND': 'Ressource nicht gefunden',
  'METHOD_NOT_ALLOWED': 'HTTP-Methode nicht erlaubt',
  'INTERNAL_SERVER_ERROR': 'Interner Serverfehler - Bitte versuchen Sie es später erneut'
}

// German translations for HTTP error codes (fallback)
const getErrorMessage = (status) => {
  switch (status) {
    case 400:
      return 'Ungültige Anfrage'
    case 401:
      return 'Nicht autorisiert - Bitte melden Sie sich an'
    case 403:
      return 'Zugriff verweigert - Sie haben keine Berechtigung für diese Aktion'
    case 404:
      return 'Ressource nicht gefunden'
    case 409:
      return 'Konflikt - Die Anfrage konnte nicht verarbeitet werden'
    case 422:
      return 'Validierungsfehler'
    case 500:
      return 'Interner Serverfehler'
    case 502:
      return 'Server nicht erreichbar'
    case 503:
      return 'Service vorübergehend nicht verfügbar'
    default:
      return `Fehler ${status}: Die Anfrage ist fehlgeschlagen`
  }
}

// Response interceptor for handling errors
api.interceptors.response.use(
  response => response,
  error => {
    // Try to extract backend error code and map to German
    const backendErrors = error.response?.data?.errors || []
    if (backendErrors.length > 0) {
      const firstError = backendErrors[0]
      const errorCode = firstError.code

      // Use mapped German message if available, otherwise use HTTP status fallback
      if (errorCode && errorCodeTranslations[errorCode]) {
        error.message = errorCodeTranslations[errorCode]
      } else if (error.response?.status) {
        // Fallback to HTTP status translation
        error.message = getErrorMessage(error.response.status)
      }
    } else if (error.response?.status) {
      // No backend errors, use HTTP status translation
      error.message = getErrorMessage(error.response.status)
    }

    // Handle authentication errors (401 Unauthorized, 403 Forbidden)
    if (error.response?.status === 401 || error.response?.status === 403) {
      // Clear localStorage when session is invalid (except for auth endpoints)
      const url = error.config?.url || ''
      const isAuthEndpoint = url.includes('/authentication/login') ||
                            url.includes('/authentication/register') ||
                            url.includes('/authentication/logout') ||
                            url.includes('/authentication/me')

      if (!isAuthEndpoint) {
        // Clear localStorage so UI updates to show logged out state
        localStorage.removeItem('user')
        // Dispatch custom event so auth store can update immediately
        window.dispatchEvent(new CustomEvent('auth-cleared'))
        // Don't redirect - just let the error propagate so views can show toasts
      }
    }
    return Promise.reject(error)
  }
)

export default api
