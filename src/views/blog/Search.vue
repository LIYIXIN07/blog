<template>
  <div>
    <div class="nblog-page-title m-box">搜索：{{ keyword }}</div>
    <div v-if="loading" class="nblog-empty">搜索中...</div>
    <div v-else-if="articles.length === 0" class="nblog-empty">未找到相关文章</div>
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
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from '@/components/blog/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'
import { searchArticles } from '@/api/article'
import type { ArticleListItem } from '@/types'

const route = useRoute()
const keyword = computed(() => String(route.query.keyword || ''))

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const articles = ref<ArticleListItem[]>([])
const loading = ref(false)

const fetchResults = async () => {
  if (!keyword.value) {
    articles.value = []
    return
  }
  loading.value = true
  try {
    const result = await searchArticles(keyword.value, pageNum.value, pageSize.value)
    articles.value = result.list
    total.value = result.total
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchResults)
watch([keyword, pageNum, pageSize], fetchResults)
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

.nblog-empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background: #fff;
  border-radius: 4px;
}
</style>
