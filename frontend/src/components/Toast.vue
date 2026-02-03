<template>
  <div class="toast-container">
    <div
      v-for="toast in toasts"
      :key="toast.id"
      :class="['toast', toast.type]"
    >
      <div class="toast-content">
        <div class="toast-title">{{ toast.title }}</div>
        <div class="toast-message">{{ toast.message }}</div>
      </div>

      <div class="toast-actions" v-if="toast.action">
        <span
          class="toast-cta"
          role="button"
          tabindex="0"
          @click="invokeAction(toast)"
          @keydown.enter.prevent="invokeAction(toast)"
          @keydown.space.prevent="invokeAction(toast)"
        >
          <svg class="cta-icon" viewBox="0 0 24 24" width="14" height="14" fill="none" aria-hidden="true">
            <path d="M13 5l7 7-7 7M5 12h14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span class="cta-text">{{ toast.action.label || 'Ansehen' }}</span>
        </span>
      </div>

      <button
        class="toast-close"
        @click="removeToast(toast.id)"
        :aria-label="`Schließen ${toast.title}`"
        :title="`Schließen ${toast.title}`"
        type="button"
      >
        <svg viewBox="0 0 24 24" width="14" height="14" aria-hidden="true" focusable="false">
          <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useToast } from '../composables/useToast'
import { useRouter } from 'vue-router'

const { toasts, removeToast } = useToast()
const router = useRouter()

const invokeAction = (toast) => {
  console.log('Invoking action:', toast.action)
  try {
    // Navigate to the specified href or default to publications listing
    router.push(toast.action?.href || '/publications')
  } finally {
    removeToast(toast.id)
  }
}
</script>
