<template>
  <aside class="nblog-right">
    <VisitorMap />

    <div class="nblog-widget m-box">
      <div class="nblog-widget-header nblog-widget-header-yellow">
        <el-icon><Collection /></el-icon>随机文章
      </div>
      <div class="nblog-widget-body nblog-random-body">
        <router-link
          v-for="article in randomArticles"
          :key="article.id"
          :to="`/article/${article.id}`"
          class="nblog-random-item"
        >
          <div
            class="nblog-random-img"
            :style="{ backgroundImage: article.coverImage ? `url(${article.coverImage})` : undefined }"
          />
          <div class="nblog-random-info">
            <div class="nblog-random-date">{{ formatDate(article.createdAt) }}</div>
            <div class="nblog-random-title">{{ article.title }}</div>
          </div>
        </router-link>
        <div v-if="!randomArticles.length" class="nblog-muted">暂无文章</div>
      </div>
    </div>

    <TagCloud />

    <div class="nblog-widget m-box">
      <div class="nblog-widget-header">
        <el-icon><FolderOpened /></el-icon>文章归档
      </div>
      <div class="nblog-widget-body">
        <router-link
          v-for="item in archives"
          :key="`${item.year}-${item.month}`"
          :to="`/archives/${item.year}/${item.month}`"
          class="nblog-list-link"
        >
          {{ item.label }} ({{ item.count }})
        </router-link>
        <div v-if="!archives.length" class="nblog-muted">暂无归档</div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Collection, FolderOpened } from '@element-plus/icons-vue'
import TagCloud from '@/components/blog/TagCloud.vue'
import VisitorMap from '@/components/blog/VisitorMap.vue'
import { getRandomArticles, getArchives } from '@/api/article'
import { formatDate } from '@/utils/date'
import type { ArchiveStat, ArticleListItem } from '@/types'

const randomArticles = ref<ArticleListItem[]>([])
const archives = ref<ArchiveStat[]>([])

onMounted(async () => {
  try {
    randomArticles.value = await getRandomArticles(5)
  } catch {
    randomArticles.value = []
  }

  try {
    archives.value = await getArchives()
  } catch {
    archives.value = []
  }
})
</script>

<style scoped>
.nblog-widget {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
  overflow: hidden;
}

.nblog-widget-header {
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  background: rgba(0, 0, 0, 0.03);
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nblog-widget-header-yellow {
  background: #fff8e1;
}

.nblog-widget-body {
  padding: 8px 14px 14px;
}

.nblog-random-body {
  background: #ffecb3;
  padding: 12px;
}

.nblog-random-item {
  display: block;
  position: relative;
  height: 7rem;
  border-radius: 5px;
  overflow: hidden;
  margin-top: 12px;
  cursor: pointer;
}

.nblog-random-item:first-child {
  margin-top: 0;
}

.nblog-random-img {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #48dbfb, #409eff);
  background-size: cover;
  background-position: center;
}

.nblog-random-info {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  padding: 8px;
  background: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.8));
  color: #fff;
  font-size: 12px;
}

.nblog-random-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nblog-list-link {
  display: block;
  padding: 8px 0;
  color: #666;
  font-size: 13px;
  border-bottom: 1px dashed #eee;
  transition: color 0.2s;
}

.nblog-list-link:last-child {
  border-bottom: none;
}

.nblog-list-link:hover {
  color: #48dbfb;
}

.nblog-muted {
  font-size: 13px;
  color: #999;
  padding: 8px 0;
}
</style>
