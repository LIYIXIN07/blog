<template>
  <div v-if="headings.length" class="nblog-widget m-box nblog-toc-widget">
    <div class="nblog-widget-header nblog-toc-header">
      <el-icon><List /></el-icon>
      本文目录
    </div>
    <nav class="nblog-toc-body">
      <a
        v-for="item in headings"
        :key="item.id"
        :href="`#${item.id}`"
        class="nblog-toc-link"
        :class="{ active: item.id === activeId }"
        :style="{ paddingLeft: `${8 + (item.level - 1) * 14}px` }"
        @click.prevent="scrollToHeading(item.id)"
      >
        {{ item.text }}
      </a>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { List } from '@element-plus/icons-vue'
import { useArticleTocStore } from '@/stores/articleToc'

const tocStore = useArticleTocStore()
const { headings, activeId } = storeToRefs(tocStore)

const scrollToHeading = (id: string) => {
  const el = document.getElementById(id)
  if (!el) return
  const top = el.getBoundingClientRect().top + window.scrollY - 72
  window.scrollTo({ top, behavior: 'smooth' })
  tocStore.setActiveId(id)
}
</script>

<style scoped>
.nblog-toc-widget {
  position: sticky;
  top: 72px;
  max-height: calc(100vh - 96px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.nblog-toc-widget.nblog-article-toc-mobile {
  position: static;
  max-height: none;
  top: auto;
}

.nblog-toc-header {
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  background: rgba(0, 0, 0, 0.03);
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 8px;
  border-top: 3px solid #ffc107;
  flex-shrink: 0;
}

.nblog-toc-body {
  padding: 10px 12px 14px;
  overflow-y: auto;
}

.nblog-toc-link {
  display: block;
  padding: 6px 0;
  font-size: 13px;
  line-height: 1.5;
  color: #666;
  text-decoration: none;
  border-bottom: 1px dashed #f0f0f0;
  transition: color 0.2s;
}

.nblog-toc-link:last-child {
  border-bottom: none;
}

.nblog-toc-link:hover,
.nblog-toc-link.active {
  color: #ffc107;
}
</style>
