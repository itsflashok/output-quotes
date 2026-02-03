<template>
  <div class="submissions-list">
    <h1>Ausstehende Einreichungen</h1>

    <div v-if="submissionStore.loading" class="loading">Einreichungen werden geladen...</div>

    <div v-else-if="submissionStore.submissions.length === 0">
      Noch keine Einreichungen verfügbar.
    </div>

    <div v-else class="submissions-container">
      <div v-for="submission in submissionStore.submissions" :key="submission.id" class="submission-card">
        <div class="submission-content-wrapper">
          <div class="avatar-section">
            <div class="submission-image">
              <img :src="getAvatarUrl(submission.saidBy)" :alt="getAuthorName(submission.saidBy)" />
            </div>
            <div class="quote-author">
              {{ getAuthorName(submission.saidBy) }}
            </div>
          </div>
          <div class="quote-text-content">
            <blockquote>
              {{ submission.content }}
            </blockquote>
            <div class="quote-meta" :class="{ 'has-date': submission.saidAt, 'has-submitter': submission.submittedAt }">
              <div class="quote-author-date" v-if="submission.saidAt">
                {{ formatDate(submission.saidAt) }}
              </div>
              <div class="quote-footnote" v-if="submission.submittedBy && submission.submittedAt">
                Eingereicht von {{ submission.submittedBy.username }} am {{ formatDate(submission.submittedAt) }}
              </div>
              <div class="quote-footnote" v-else-if="submission.submittedAt">
                Eingereicht von Anonym am {{ formatDate(submission.submittedAt) }}
              </div>
            </div>
          </div>
        </div>

        <div class="vote-section">
          <div class="vote-progress">
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: getVotePercentage(submission.voteCount, submission.voteGoal) + '%' }"
              ></div>
            </div>
            <span class="vote-count">{{ submission.voteCount }} / {{ submission.voteGoal || 10 }} Stimmen</span>
          </div>
           <div
            class="vote-button"
            @click="handleVote(submission.id, submission.selfVoted)"
            :class="{ disabled: voting || submission.selfVoted, 'already-voted': submission.selfVoted }"
            role="button"
            :tabindex="submission.selfVoted ? -1 : 0"
            :title="submission.selfVoted ? 'Du hast bereits abgestimmt' : 'Abstimmen'"
          >
            <svg v-if="!submission.selfVoted" class="icon-vote" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 4L4 12H8V20H16V12H20L12 4Z" fill="currentColor"/>
            </svg>
            <svg v-else class="icon-check" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z" fill="currentColor"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <div v-if="submissionStore.totalPages > 1" class="pagination">
      <button
        type="button"
        @click="loadPage(submissionStore.currentPage - 1)"
        :disabled="submissionStore.currentPage === 0 || submissionStore.loading"
        class="pagination-icon"
      >
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>

      <button
        type="button"
        @click="loadPage(submissionStore.currentPage + 1)"
        :disabled="submissionStore.currentPage >= submissionStore.totalPages - 1 || submissionStore.loading"
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
import { useSubmissionStore } from '../stores/submission'
import { useToast } from '../composables/useToast'

const submissionStore = useSubmissionStore()
const { error, success } = useToast()
const voting = ref(false)

onMounted(() => {
  submissionStore.fetchSubmissions()
})

watch(() => submissionStore.error, (newError) => {
  if (newError) {
    error(newError, 'Einreichungen konnten nicht geladen werden')
  }
})

const loadPage = (page) => {
  if (submissionStore.loading) return
  submissionStore.fetchSubmissions(page)
}

const getPageNumbers = () => {
  const current = submissionStore.currentPage + 1
  const total = submissionStore.totalPages
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

const getVotePercentage = (voteCount, voteGoal) => {
  const goal = voteGoal || 10 // Standardwert 10, falls nicht angegeben
  return Math.min((voteCount / goal) * 100, 100)
}

const getAvatarUrl = (author) => {
  if (!author) {
    return 'https://api.dicebear.com/7.x/avataaars/svg?seed=anonymous';
  }
  const seed = `${author.firstName}-${author.lastName}`.toLowerCase();
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(seed)}`;
}

const getAuthorName = (author) => {
  if (!author) return 'Anonym'
  return `${author.firstName} ${author.lastName}`
}

const handleVote = async (id, selfVoted) => {
  if (voting.value || selfVoted) return

  voting.value = true
  const result = await submissionStore.voteSubmission(id)
  voting.value = false

  if (result.success) {
    if (result.published) {
      console.log('Submission published, pubId:', result.data?.submissionPublishingStatus?.publicationId)
      // If backend returned a publication id, navigate to it; otherwise go to publications list
      const href = '/publications'
      success('Diese Einreichung wurde nach deiner Abstimmung veröffentlicht.', 'Erfolg', 5000, { label: 'Ansehen', href })
    } else {
      success('Stimme erfolgreich abgegeben!')
    }
  } else {
    error(result.error || 'Abstimmung fehlgeschlagen', 'Abstimmung fehlgeschlagen')
  }
}
</script>
