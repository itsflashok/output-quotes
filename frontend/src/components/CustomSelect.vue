<template>
  <div class="custom-select" ref="selectRef">
    <div
      class="select-trigger"
      @click="toggleDropdown"
      :class="{ 'open': isOpen, 'disabled': disabled }"
    >
      <span class="select-value" :class="{ 'placeholder': !selectedOption }">
        {{ selectedOption ? selectedOption.label : placeholder }}
      </span>
      <svg class="select-arrow" :class="{ 'rotate': isOpen }" width="20" height="20" viewBox="0 0 20 20" fill="none">
        <path d="M5 7.5L10 12.5L15 7.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </div>

    <transition name="dropdown">
      <div v-if="isOpen" class="select-dropdown">
        <div
          v-for="option in options"
          :key="option.value"
          class="select-option"
          :class="{ 'selected': option.value === modelValue }"
          @click="selectOption(option)"
        >
          {{ option.label }}
        </div>
        <div v-if="options.length === 0" class="select-empty">
          Keine Optionen verfügbar
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  options: {
    type: Array,
    required: true,
    // Array of { value, label }
  },
  placeholder: {
    type: String,
    default: 'Bitte auswählen...'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const isOpen = ref(false)
const selectRef = ref(null)

const selectedOption = computed(() => {
  return props.options.find(opt => opt.value === props.modelValue)
})

const toggleDropdown = () => {
  if (!props.disabled) {
    isOpen.value = !isOpen.value
  }
}

const selectOption = (option) => {
  emit('update:modelValue', option.value)
  isOpen.value = false
}

const handleClickOutside = (event) => {
  if (selectRef.value && !selectRef.value.contains(event.target)) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.custom-select {
  position: relative;
  width: 100%;
}

.select-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.85rem 1rem;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  min-height: 44px;
}

.select-trigger:hover:not(.disabled) {
  border-color: #9ca3af;
}

.select-trigger.open {
  border-color: #6b7280;
  box-shadow: 0 0 0 3px rgba(107, 114, 128, 0.1);
}

.select-trigger.disabled {
  background: #f3f4f6;
  cursor: not-allowed;
  opacity: 0.6;
}

.select-value {
  flex: 1;
  font-size: 15px;
  color: #374151;
  font-weight: 500;
}

.select-value.placeholder {
  color: #9ca3af;
  font-weight: 400;
}

.select-arrow {
  color: #6b7280;
  transition: transform 0.2s ease;
  flex-shrink: 0;
  margin-left: 0.5rem;
}

.select-arrow.rotate {
  transform: rotate(180deg);
}

.select-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-height: 250px;
  overflow-y: auto;
  z-index: 1000;
}

.select-option {
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background 0.15s ease;
  font-size: 15px;
  color: #374151;
}

.select-option:hover {
  background: #f9fafb;
}

.select-option.selected {
  background: #f3f4f6;
  font-weight: 600;
  color: #1f2937;
}

.select-empty {
  padding: 0.75rem 1rem;
  color: #9ca3af;
  font-size: 14px;
  text-align: center;
}

/* Dropdown transition */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* Custom scrollbar */
.select-dropdown::-webkit-scrollbar {
  width: 6px;
}

.select-dropdown::-webkit-scrollbar-track {
  background: #f9fafb;
  border-radius: 8px;
}

.select-dropdown::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 8px;
}

.select-dropdown::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>
