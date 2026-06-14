<template>
  <div>
    <div v-if="loading" class="nblog-empty">加载中...</div>
    <div v-else-if="articles.length === 0" class="nblog-empty">暂无文章</div>
    <template v-else>
      <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
      <Pagination
        v-if="total > 0"
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from '@/components/blog/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'
import { getArticleList } from '@/api/article'
import type { ArticleListItem } from '@/types'

const route = useRoute()
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const articles = ref<ArticleListItem[]>([])
const loading = ref(false)

const fetchArticles = async () => {
  loading.value = true
  try {
    const result = await getArticleList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 1
    })
    articles.value = result.list
    total.value = result.total
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

watch([pageNum, pageSize], fetchArticles)
watch(
  () => route.name,
  (name) => {
    if (name === 'Home') {
      fetchArticles()
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.nblog-empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
}
</style>
