<template>
  <aside class="space-y-6">
    <div class="bg-white rounded-lg shadow-sm p-6">
      <div class="text-center">
        <div class="w-20 h-20 mx-auto mb-4 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
          <User class="w-10 h-10 text-white" />
        </div>
        <h3 class="text-xl font-bold text-gray-800">{{ authorName }}</h3>
        <p class="text-gray-500 text-sm mt-1">{{ authorBio }}</p>
      </div>
    </div>
    
    <div class="bg-white rounded-lg shadow-sm p-6">
      <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center">
        <FileText class="w-5 h-5 mr-2 text-blue-500" />
        最新文章
      </h3>
      <ul class="space-y-3">
        <li v-for="article in latestArticles" :key="article.id">
          <router-link 
            :to="`/article/${article.id}`" 
            class="block text-gray-600 hover:text-blue-500 transition-colors truncate text-sm"
            :title="article.title"
          >
            {{ article.title }}
          </router-link>
        </li>
      </ul>
    </div>
    
    <div class="bg-white rounded-lg shadow-sm p-6">
      <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center">
        <TagIcon class="w-5 h-5 mr-2 text-blue-500" />
        热门标签
      </h3>
      <div class="flex flex-wrap gap-2">
        <router-link 
          v-for="tag in hotTags" 
          :key="tag.id"
          :to="`/tag/${tag.id}`"
          class="px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-sm hover:bg-blue-100 transition-colors"
        >
          {{ tag.name }} ({{ tag.articleCount || 0 }})
        </router-link>
      </div>
    </div>
    
    <div class="bg-white rounded-lg shadow-sm p-6">
      <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center">
        <Archive class="w-5 h-5 mr-2 text-blue-500" />
        文章归档
      </h3>
      <ul class="space-y-2">
        <li v-for="archive in archives" :key="archive">
          <button class="text-gray-600 hover:text-blue-500 transition-colors text-sm w-full text-left">
            {{ archive }}
          </button>
        </li>
      </ul>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Document as FileText, CollectionTag as TagIcon, Files as Archive } from '@element-plus/icons-vue'
import { useSettingsStore } from '@/stores/settings'
import { getLatestArticles } from '@/api/article'
import { getTagList } from '@/api/tag'
import type { ArticleListItem, Tag } from '@/types'

const authorName = ref('DollMeowOnly')
const authorBio = ref('热爱技术，乐于分享')
const latestArticles = ref<ArticleListItem[]>([])
const hotTags = ref<Tag[]>([])
const archives = ref<string[]>(['2024年12月', '2024年11月', '2024年10月'])

const settingsStore = useSettingsStore()

onMounted(() => {
  settingsStore.fetchSettings().then(settings => {
    if (settings) {
      authorName.value = settings.authorName || 'DollMeowOnly'
      authorBio.value = settings.authorBio || '热爱技术，乐于分享'
    }
  })
  
  getLatestArticles(5).then(articles => {
    latestArticles.value = articles
  }).catch(() => {
    latestArticles.value = []
  })
  
  getTagList().then(tags => {
    hotTags.value = tags.slice(0, 6)
  }).catch(() => {
    hotTags.value = []
  })
})
</script>