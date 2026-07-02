<template>

  <footer class="nblog-footer">

    <div class="nblog-footer-inner">

      <div class="nblog-footer-grid">

        <div class="nblog-footer-col nblog-footer-brand">

          <h4>{{ siteName }}</h4>

          <img v-if="authorAvatar" :src="authorAvatar" :alt="siteName" class="nblog-footer-logo" />

          <div v-else class="nblog-footer-logo nblog-footer-logo-placeholder" />

        </div>



        <div class="nblog-footer-col nblog-footer-latest">

          <h4>最新博客</h4>

          <router-link

            v-for="article in latestArticles"

            :key="article.id"

            :to="`/article/${article.id}`"

            class="nblog-footer-link"

          >

            {{ article.title }}

          </router-link>

          <div v-if="!latestArticles.length" class="nblog-footer-muted">暂无文章</div>

        </div>



        <div class="nblog-footer-col nblog-footer-hitokoto">

          <p class="nblog-hitokoto-text">{{ hitokoto.text }}</p>

          <p v-if="hitokoto.from" class="nblog-hitokoto-from">——《{{ hitokoto.from }}》</p>

        </div>



        <div class="nblog-footer-col nblog-footer-qr">

          <h4>{{ footerImgTitle }}</h4>

          <img

            v-if="qrImageSrc"

            :src="qrImageSrc"

            :alt="footerImgTitle"

            class="nblog-footer-qr-image"

            loading="lazy"

          />

          <div v-else class="nblog-footer-qr-placeholder" />

          <p class="nblog-footer-qr-tip">扫码访问本站</p>

        </div>

      </div>



      <div class="nblog-footer-divider" />



      <p class="nblog-footer-copy">

        <span v-if="copyright?.title">{{ copyright.title }}</span>

        <span v-else>&copy; {{ currentYear }}</span>

        <router-link to="/">{{ copyright?.siteName || siteName }}</router-link>

        <span v-if="displayIcp">|</span>

        <a v-if="displayIcp" href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">{{ displayIcp }}</a>

      </p>



      <div class="nblog-footer-badges">

        <div v-for="badge in badges" :key="badge.subject" class="github-badge">

          <span class="badge-subject">{{ badge.subject }}</span>

          <span class="badge-value" :class="`bg-${badge.color}`">{{ badge.value }}</span>

        </div>

      </div>

    </div>

  </footer>

</template>



<script setup lang="ts">

import { computed, onMounted, reactive, ref } from 'vue'

import QRCode from 'qrcode'

import { useSettingsStore } from '@/stores/settings'

import { getSiteInfo } from '@/api/site'

import { getSiteStats } from '@/api/statistics'

import { getLatestArticles } from '@/api/article'

import type { ArticleListItem } from '@/types'



const settingsStore = useSettingsStore()

const DEFAULT_ICP = '赣ICP备2026013392号-1'
const siteName = ref('Blog')

const authorAvatar = ref('')

const icp = ref('')

const footerImgTitle = ref('手机看本站')

const qrImageSrc = ref('')

const currentYear = new Date().getFullYear()

const latestArticles = ref<ArticleListItem[]>([])



const copyright = reactive({

  title: '',

  siteName: ''

})



const hitokoto = reactive({

  text: '生活明朗，万物可爱。',

  from: ''

})



const siteStats = reactive({

  totalPv: 0,

  totalUv: 0,

  totalArticles: 0,

  runDays: 1

})



const formatNumber = (num: number) => {

  if (num >= 10000) return `${(num / 10000).toFixed(1)}万`

  return String(num)

}



const badges = computed(() => [

  { subject: 'Total Views', value: formatNumber(siteStats.totalPv), color: 'brightgreen' },

  { subject: 'Visitors', value: formatNumber(siteStats.totalUv), color: 'orange' },

  { subject: 'Run Days', value: String(siteStats.runDays), color: 'blue' },

  { subject: 'Articles', value: String(siteStats.totalArticles), color: 'pink' }

])



const displayIcp = computed(() => icp.value || DEFAULT_ICP)



const DEFAULT_QR_PATHS = ['/images/qr.png', '/img/qr.png']



const isCustomQrImage = (url?: string) => {

  if (!url || !url.trim()) return false

  return !DEFAULT_QR_PATHS.includes(url.trim())

}



const resolveQrImage = async (footerImgUrl?: string) => {

  if (isCustomQrImage(footerImgUrl)) {

    qrImageSrc.value = footerImgUrl!.trim()

    return

  }



  const siteUrl = window.location.origin

  try {

    qrImageSrc.value = await QRCode.toDataURL(siteUrl, {

      width: 120,

      margin: 1,

      color: {

        dark: '#1b1c1d',

        light: '#ffffff'

      }

    })

  } catch {

    qrImageSrc.value = ''

  }

}



onMounted(async () => {

  const settings = await settingsStore.fetchSettings()

  if (settings) {

    siteName.value = settings.siteName || 'Blog'

    authorAvatar.value = settings.authorAvatar || ''

    icp.value = settings.icp || ''

  }



  try {

    const siteInfo = await getSiteInfo()

    if (siteInfo.blogName) {

      siteName.value = siteInfo.blogName

    }

    footerImgTitle.value = siteInfo.footerImgTitle || '手机看本站'

    if (siteInfo.copyright) {

      copyright.title = siteInfo.copyright.title || ''

      copyright.siteName = siteInfo.copyright.siteName || ''

    }

    if (siteInfo.beian) {

      icp.value = siteInfo.beian

    }

    await resolveQrImage(siteInfo.footerImgUrl)

  } catch {

    await resolveQrImage()

  }



  try {

    latestArticles.value = await getLatestArticles(5)

  } catch {

    latestArticles.value = []

  }



  try {

    Object.assign(siteStats, await getSiteStats())

  } catch {

    // 静默失败

  }



  try {

    const res = await fetch('https://v1.hitokoto.cn/')

    const data = await res.json()

    hitokoto.text = data.hitokoto || hitokoto.text

    hitokoto.from = data.from || ''

  } catch {

    // 静默失败

  }

})

</script>



<style scoped>

@import '@/assets/styles/badge.css';



.nblog-footer {

  background: #1b1c1d;

  color: rgba(255, 255, 255, 0.65);

  padding: 48px 20px 36px;

  margin-top: auto;

}



.nblog-footer-inner {

  max-width: 1200px;

  margin: 0 auto;

  text-align: center;

}



.nblog-footer-grid {

  display: grid;

  grid-template-columns: minmax(100px, 1fr) minmax(160px, 2fr) minmax(180px, 2.2fr) minmax(100px, 1fr);

  gap: 24px 32px;

  text-align: left;

  margin-bottom: 24px;

  align-items: start;

}



.nblog-footer-col h4 {

  color: rgba(255, 255, 255, 0.9);

  font-size: 14px;

  font-weight: 500;

  letter-spacing: 1px;

  margin: 0 0 16px;

}



.nblog-footer-logo {

  width: 100px;

  max-width: 100%;

  border-radius: 8px;

  display: block;

}



.nblog-footer-logo-placeholder {

  height: 100px;

  background: linear-gradient(135deg, #48dbfb, #409eff);

}



.nblog-footer-link {

  display: block;

  color: rgba(255, 255, 255, 0.55);

  font-size: 13px;

  padding: 4px 0;

  overflow: hidden;

  text-overflow: ellipsis;

  white-space: nowrap;

  transition: color 0.2s;

}



.nblog-footer-link:hover {

  color: #48dbfb;

}



.nblog-footer-muted {

  font-size: 13px;

  color: rgba(255, 255, 255, 0.35);

}



.nblog-footer-hitokoto {

  text-align: left;

}



.nblog-hitokoto-text {

  font-size: 13px;

  line-height: 1.8;

  color: rgba(255, 255, 255, 0.55);

  margin: 0;

}



.nblog-hitokoto-from {

  font-size: 12px;

  color: rgba(255, 255, 255, 0.35);

  text-align: right;

  margin: 8px 0 0;

}



.nblog-footer-qr {

  text-align: right;

}



.nblog-footer-qr-image {

  width: 100px;

  max-width: 100%;

  height: auto;

  border-radius: 8px;

  display: inline-block;

  background: #fff;

  padding: 4px;

  box-sizing: border-box;

}



.nblog-footer-qr-placeholder {

  width: 100px;

  height: 100px;

  border-radius: 8px;

  background: rgba(255, 255, 255, 0.08);

  display: inline-block;

}



.nblog-footer-qr-tip {

  margin: 8px 0 0;

  font-size: 12px;

  color: rgba(255, 255, 255, 0.35);

}



.nblog-footer-divider {

  height: 1px;

  background: rgba(255, 255, 255, 0.08);

  margin: 24px 0;

}



.nblog-footer-copy {

  font-size: 13px;

  color: rgba(255, 255, 255, 0.45);

  margin: 0 0 16px;

}



.nblog-footer-copy a {

  color: #ffe500;

  margin: 0 8px;

}



.nblog-footer-badges {

  display: flex;

  flex-wrap: wrap;

  justify-content: center;

  gap: 8px;

}



@media (max-width: 1024px) {

  .nblog-footer-grid {

    grid-template-columns: 1fr 1fr;

  }



  .nblog-footer-brand {

    order: 1;

  }



  .nblog-footer-latest {

    order: 2;

  }



  .nblog-footer-hitokoto {

    order: 3;

    text-align: left;

  }



  .nblog-footer-qr {

    order: 4;

    text-align: right;

  }

}



@media (max-width: 640px) {

  .nblog-footer {

    padding: 36px 16px 28px;

  }



  .nblog-footer-grid {

    grid-template-columns: 1fr;

    gap: 28px;

    text-align: center;

  }



  .nblog-footer-brand,

  .nblog-footer-latest,

  .nblog-footer-hitokoto,

  .nblog-footer-qr {

    order: unset;

    text-align: center;

  }



  .nblog-hitokoto-from {

    text-align: center;

  }



  .nblog-footer-logo,

  .nblog-footer-qr-image,

  .nblog-footer-qr-placeholder {

    margin: 0 auto;

  }



  .nblog-footer-link {

    white-space: normal;

    overflow: visible;

    text-overflow: unset;

  }

}



@media (min-width: 641px) and (max-width: 1024px) and (orientation: portrait) {

  .nblog-footer-qr-image,

  .nblog-footer-qr-placeholder {

    width: 92px;

  }

}

</style>

