<template>
  <div class="markdown-body" v-html="renderedContent"></div>
</template>

<script setup lang="ts">
import { computed, onMounted, nextTick } from 'vue'
import { parseMarkdown } from '@/utils/markdown'

const props = defineProps<{
  content: string
}>()

const renderedContent = computed(() => {
  return parseMarkdown(props.content)
})

onMounted(() => {
  nextTick(() => {
    addCopyButtons()
  })
})

function addCopyButtons() {
  const codeBlocks = document.querySelectorAll('pre')
  codeBlocks.forEach((block) => {
    const copyBtn = document.createElement('button')
    copyBtn.className = 'copy-btn absolute top-2 right-2 px-2 py-1 bg-gray-700 text-white text-xs rounded hover:bg-gray-600 transition-colors'
    copyBtn.textContent = '复制'
    copyBtn.onclick = () => {
      const code = block.querySelector('code')?.textContent || ''
      navigator.clipboard.writeText(code).then(() => {
        copyBtn.textContent = '已复制'
        setTimeout(() => {
          copyBtn.textContent = '复制'
        }, 2000)
      })
    }
    block.style.position = 'relative'
    block.appendChild(copyBtn)
  })
}
</script>

<style scoped>
.markdown-body pre {
  position: relative;
}
</style>