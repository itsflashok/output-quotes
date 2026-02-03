import { ref } from 'vue'

const toasts = ref([])
let nextId = 0

export function useToast() {
  const addToast = (message, type = 'error', title = null, duration = 5000, action = null) => {
    const id = nextId++
    const toast = {
      id,
      message,
      type,
      title: title || getDefaultTitle(type),
      action // optional { label: string, onClick: function }
    }

    toasts.value.push(toast)

    if (duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, duration)
    }

    return id
  }

  const removeToast = (id) => {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  const getDefaultTitle = (type) => {
    switch (type) {
      case 'error':
        return 'Fehler'
      case 'success':
        return 'Erfolg'
      case 'warning':
        return 'Warnung'
      default:
        return 'Benachrichtigung'
    }
  }

  const error = (message, title = 'Fehler', duration = 5000, action = null) => {
    return addToast(message, 'error', title, duration, action)
  }

  const success = (message, title = 'Erfolg', duration = 3000, action = null) => {
    return addToast(message, 'success', title, duration, action)
  }

  const warning = (message, title = 'Warnung', duration = 4000, action = null) => {
    return addToast(message, 'warning', title, duration, action)
  }

  return {
    toasts,
    addToast,
    removeToast,
    error,
    success,
    warning
  }
}
