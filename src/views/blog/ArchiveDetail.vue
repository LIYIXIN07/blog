<template>
  <div>
    <div class="nblog-page-title m-box">{{ pageTitle }}</div>
    <div v-if="loading" class="nblog-empty">加载中...</div>
    <div v-else-if="articles.length === 0" class="nblog-empty">该时段暂无文章</div>
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
import { getArticleList } from '@/api/article'
import type { ArticleListItem } from '@/types'

const route = useRoute()
const year = computed(() => Number(route.params.year))
const month = computed(() => Number(route.params.month))
const pageTitle = computed(() => `${year.value}年${String(month.value).padStart(2, '0')}月`)

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
      status: 1,
      year: year.value,
      month: month.value
    })
    articles.value = result.list
    total.value = result.total
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchArticles)
watch([pageNum, pageSize, year, month], fetchArticles)
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
