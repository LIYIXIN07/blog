<template>
  <div>
    <div v-if="loading" class="nblog-empty">加载中...</div>
    <article v-else-if="article" class="nblog-article m-box">
      <div v-if="article.isTop" class="nblog-article-top">置顶</div>
      <img v-if="article.coverImage" :src="article.coverImage" :alt="article.title" class="nblog-article-cover" />
      <div class="nblog-article-body">
        <h1 class="nblog-article-title">{{ article.title }}</h1>
        <div class="nblog-article-meta">
          <span class="meta-date">{{ formatDate(article.createdAt) }}</span>
          <span class="meta-views">{{ article.viewCount }} 阅读</span>
          <span>{{ article.author }}</span>
          <span v-if="article.categoryName" class="meta-category">{{ article.categoryName }}</span>
        </div>
        <div v-if="article.tags?.length" class="nblog-article-tags">
          <router-link
            v-for="tag in article.tags"
            :key="tag.id"
            :to="`/tag/${tag.id}`"
            class="nblog-tag-chip"
            :style="{ background: `${tag.color || '#48dbfb'}22`, color: tag.color || '#48dbfb' }"
          >
            {{ tag.name }}
          </router-link>
        </div>
        <ArticleToc v-if="tocStore.headings.length" class="nblog-article-toc-mobile" />
        <div class="nblog-article-content">
          <MarkdownRenderer :content="article.content" />
        </div>
      </div>
    </article>
    <div v-else class="nblog-empty">文章不存在</div>

    <button
      v-show="showBackTop"
      class="nblog-back-top"
      @click="scrollToTop"
    >
      <el-icon size="20"><ArrowUp /></el-icon>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowUp } from '@element-plus/icons-vue'
import MarkdownRenderer from '@/components/blog/MarkdownRenderer.vue'
import ArticleToc from '@/components/blog/ArticleToc.vue'
import { getArticleDetail } from '@/api/article'
import { formatDate } from '@/utils/date'
import { getHeadings } from '@/utils/markdown'
import { applySeo, buildBlogPostingJsonLd, getSiteOrigin } from '@/utils/seo'
import { useArticleTocStore } from '@/stores/articleToc'
import type { Article } from '@/types'

const route = useRoute()
const tocStore = useArticleTocStore()
const articleId = computed(() => Number(route.params.id))
const article = ref<Article | null>(null)
const loading = ref(false)
const showBackTop = ref(false)

const syncToc = (content: string) => {
  tocStore.setHeadings(getHeadings(content))
}

const updateActiveHeading = () => {
  if (!tocStore.headings.length) return
  const offset = 80
  let active = tocStore.headings[0]?.id ?? ''
  for (const heading of tocStore.headings) {
    const el = document.getElementById(heading.id)
    if (el && el.getBoundingClientRect().top <= offset) {
      active = heading.id
    }
  }
  tocStore.setActiveId(active)
}

const onScroll = () => {
  showBackTop.value = window.scrollY > 400
  updateActiveHeading()
}

const syncArticleSeo = (data: Article) => {
  const origin = getSiteOrigin()
  const keywords = [data.categoryName, ...(data.tags?.map((tag) => tag.name) || [])]
    .filter(Boolean)
    .join(', ')

  applySeo({
    title: data.title,
    description: data.summary || data.title,
    keywords,
    image: data.coverImage,
    url: origin ? `${origin}/article/${data.id}` : undefined,
    type: 'article',
    jsonLd: buildBlogPostingJsonLd(data)
  })
}

const fetchArticle = async () => {
  loading.value = true
  try {
    const data = await getArticleDetail(articleId.value)
    article.value = data
    syncArticleSeo(data)
    syncToc(data.content)
    await nextTick()
    updateActiveHeading()
  } catch {
    article.value = null
    tocStore.clear()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchArticle()
  window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  tocStore.clear()
})

watch(articleId, fetchArticle)

watch(
  () => route.hash,
  (hash) => {
    if (!hash) return
    const id = hash.replace(/^#/, '')
    if (document.getElementById(id)) {
      tocStore.setActiveId(id)
    }
  }
)

const scrollToTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })
</script>

<style scoped>
.nblog-article {
  position: relative;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
}

.nblog-article-top {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #db2828;
  color: #fff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  z-index: 2;
}

.nblog-article-cover {
  width: 100%;
  max-height: 320px;
  object-fit: cover;
}

.nblog-article-body {
  padding: 28px 32px 36px;
}

.nblog-article-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 16px;
}

.nblog-article-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px;
  font-size: 13px;
  color: #999;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
  margin-bottom: 16px;
}

.meta-date { color: #00a7e0; }
.meta-views { color: #ff3f1f; }
.meta-category { color: #f2711c; }

.nblog-article-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
}

.nblog-article-toc-mobile {
  display: none;
  margin-bottom: 24px;
}

@media (max-width: 1100px) {
  .nblog-article-toc-mobile {
    display: block;
  }
}

.nblog-tag-chip {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 4px;
}

.nblog-article-content {
  line-height: 1.8;
  color: #444;
}

.nblog-empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background: #fff;
  border-radius: 4px;
}

.nblog-back-top {
  position: fixed;
  bottom: 32px;
  right: 32px;
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  background: #48dbfb;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(72, 219, 251, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

@media (max-width: 768px) {
  .nblog-article-body {
    padding: 20px 16px 24px;
  }

  .nblog-article-title {
    font-size: 22px;
    line-height: 1.35;
    padding: 0 4px;
  }

  .nblog-article-meta {
    gap: 10px 12px;
    font-size: 12px;
  }

  .nblog-article-content {
    font-size: 15px;
    overflow-wrap: anywhere;
  }

  .nblog-back-top {
    bottom: 88px;
    right: 16px;
    width: 40px;
    height: 40px;
  }
}
</style>
