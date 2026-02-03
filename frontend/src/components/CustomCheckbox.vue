<template>
  <label class="custom-checkbox" :class="{ checked: modelValue }">
    <input
      type="checkbox"
      :checked="modelValue"
      @change="$emit('update:modelValue', $event.target.checked)"
      class="checkbox-input"
    />
    <span class="checkbox-box">
      <svg v-if="modelValue" class="checkmark" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M5 13l4 4L19 7" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </span>
    <span class="checkbox-label">
      <slot></slot>
    </span>
  </label>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.custom-checkbox {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s ease;
}

.custom-checkbox:hover .checkbox-box {
  border-color: #6b7280;
  background: #f9fafb;
}

.checkbox-input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
  pointer-events: none;
}

.checkbox-box {
  width: 20px;
  height: 20px;
  min-width: 20px;
  min-height: 20px;
  border: 2px solid #d1d5db;
  border-radius: 6px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.custom-checkbox.checked .checkbox-box {
  background: #4b5563;
  border-color: #4b5563;
}

.checkmark {
  width: 14px;
  height: 14px;
  color: white;
  animation: checkmarkAppear 0.2s ease;
  position: absolute;
}

@keyframes checkmarkAppear {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.checkbox-label {
  color: #374151;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
}

.custom-checkbox:hover .checkbox-label {
  color: #1f2937;
}
</style>
