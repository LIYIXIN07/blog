<template>
  <div>
    <h1 class="text-2xl font-bold text-gray-800 mb-6">仪表盘</h1>
    
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">文章总数</p>
            <p class="text-3xl font-bold text-blue-500 mt-2">{{ stats.articleCount }}</p>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
            <FileText class="w-6 h-6 text-blue-500" />
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">分类总数</p>
            <p class="text-3xl font-bold text-green-500 mt-2">{{ stats.categoryCount }}</p>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
            <Folder class="w-6 h-6 text-green-500" />
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">标签总数</p>
            <p class="text-3xl font-bold text-purple-500 mt-2">{{ stats.tagCount }}</p>
          </div>
          <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
            <Tags class="w-6 h-6 text-purple-500" />
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">总访问量</p>
            <p class="text-3xl font-bold text-orange-500 mt-2">{{ stats.viewCount }}</p>
          </div>
          <div class="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
            <Eye class="w-6 h-6 text-orange-500" />
          </div>
        </div>
      </div>
    </div>
    
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center">
          <Clock class="w-5 h-5 mr-2 text-blue-500" />
          最近发布的文章
        </h3>
        <div v-if="recentArticles.length === 0" class="text-center py-8">
          <FileText class="w-12 h-12 text-gray-300 mx-auto mb-2" />
          <p class="text-gray-500">暂无文章</p>
        </div>
        <ul v-else class="space-y-3">
          <li 
            v-for="article in recentArticles" 
            :key="article.id"
            class="flex items-center justify-between py-2 border-b border-gray-100 last:border-0"
          >
            <router-link 
              :to="`/admin/articles/edit/${article.id}`" 
              class="text-gray-800 hover:text-blue-500 transition-colors truncate"
            >
              {{ article.title }}
            </router-link>
            <span class="text-gray-400 text-sm">{{ formatDate(article.createdAt) }}</span>
          </li>
        </ul>
      </div>
      
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center">
          <TrendingUp class="w-5 h-5 mr-2 text-green-500" />
          访问量趋势
        </h3>
        <div class="h-48 flex items-end justify-between gap-4">
          <div 
            v-for="(value, index) in viewTrend" 
            :key="index"
            class="flex-1 bg-gradient-to-t from-blue-500 to-blue-300 rounded-t-lg transition-all"
            :style="{ height: `${value}%` }"
          ></div>
        </div>
        <div class="flex justify-between mt-2 text-xs text-gray-400">
          <span>周一</span>
          <span>周二</span>
          <span>周三</span>
          <span>周四</span>
          <span>周五</span>
          <span>周六</span>
          <span>周日</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Document as FileText, Folder, CollectionTag as Tags, View as Eye, Clock, Top as TrendingUp } from '@element-plus/icons-vue'
import { getLatestArticles } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { getTagList } from '@/api/tag'
import { formatDate } from '@/utils/date'
import type { ArticleListItem } from '@/types'

const stats = ref({
  articleCount: 0,
  categoryCount: 0,
  tagCount: 0,
  viewCount: 0
})

const recentArticles = ref<ArticleListItem[]>([])
const viewTrend = ref([30, 45, 60, 40, 80, 95, 70])

const fetchStats = async () => {
  try {
    const [articles, categories, tags] = await Promise.all([
      getLatestArticles(100),
      getCategoryList(),
      getTagList()
    ])
    
    stats.value.articleCount = articles.length
    stats.value.categoryCount = categories.length
    stats.value.tagCount = tags.length
    stats.value.viewCount = articles.reduce((sum, article) => sum + article.viewCount, 0)
    
    recentArticles.value = articles.slice(0, 5)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>