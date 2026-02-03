<template>
  <div class="publications-list">
    <h1>Zitate</h1>

    <div v-if="publicationStore.loading" class="loading">Veröffentlichungen werden geladen...</div>

    <div v-else-if="publicationStore.publications.length === 0" class="empty-state">
      <svg class="empty-icon" width="64" height="64" viewBox="0 0 24 24" fill="none">
        <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <h2 class="empty-title">Noch keine Veröffentlichungen</h2>
      <p class="empty-description">Es wurden noch keine Zitate veröffentlicht. Schau später nochmal vorbei!</p>
      <router-link v-if="authStore.isAuthenticated" to="/submit" class="empty-action-button">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>Zitat einreichen</span>
      </router-link>
    </div>

    <div v-else class="publications-container">
      <div v-for="publication in publicationStore.publications" :key="publication.id" class="publication-card">
        <div class="publication-content-wrapper">
          <div class="avatar-section">
            <div class="publication-image">
              <img :src="getAvatarUrl(publication.saidBy)" :alt="getAuthorName(publication.saidBy)" />
            </div>
            <div class="quote-author">
              {{ getAuthorName(publication.saidBy) }}
            </div>
          </div>
          <div class="quote-text-content">
            <blockquote>
              {{ publication.content }}
            </blockquote>
            <div class="quote-author-date" v-if="publication.saidAt">
              {{ formatDate(publication.saidAt) }}
            </div>
            <div class="quote-footnote" v-if="publication.submittedBy && publication.submittedAt">
              Eingereicht von {{ publication.submittedBy.username }} am {{ formatDate(publication.submittedAt) }}
            </div>
            <div class="quote-footnote" v-else-if="publication.submittedAt">
              Eingereicht von Anonym am {{ formatDate(publication.submittedAt) }}
            </div>

            <!-- Interactive buttons for authenticated users -->
            <div class="inline-reactions" v-if="authStore.isAuthenticated">
              <div
                class="like-button"
                @click="handleLike(publication.id)"
                :class="{
                  disabled: reacting,
                  active: publication.selfReactionData?.reacted && publication.selfReactionData?.reactionType === 'LIKE'
                }"
                role="button"
                tabindex="0"
              >
                <svg class="icon-like" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M7 22V11M2 13V20C2 21.1046 2.89543 22 4 22H16.4262C17.907 22 19.1662 20.9197 19.3914 19.4562L20.4683 12.4562C20.7479 10.6389 19.3418 9 17.5032 9H14V4C14 2.89543 13.1046 2 12 2C11.4477 2 11 2.44772 11 3V3.56075C11 3.99174 10.8081 4.40081 10.4729 4.67668L7.58579 7.08579C7.22508 7.3824 7 7.83259 7 8.31034V11M7 11H4C2.89543 11 2 11.8954 2 13Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="reaction-count">{{ publication.likeCount || 0 }}</span>
              </div>
              <div
                class="dislike-button"
                @click="handleDislike(publication.id)"
                :class="{
                  disabled: reacting,
                  active: publication.selfReactionData?.reacted && publication.selfReactionData?.reactionType === 'DISLIKE'
                }"
                role="button"
                tabindex="0"
              >
                <svg class="icon-dislike" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 2V13M22 11V4C22 2.89543 21.1046 2 20 2H7.57377C6.09297 2 4.83384 3.08027 4.60859 4.54383L3.53166 11.5438C3.25207 13.3611 4.65825 15 6.49682 15H10V20C10 21.1046 10.8954 22 12 22C12.5523 22 13 21.5523 13 21V20.4392C13 20.0083 13.1919 19.5992 13.5271 19.3233L16.4142 16.9142C16.7749 16.6176 17 16.1674 17 15.6897V13M17 13H20C21.1046 13 22 12.1046 22 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="reaction-count">{{ publication.dislikeCount || 0 }}</span>
              </div>
            </div>

            <!-- Read-only counts for guests -->
            <div class="inline-reactions readonly" v-else>
              <div class="reaction-display">
                <svg class="icon-like" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M7 22V11M2 13V20C2 21.1046 2.89543 22 4 22H16.4262C17.907 22 19.1662 20.9197 19.3914 19.4562L20.4683 12.4562C20.7479 10.6389 19.3418 9 17.5032 9H14V4C14 2.89543 13.1046 2 12 2C11.4477 2 11 2.44772 11 3V3.56075C11 3.99174 10.8081 4.40081 10.4729 4.67668L7.58579 7.08579C7.22508 7.3824 7 7.83259 7 8.31034V11M7 11H4C2.89543 11 2 11.8954 2 13Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="reaction-count">{{ publication.likeCount || 0 }}</span>
              </div>
              <div class="reaction-display">
                <svg class="icon-dislike" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 2V13M22 11V4C22 2.89543 21.1046 2 20 2H7.57377C6.09297 2 4.83384 3.08027 4.60859 4.54383L3.53166 11.5438C3.25207 13.3611 4.65825 15 6.49682 15H10V20C10 21.1046 10.8954 22 12 22C12.5523 22 13 21.5523 13 21V20.4392C13 20.0083 13.1919 19.5992 13.5271 19.3233L16.4142 16.9142C16.7749 16.6176 17 16.1674 17 15.6897V13M17 13H20C21.1046 13 22 12.1046 22 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="reaction-count">{{ publication.dislikeCount || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="publicationStore.totalPages > 1" class="pagination">
      <button
        type="button"
        @click="loadPage(publicationStore.currentPage - 1)"
        :disabled="publicationStore.currentPage === 0"
        class="pagination-icon"
      >
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>

      <button
        type="button"
        @click="loadPage(publicationStore.currentPage + 1)"
        :disabled="publicationStore.currentPage >= publicationStore.totalPages - 1"
        class="pagination-icon"
      >
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, watch, ref } from 'vue'
import { usePublicationStore } from '../stores/publication'
import { useAuthStore } from '../stores/auth'
import { useToast } from '../composables/useToast'

const publicationStore = usePublicationStore()
const authStore = useAuthStore()
const { error } = useToast()
const reacting = ref(false)

onMounted(() => {
  publicationStore.fetchPublications()
})

watch(() => publicationStore.error, (newError) => {
  if (newError) {
    error(newError, 'Veröffentlichungen konnten nicht geladen werden')
  }
})

const loadPage = (page) => {
  publicationStore.fetchPublications(page)
}

const getPageNumbers = () => {
  const current = publicationStore.currentPage + 1
  const total = publicationStore.totalPages
  const pages = []

  if (total <= 7) {
    // Show all pages if 7 or fewer
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // Always show first page
    pages.push(1)

    if (current > 3) {
      pages.push('...')
    }

    // Show pages around current page
    const start = Math.max(2, current - 1)
    const end = Math.min(total - 1, current + 1)

    for (let i = start; i <= end; i++) {
      pages.push(i)
    }

    if (current < total - 2) {
      pages.push('...')
    }

    // Always show last page
    pages.push(total)
  }

  return pages
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('de-DE', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getAvatarUrl = (author) => {
  if (!author) return 'https://api.dicebear.com/7.x/avataaars/svg?seed=anonymous'
  const seed = `${author.firstName}-${author.lastName}`.toLowerCase()
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(seed)}`
}

const getAuthorName = (author) => {
  if (!author) return 'Anonym'
  return `${author.firstName} ${author.lastName}`
}

const handleLike = async (id) => {
  reacting.value = true
  const result = await publicationStore.likePublication(id)
  reacting.value = false

  if (!result.success) {
    error(result.error || 'Bewertung fehlgeschlagen', 'Bewertung fehlgeschlagen')
  }
}

const handleDislike = async (id) => {
  reacting.value = true
  const result = await publicationStore.dislikePublication(id)
  reacting.value = false

  if (!result.success) {
    error(result.error || 'Bewertung fehlgeschlagen', 'Bewertung fehlgeschlagen')
  }
}
</script>
