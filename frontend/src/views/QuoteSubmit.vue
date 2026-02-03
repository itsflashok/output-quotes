<template>
  <div class="quote-submit">
      <h1>Zitat einreichen</h1>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="content">Zitatinhalt:</label>
        <textarea
          id="content"
          v-model="formData.content"
          required
          maxlength="1000"
          rows="5"
        ></textarea>
      </div>

      <div class="form-group">
        <label for="author">Autor:</label>
        <CustomSelect
          v-model="formData.saidBy"
          :options="authorOptions"
          placeholder="Autor auswählen"
          :disabled="authorStore.loading"
        />
      </div>

      <div class="form-group">
        <label for="saidAt">Gesagt am:</label>
        <input
          type="datetime-local"
          id="saidAt"
          v-model="formData.saidAt"
          required
          :max="maxDate"
        />
      </div>

      <div class="form-group">
        <CustomCheckbox v-model="formData.authorHidden">
          Autor anonym anzeigen
        </CustomCheckbox>
      </div>

      <div class="form-group">
        <CustomCheckbox v-model="formData.submitterHidden">
          Einreicher anonym anzeigen
        </CustomCheckbox>
      </div>

      <button type="submit" :disabled="quoteStore.loading || authorStore.loading">
        {{ quoteStore.loading ? 'Wird eingereicht...' : 'Zitat einreichen' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useQuoteStore } from '../stores/quote'
import { useAuthorStore } from '../stores/author'
import { useToast } from '../composables/useToast'
import CustomSelect from '../components/CustomSelect.vue'
import CustomCheckbox from '../components/CustomCheckbox.vue'

const router = useRouter()
const quoteStore = useQuoteStore()
const authorStore = useAuthorStore()
const { error, success } = useToast()

const formData = ref({
  content: '',
  saidBy: '',
  saidAt: '',
  authorHidden: false,
  submitterHidden: false
})

const maxDate = computed(() => {
  const now = new Date()
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset())
  return now.toISOString().slice(0, 16)
})

const authorOptions = computed(() => {
  return authorStore.authors.map(author => ({
    value: author.id,
    label: `${author.firstName} ${author.lastName}`
  }))
})

onMounted(() => {
  authorStore.fetchAuthors()
})

watch(() => authorStore.error, (newError) => {
  if (newError) {
    error(newError, 'Autoren konnten nicht geladen werden')
  }
})

const handleSubmit = async () => {
  const result = await quoteStore.submitQuote(formData.value)

  if (result.success) {
    if (result.published) {
      success('Zitat wurde erfolgreich eingereicht und direkt veröffentlicht!')
    } else {
      success('Zitat erfolgreich eingereicht!')
    }

    formData.value = {
      content: '',
      saidBy: '',
      saidAt: '',
      authorHidden: false,
      submitterHidden: false
    }
    setTimeout(() => {
      router.push('/')
    }, 1500)
  } else {
    error(result.error || 'Einreichung des Zitats fehlgeschlagen', 'Einreichung fehlgeschlagen')
  }
}

</script>
