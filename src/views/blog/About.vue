<template>
  <div>
    <div class="nblog-page-title m-box">{{ about.title || '关于我' }}</div>

    <div class="nblog-panel m-box">
      <section v-if="about.content" class="nblog-about-section about-content">
        <MarkdownRenderer :content="about.content" />
      </section>
      <section v-else class="nblog-about-section about-fallback">
        <div class="nblog-about-header">
          <img v-if="authorAvatar" :src="authorAvatar" class="nblog-about-avatar" />
          <div v-else class="nblog-about-avatar nblog-about-avatar-placeholder" />
          <div>
            <h2>{{ authorName }}</h2>
            <p>{{ authorBio }}</p>
          </div>
        </div>
        <section v-if="hasContact" class="nblog-about-section">
          <h3>联系方式</h3>
          <ul>
            <li v-if="authorEmail">邮箱：{{ authorEmail }}</li>
            <li v-if="github">
              GitHub：
              <a :href="github" target="_blank" rel="noopener noreferrer">{{ github }}</a>
            </li>
            <li v-if="telegram">
              Telegram：
              <a :href="telegram" target="_blank" rel="noopener noreferrer">{{ telegram }}</a>
            </li>
            <li v-if="twitter">
              Twitter：
              <a :href="twitter" target="_blank" rel="noopener noreferrer">{{ twitter }}</a>
            </li>
            <li v-if="gitee">
              Gitee：
              <a :href="gitee" target="_blank" rel="noopener noreferrer">{{ gitee }}</a>
            </li>
          </ul>
        </section>
        <p>{{ siteDescription || '暂无介绍' }}</p>
      </section>
    </div>

    <CommentSection
      v-if="about.commentEnabled"
      :page-type="COMMENT_PAGE_ABOUT"
    />
    <div v-else class="comment-closed m-box">评论已关闭</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import CommentSection from '@/components/blog/CommentSection.vue'
import MarkdownRenderer from '@/components/blog/MarkdownRenderer.vue'
import { getAboutInfo } from '@/api/about'
import { COMMENT_PAGE_ABOUT } from '@/api/comment'
import { useSettingsStore } from '@/stores/settings'

const settingsStore = useSettingsStore()

const DEFAULT_AVATAR = '/images/author-avatar.png'

const authorName = ref('DollMeowOnly')
const authorBio = ref('热爱技术，乐于分享')
const authorAvatar = ref(DEFAULT_AVATAR)
const authorEmail = ref('')
const github = ref('https://github.com/telF4')
const telegram = ref('https://t.me/ghxjsksnhdjjdj')
const twitter = ref('https://x.com/jien386618')
const gitee = ref('https://gitee.com/DollMeowOnly')
const siteDescription = ref('')

const hasContact = computed(
  () => authorEmail.value || github.value || telegram.value || twitter.value || gitee.value
)

const about = reactive({
  title: '关于我',
  content: '',
  commentEnabled: true
})

onMounted(async () => {
  const settings = await settingsStore.fetchSettings()
  if (settings) {
    authorName.value = settings.authorName || 'DollMeowOnly'
    authorBio.value = settings.authorBio || '热爱技术，乐于分享'
    authorAvatar.value = settings.authorAvatar || DEFAULT_AVATAR
    authorEmail.value = settings.email || ''
    github.value = settings.github || github.value
    telegram.value = settings.telegram || telegram.value
    twitter.value = settings.twitter || twitter.value
    gitee.value = settings.gitee || gitee.value
    siteDescription.value = settings.siteDescription || ''
  }

  try {
    const data = await getAboutInfo()
    about.title = data.title || '关于我'
    about.content = data.content || ''
    about.commentEnabled = data.commentEnabled !== false
  } catch {
    // 静默失败
  }
})
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
  text-align: center;
}

.nblog-panel {
  background: #fff;
  border-radius: 4px;
  padding: 28px;
}

.nblog-about-header {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 28px;
}

.nblog-about-avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.nblog-about-avatar-placeholder {
  background: linear-gradient(135deg, #48dbfb, #409eff);
}

.nblog-about-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px;
}

.nblog-about-header p {
  color: #666;
  line-height: 1.7;
  margin: 0;
}

.nblog-about-section {
  margin-bottom: 24px;
}

.nblog-about-section h3 {
  font-weight: 700;
  color: #333;
  margin: 0 0 12px;
}

.nblog-about-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nblog-about-section li {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.nblog-about-section a {
  color: #48dbfb;
}

.nblog-about-section p {
  color: #666;
  line-height: 1.8;
  margin: 0;
}

.about-content {
  line-height: 1.8;
}

.about-fallback {
  margin-bottom: 0;
}

.comment-closed {
  margin-top: 20px;
  padding: 24px;
  background: #fff;
  border-radius: 4px;
  text-align: center;
  color: #909399;
}
</style>
