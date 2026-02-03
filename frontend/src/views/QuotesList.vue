<template>
  <div class="quotes-list">
    <h1>Zitate</h1>

    <div v-if="quoteStore.loading" class="loading">Zitate werden geladen...</div>

    <div v-else-if="quoteStore.quotes.length === 0">
      Noch keine Zitate verfügbar.
    </div>

    <div v-else class="quotes-container">
      <div v-for="quote in quoteStore.quotes" :key="quote.id" class="quote-card">
        <blockquote>
          "{{ quote.content }}"
        </blockquote>
        <div class="quote-meta">
          <span class="author">— {{ quote.saidBy.firstName }} {{ quote.saidBy.lastName }}</span>
          <span class="date">{{ formatDate(quote.saidAt) }}</span>
        </div>
      </div>
    </div>

    <div v-if="quoteStore.totalPages > 1" class="pagination">
      <button
        type="button"
        @click="loadPage(quoteStore.currentPage - 1)"
        :disabled="quoteStore.currentPage === 0"
      >
        Zurück
      </button>
      <span>Seite {{ quoteStore.currentPage + 1 }} von {{ quoteStore.totalPages }}</span>
      <button
        type="button"
        @click="loadPage(quoteStore.currentPage + 1)"
        :disabled="quoteStore.currentPage >= quoteStore.totalPages - 1"
      >
        Weiter
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, watch } from 'vue'
import { useQuoteStore } from '../stores/quote'
import { useToast } from '../composables/useToast'

const quoteStore = useQuoteStore()
const { error } = useToast()

onMounted(() => {
  quoteStore.fetchQuotes()
})


watch(() => quoteStore.error, (newError) => {
  if (newError) {
    error(newError, 'Zitate konnten nicht geladen werden')
  }
})

const loadPage = (page) => {
  console.log('Seite wird geladen:', page)
  quoteStore.fetchQuotes(page)
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('de-DE', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>
