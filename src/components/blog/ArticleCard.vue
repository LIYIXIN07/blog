<template>
  <article class="nblog-item m-box">
    <div v-if="article.isTop" class="nblog-item-top">
      <el-icon><Top /></el-icon>
    </div>

    <h2 class="nblog-item-title m-scaleup">
      <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
    </h2>

    <div class="nblog-item-meta">
      <span class="meta-date">
        <el-icon><Calendar /></el-icon>{{ formatDate(article.createdAt) }}
      </span>
      <span class="meta-views">
        <el-icon><View /></el-icon>{{ article.viewCount || 0 }}
      </span>
      <span class="meta-author">
        <el-icon><EditPen /></el-icon>{{ article.author || '博主' }}
      </span>
    </div>

    <router-link
      v-if="article.categoryName"
      :to="`/category`"
      class="nblog-item-category"
    >
      <el-icon><FolderOpened /></el-icon>{{ article.categoryName }}
    </router-link>

    <p v-if="article.summary" class="nblog-item-summary">{{ article.summary }}</p>

    <div class="nblog-item-action">
      <router-link :to="`/article/${article.id}`" class="color-btn">阅读全文</router-link>
    </div>

    <div class="nblog-item-divider" />

    <div v-if="article.tags?.length" class="nblog-item-tags">
      <router-link
        v-for="tag in article.tags"
        :key="tag.id"
        :to="`/tag/${tag.id}`"
        class="nblog-tag"
        :style="{ background: `${tag.color || '#48dbfb'}22`, color: tag.color || '#48dbfb' }"
      >
        {{ tag.name }}
      </router-link>
    </div>
  </article>
</template>

<script setup lang="ts">
import { Calendar, View, EditPen, FolderOpened, Top } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import type { ArticleListItem } from '@/types'

defineProps<{
  article: ArticleListItem
}>()
</script>

<style scoped>
.nblog-item {
  position: relative;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
  padding: 28px 24px 20px;
  margin-bottom: 24px;
  overflow: hidden;
}

.nblog-item-top {
  position: absolute;
  top: 0;
  right: 0;
  width: 48px;
  height: 48px;
  background: #db2828;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  clip-path: polygon(100% 0, 0 0, 100% 100%);
  padding: 4px 4px 0 0;
}

.nblog-item-title {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 16px;
}

.nblog-item-title a {
  color: #333;
  transition: color 0.2s;
}

.nblog-item-title a:hover {
  color: #48dbfb;
}

.nblog-item-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px;
  font-size: 13px;
  margin-bottom: 16px;
}

.nblog-item-meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.meta-date { color: #00a7e0; }
.meta-views { color: #ff3f1f; }
.meta-author { color: #333; }

.nblog-item-category {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #f2711c;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 0 4px 4px 0;
  margin-left: -24px;
  margin-bottom: 16px;
  clip-path: polygon(12px 0, 100% 0, 100% 100%, 0 100%);
}

.nblog-item-summary {
  color: #666;
  font-size: 14px;
  line-height: 1.8;
  margin: 0 0 8px;
}

.nblog-item-action {
  text-align: center;
  padding: 12px 0;
}

.nblog-item-divider {
  height: 1px;
  background: #eee;
  margin: 8px 0 12px;
}

.nblog-item-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.nblog-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  transition: opacity 0.2s;
}

.nblog-tag:hover {
  opacity: 0.85;
}

@media (max-width: 768px) {
  .nblog-item {
    padding: 20px 16px 16px;
    margin-bottom: 16px;
  }

  .nblog-item-title {
    font-size: 20px;
    line-height: 1.4;
    padding: 0 4px;
  }

  .nblog-item-meta {
    gap: 10px 14px;
    font-size: 12px;
  }

  .nblog-item-category {
    margin-left: -16px;
    font-size: 12px;
    padding: 5px 12px;
  }

  .nblog-item-summary {
    font-size: 14px;
    line-height: 1.75;
  }

  .nblog-item-action :deep(.color-btn) {
    width: auto;
    min-width: 7em;
    max-width: 100%;
    height: auto;
    line-height: 36px;
    font-size: 13px;
  }
}
</style>
