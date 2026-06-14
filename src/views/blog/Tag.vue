<template>
  <div>
    <div class="nblog-page-title m-box">
      {{ selectedTag ? `#${selectedTag.name}` : '标签云' }}
      <span v-if="selectedTag" class="nblog-page-count">({{ total }}篇)</span>
    </div>

    <div v-if="!selectedTag" class="nblog-tag-cloud">
      <router-link
        v-for="tag in tags"
        :key="tag.id"
        :to="`/tag/${tag.id}`"
        class="nblog-tag-cloud-item"
        :style="{ backgroundColor: `${tag.color || '#48dbfb'}22`, color: tag.color || '#48dbfb' }"
      >
        {{ tag.name }} ({{ tag.articleCount || 0 }})
      </router-link>
    </div>

    <div v-else>
      <div v-if="loading" class="nblog-empty">加载中...</div>
      <div v-else-if="articles.length === 0" class="nblog-empty">该标签暂无文章</div>
      <template v-else>
        <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
        <Pagination v-if="total > 0" v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from '@/components/blog/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'
import { getTagList } from '@/api/tag'
import { getArticleList } from '@/api/article'
import { applySeo, getSiteOrigin } from '@/utils/seo'
import type { Tag, ArticleListItem } from '@/types'

const route = useRoute()
const routeTagId = computed(() => (route.params.id ? Number(route.params.id) : null))

const tags = ref<Tag[]>([])
const articles = ref<ArticleListItem[]>([])
const selectedTag = ref<Tag | null>(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const syncTagSeo = () => {
  const origin = getSiteOrigin()
  if (selectedTag.value) {
    applySeo({
      title: `#${selectedTag.value.name} - 标签`,
      description: `标签「${selectedTag.value.name}」下的文章，共 ${total.value} 篇`,
      url: origin ? `${origin}/tag/${selectedTag.value.id}` : undefined
    })
    return
  }
  applySeo({
    title: '标签',
    description: '按标签浏览全部文章',
    url: origin ? `${origin}/tag` : undefined
  })
}

const fetchTags = async () => {
  try {
    tags.value = await getTagList()
    if (routeTagId.value) {
      selectedTag.value = tags.value.find(t => t.id === routeTagId.value) || null
    }
    syncTagSeo()
  } catch {
    tags.value = []
  }
}

const fetchArticles = async () => {
  if (!routeTagId.value) return
  loading.value = true
  try {
    const result = await getArticleList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      tagId: routeTagId.value,
      status: 1
    })
    articles.value = result.list
    total.value = result.total
    syncTagSeo()
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTags()
  if (routeTagId.value) fetchArticles()
})

watch(routeTagId, () => {
  pageNum.value = 1
  selectedTag.value = routeTagId.value ? tags.value.find(t => t.id === routeTagId.value) || null : null
  syncTagSeo()
  if (routeTagId.value) fetchArticles()
  else articles.value = []
})

watch([pageNum, pageSize], fetchArticles)
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

.nblog-page-count {
  font-size: 14px;
  font-weight: 400;
  color: #999;
  margin-left: 8px;
}

.nblog-tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.nblog-tag-cloud-item {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  transition: opacity 0.2s;
}

.nblog-tag-cloud-item:hover {
  opacity: 0.85;
}

.nblog-empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background: #fff;
  border-radius: 4px;
}
</style>
