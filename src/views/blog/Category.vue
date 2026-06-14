<template>
  <div>
    <div class="nblog-page-title m-box">
      {{ selectedCategory ? selectedCategory.name : '分类' }}
      <span v-if="selectedCategory" class="nblog-page-count">({{ total }}篇)</span>
    </div>

    <div v-if="!selectedCategory" class="nblog-category-grid">
      <router-link
        v-for="category in categories"
        :key="category.id"
        :to="`/category/${category.id}`"
        class="nblog-category-card m-box"
      >
        <el-icon size="36" color="#48dbfb"><Folder /></el-icon>
        <h3>{{ category.name }}</h3>
        <p>{{ category.articleCount || 0 }} 篇</p>
      </router-link>
    </div>

    <div v-else>
      <div v-if="loading" class="nblog-empty">加载中...</div>
      <div v-else-if="articles.length === 0" class="nblog-empty">该分类暂无文章</div>
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
import { Folder } from '@element-plus/icons-vue'
import ArticleCard from '@/components/blog/ArticleCard.vue'
import Pagination from '@/components/common/Pagination.vue'
import { getCategoryList } from '@/api/category'
import { getArticleList } from '@/api/article'
import { applySeo, getSiteOrigin } from '@/utils/seo'
import type { Category, ArticleListItem } from '@/types'

const route = useRoute()
const routeCategoryId = computed(() => {
  const id = route.params.id
  return id ? Number(id) : null
})

const categories = ref<Category[]>([])
const articles = ref<ArticleListItem[]>([])
const selectedCategory = ref<Category | null>(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const syncCategorySeo = () => {
  const origin = getSiteOrigin()
  if (selectedCategory.value) {
    applySeo({
      title: `${selectedCategory.value.name} - 分类`,
      description: `分类「${selectedCategory.value.name}」下的文章，共 ${total.value} 篇`,
      url: origin ? `${origin}/category/${selectedCategory.value.id}` : undefined
    })
    return
  }
  applySeo({
    title: '分类',
    description: '按分类浏览全部文章',
    url: origin ? `${origin}/category` : undefined
  })
}

const fetchCategories = async () => {
  try {
    categories.value = await getCategoryList()
    if (routeCategoryId.value) {
      selectedCategory.value = categories.value.find(c => c.id === routeCategoryId.value) || null
    }
    syncCategorySeo()
  } catch {
    categories.value = []
  }
}

const fetchArticles = async () => {
  if (!routeCategoryId.value) return
  loading.value = true
  try {
    const result = await getArticleList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: routeCategoryId.value,
      status: 1
    })
    articles.value = result.list
    total.value = result.total
    syncCategorySeo()
  } catch {
    articles.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  if (routeCategoryId.value) fetchArticles()
})

watch(routeCategoryId, () => {
  pageNum.value = 1
  selectedCategory.value = routeCategoryId.value
    ? categories.value.find(c => c.id === routeCategoryId.value) || null
    : null
  syncCategorySeo()
  if (routeCategoryId.value) fetchArticles()
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

.nblog-category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.nblog-category-card {
  background: #fff;
  border-radius: 4px;
  padding: 24px 16px;
  text-align: center;
  transition: transform 0.2s;
}

.nblog-category-card:hover {
  transform: translateY(-2px);
}

.nblog-category-card h3 {
  font-weight: 700;
  color: #333;
  margin: 12px 0 4px;
}

.nblog-category-card p {
  font-size: 13px;
  color: #999;
}

.nblog-empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background: #fff;
  border-radius: 4px;
}
</style>
