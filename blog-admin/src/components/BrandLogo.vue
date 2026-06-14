<template>
  <div class="brand-logo" :style="rootStyle" :title="title">
    <div class="brand-logo-ring">
      <img :src="src" :alt="title" class="brand-logo-img" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  size?: number
  src?: string
  title?: string
}>(), {
  size: 36,
  src: '/images/author-avatar.png',
  title: 'DollMeowOnly'
})

const rootStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`
}))
</script>

<style scoped>
.brand-logo {
  flex-shrink: 0;
}

.brand-logo-ring {
  width: 100%;
  height: 100%;
  padding: 2px;
  border-radius: 10px;
  background: linear-gradient(135deg, #93c5fd 0%, #818cf8 45%, #c4b5fd 100%);
  box-shadow:
    0 4px 14px rgba(99, 102, 241, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.25);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.brand-logo:hover .brand-logo-ring {
  transform: translateY(-1px);
  box-shadow:
    0 6px 18px rgba(99, 102, 241, 0.42),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.brand-logo-img {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  object-fit: cover;
  display: block;
  background: #1f2937;
}
</style>
