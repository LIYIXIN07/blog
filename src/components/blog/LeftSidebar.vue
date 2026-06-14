<template>
  <aside class="nblog-left">
    <div class="nblog-intro m-box">
      <div class="nblog-intro-avatar">
        <img v-if="authorAvatar" :src="authorAvatar" :alt="authorName" />
        <div v-else class="nblog-intro-avatar-placeholder" />
      </div>
      <div class="nblog-intro-name">{{ authorName }}</div>
      <RollText />
      <div class="nblog-intro-bio">{{ authorBio }}</div>
      <div class="nblog-intro-social">
        <SocialLinks
          :github="github"
          :telegram="telegram"
          :twitter="twitter"
          :gitee="gitee"
          :email="email"
        />
      </div>
    </div>

    <ArticleToc v-if="isArticlePage" class="nblog-toc-desktop" />

    <div class="nblog-widget m-box">
      <div class="nblog-widget-header">
        <el-icon><Clock /></el-icon>最新文章
      </div>
      <div class="nblog-widget-body">
        <router-link
          v-for="article in latestArticles"
          :key="article.id"
          :to="`/article/${article.id}`"
          class="nblog-list-link"
          :title="article.title"
        >
          {{ article.title }}
        </router-link>
        <div v-if="!latestArticles.length" class="nblog-muted">暂无文章</div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { Clock } from '@element-plus/icons-vue'
import RollText from '@/components/blog/RollText.vue'
import SocialLinks from '@/components/blog/SocialLinks.vue'
import ArticleToc from '@/components/blog/ArticleToc.vue'
import { useSettingsStore } from '@/stores/settings'
import { getLatestArticles } from '@/api/article'
import type { ArticleListItem } from '@/types'

const DEFAULT_AVATAR = '/images/author-avatar.png'
const DEFAULT_GITHUB = 'https://github.com/telF4'
const DEFAULT_TELEGRAM = 'https://t.me/ghxjsksnhdjjdj'
const DEFAULT_TWITTER = 'https://x.com/jien386618'
const DEFAULT_GITEE = 'https://gitee.com/DollMeowOnly'

const settingsStore = useSettingsStore()
const route = useRoute()
const isArticlePage = computed(() => route.name === 'Article')
const authorName = ref('DollMeowOnly')
const authorBio = ref('热爱技术，乐于分享')
const authorAvatar = ref(DEFAULT_AVATAR)
const github = ref(DEFAULT_GITHUB)
const telegram = ref(DEFAULT_TELEGRAM)
const twitter = ref(DEFAULT_TWITTER)
const gitee = ref(DEFAULT_GITEE)
const email = ref('')
const latestArticles = ref<ArticleListItem[]>([])

onMounted(async () => {
  const settings = await settingsStore.fetchSettings()
  if (settings) {
    authorName.value = settings.authorName || 'DollMeowOnly'
    authorBio.value = settings.authorBio || '热爱技术，乐于分享'
    authorAvatar.value = settings.authorAvatar || DEFAULT_AVATAR
    github.value = settings.github || DEFAULT_GITHUB
    telegram.value = settings.telegram || DEFAULT_TELEGRAM
    twitter.value = settings.twitter || DEFAULT_TWITTER
    gitee.value = settings.gitee || DEFAULT_GITEE
    email.value = settings.email || ''
  }

  try {
    latestArticles.value = await getLatestArticles(5)
  } catch {
    latestArticles.value = []
  }
})
</script>

<style scoped>
.nblog-intro {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 20px;
  text-align: center;
}

.nblog-intro-avatar img,
.nblog-intro-avatar-placeholder {
  width: 100%;
  height: 180px;
  object-fit: cover;
  display: block;
}

.nblog-intro-avatar-placeholder {
  background: linear-gradient(135deg, #48dbfb, #409eff);
}

.nblog-intro-name {
  font-size: 18px;
  font-weight: 700;
  padding: 16px 16px 4px;
  background: linear-gradient(120deg, #6e40aa, #e4419d, #48ef82, #35abd8, #6e40aa);
  background-size: 300% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  animation: name-shimmer 6s ease-in-out infinite;
}

@keyframes name-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.nblog-intro-bio {
  font-size: 13px;
  color: #999;
  padding: 0 16px 12px;
  line-height: 1.6;
}

.nblog-intro-social {
  padding: 12px 16px 16px;
  border-top: 1px solid #f0f0f0;
}

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

.nblog-widget-body {
  padding: 8px 14px 14px;
}

.nblog-list-link {
  display: block;
  padding: 8px 0;
  color: #666;
  font-size: 13px;
  border-bottom: 1px dashed #eee;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

@media (max-width: 1100px) {
  .nblog-toc-desktop {
    display: none;
  }
}
</style>
