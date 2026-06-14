<template>
  <div>
    <div class="nblog-page-title m-box">文章归档</div>
    <div class="nblog-panel m-box">
      <router-link
        v-for="item in archives"
        :key="`${item.year}-${item.month}`"
        :to="`/archives/${item.year}/${item.month}`"
        class="nblog-archive-link"
      >
        <span>{{ item.label }}</span>
        <span>{{ item.count }} 篇</span>
      </router-link>
      <div v-if="!archives.length" class="nblog-empty">暂无归档</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getArchives } from '@/api/article'
import type { ArchiveStat } from '@/types'

const archives = ref<ArchiveStat[]>([])

onMounted(async () => {
  try {
    archives.value = await getArchives()
  } catch {
    archives.value = []
  }
})
</script>

<style scoped>
.nblog-page-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  padding: 16px 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
  border-top: 3px solid #48dbfb;
}

.nblog-panel {
  background: #fff;
  border-radius: 4px;
  padding: 8px 20px 16px;
}

.nblog-archive-link {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  color: #666;
  font-size: 14px;
  border-bottom: 1px dashed #eee;
  transition: color 0.2s;
}

.nblog-archive-link:last-child {
  border-bottom: none;
}

.nblog-archive-link:hover {
  color: #48dbfb;
}

.nblog-empty {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}
</style>
