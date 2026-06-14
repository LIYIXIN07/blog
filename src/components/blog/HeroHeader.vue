<template>
  <header
    ref="heroRef"
    class="naccl-hero"
    :style="{ height: heroHeight + 'px' }"
  >
    <div class="naccl-hero-view">
      <img ref="preloadRef" :src="heroConfig.bg1" alt="" class="naccl-hero-preload" />
      <div class="naccl-hero-bg naccl-hero-bg1" :style="{ backgroundImage: `url(${heroConfig.bg1})` }" />
      <div class="naccl-hero-bg naccl-hero-bg2" :style="{ backgroundImage: `url(${heroConfig.bg2})` }" />
      <div
        v-show="bgLoaded"
        class="naccl-hero-bg naccl-hero-bg3"
        :style="{ backgroundImage: `url(${heroConfig.bg3})` }"
      />
    </div>

    <div class="naccl-hero-glitch" :data-word="heroTitle">
      {{ heroTitle }}
      <div class="naccl-hero-glitch-line" />
    </div>

    <button type="button" class="naccl-hero-scroll" aria-label="向下滚动" @click="scrollDown">
      <el-icon size="28"><ArrowDown /></el-icon>
    </button>

    <img
      v-if="authorAvatar"
      :src="authorAvatar"
      :alt="heroTitle"
      class="naccl-hero-avatar"
    />

    <div class="naccl-hero-wave naccl-hero-wave1" />
    <div class="naccl-hero-wave naccl-hero-wave2" />
  </header>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { useSettingsStore } from '@/stores/settings'
import { heroConfig } from '@/config/hero'

const settingsStore = useSettingsStore()
const heroRef = ref<HTMLElement | null>(null)
const preloadRef = ref<HTMLImageElement | null>(null)

const DEFAULT_AVATAR = '/images/author-avatar.png'

const heroTitle = ref("Blog")
const authorAvatar = ref(DEFAULT_AVATAR)
const bgLoaded = ref(false)
const heroHeight = ref(typeof window !== 'undefined' ? window.innerHeight : 800)

let startX = 0

const setHeroHeight = () => {
  heroHeight.value = window.innerHeight
}

const scrollDown = () => {
  window.scrollTo({ top: window.innerHeight, behavior: 'smooth' })
}

const onMouseEnter = (e: MouseEvent) => {
  startX = e.clientX
}

const onMouseMove = (e: MouseEvent) => {
  const el = heroRef.value
  if (!el) return
  const percentage = (e.clientX - startX) / window.outerWidth + 0.5
  el.style.setProperty('--percentage', String(percentage))
  el.classList.add('moving')
}

const onMouseLeave = () => {
  const el = heroRef.value
  if (!el) return
  el.classList.remove('moving')
  el.style.setProperty('--percentage', '0.5')
}

onMounted(async () => {
  setHeroHeight()
  window.addEventListener('resize', setHeroHeight)

  const settings = await settingsStore.fetchSettings()
  if (settings) {
    heroTitle.value = settings.siteName || 'Blog'
    authorAvatar.value = settings.authorAvatar || DEFAULT_AVATAR
  }

  if (preloadRef.value) {
    preloadRef.value.onload = () => { bgLoaded.value = true }
    if (preloadRef.value.complete) bgLoaded.value = true
  }

  const el = heroRef.value
  if (el) {
    el.addEventListener('mouseenter', onMouseEnter)
    el.addEventListener('mousemove', onMouseMove)
    el.addEventListener('mouseleave', onMouseLeave)
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', setHeroHeight)
  const el = heroRef.value
  if (el) {
    el.removeEventListener('mouseenter', onMouseEnter)
    el.removeEventListener('mousemove', onMouseMove)
    el.removeEventListener('mouseleave', onMouseLeave)
  }
})
</script>

<style scoped>
.naccl-hero {
  --percentage: 0.5;
  position: relative;
  width: 100%;
  overflow: hidden;
  user-select: none;
}

.naccl-hero-preload {
  display: none;
}

.naccl-hero-view {
  position: absolute;
  inset: 0;
  display: flex;
  justify-content: center;
  transform: translateX(calc(var(--percentage) * 100px));
}

.naccl-hero-bg {
  position: absolute;
  width: 110%;
  height: 100%;
  background-position: center center;
  background-size: cover;
}

.naccl-hero-bg1 {
  z-index: 10;
  opacity: calc(1 - (var(--percentage) - 0.5) / 0.5);
}

.naccl-hero-bg2 {
  z-index: 20;
  opacity: calc(1 - (var(--percentage) - 0.25) / 0.25);
}

.naccl-hero-bg3 {
  left: -10%;
  z-index: 5;
}

.naccl-hero-view,
.naccl-hero-bg1,
.naccl-hero-bg2 {
  transition: 0.2s all ease-in;
}

.naccl-hero.moving .naccl-hero-view,
.naccl-hero.moving .naccl-hero-bg1,
.naccl-hero.moving .naccl-hero-bg2 {
  transition: none;
}

/* 故障风标题 */
.naccl-hero-glitch {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%) scale(2.2);
  font-size: 34px;
  font-family: 'Noto Serif SC', Georgia, 'Times New Roman', serif;
  font-weight: 700;
  color: transparent;
  z-index: 30;
  white-space: nowrap;
  padding: 0 4px;
}

.naccl-hero-glitch-line {
  position: absolute;
  width: calc(100% - 8px);
  left: -0.5px;
  height: 1px;
  background: #000;
  z-index: 50;
  animation: naccl-line-move 5s ease-out infinite;
}

.naccl-hero-glitch::before,
.naccl-hero-glitch::after {
  content: attr(data-word);
  position: absolute;
  top: 0;
  line-height: 50px;
  overflow: hidden;
  filter: contrast(200%);
}

.naccl-hero-glitch::before {
  left: 0;
  color: red;
  text-shadow: 1px 0 0 red;
  z-index: 30;
  animation: naccl-glitch 0.95s infinite;
}

.naccl-hero-glitch::after {
  left: -1px;
  color: cyan;
  text-shadow: -1px 0 0 cyan;
  z-index: 40;
  mix-blend-mode: lighten;
  animation: naccl-glitch 1.1s infinite 0.2s;
}

@keyframes naccl-line-move {
  9% { top: 38px; }
  14% { top: 8px; }
  18% { top: 42px; }
  22% { top: 1px; }
  32% { top: 32px; }
  34% { top: 12px; }
  40% { top: 26px; }
  43% { top: 7px; }
  99% { top: 30px; }
}

@keyframes naccl-glitch {
  10% { top: -0.4px; left: -1.1px; }
  20% { top: 0.4px; left: -0.2px; }
  30% { left: 0.5px; }
  40% { top: -0.3px; left: -0.7px; }
  50% { left: 0.2px; }
  60% { top: 1.8px; left: -1.2px; }
  70% { top: -1px; left: 0.1px; }
  80% { top: -0.4px; left: -0.9px; }
  90% { left: 1.2px; }
  100% { left: -1.2px; }
}

/* 向下滚动 */
.naccl-hero-scroll {
  position: absolute;
  bottom: 150px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  width: 56px;
  height: 56px;
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.15);
  color: rgba(255, 255, 255, 0.65);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
  animation: naccl-scroll-bounce 0.5s ease-in-out alternate infinite;
  transition: opacity 0.2s, border-color 0.2s;
}

.naccl-hero-scroll:hover {
  opacity: 1;
  border-color: rgba(255, 255, 255, 0.8);
  color: #fff;
}

@keyframes naccl-scroll-bounce {
  100% { transform: translateX(-50%) translateY(10px); }
}

/* 右下角头像 */
.naccl-hero-avatar {
  position: absolute;
  right: 40px;
  bottom: 100px;
  z-index: 90;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
}

/* 波浪过渡 */
.naccl-hero-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 80;
  background-repeat: repeat-x;
  background-size: 1200px 100%;
  pointer-events: none;
}

.naccl-hero-wave1 {
  height: 75px;
  background-image: url('/img/header/wave1.svg');
}

.naccl-hero-wave2 {
  height: 90px;
  width: calc(100% + 100px);
  left: -100px;
  background-image: url('/img/header/wave2.svg');
}

@media (max-width: 1100px) {
  .naccl-hero-glitch {
    transform: translate(-50%, -50%) scale(1.35);
    font-size: clamp(18px, 3.6vw, 28px);
    max-width: 88vw;
    white-space: normal;
    text-align: center;
    line-height: 1.35;
    padding: 0 12px;
    word-break: break-word;
  }
}

@media (max-width: 768px) {
  .naccl-hero-glitch {
    top: 38%;
    transform: translate(-50%, -50%);
    font-size: clamp(15px, 5vw, 24px);
    max-width: 88vw;
    white-space: normal;
    text-align: center;
    line-height: 1.35;
    padding: 0 12px;
    word-break: break-word;
  }

  .naccl-hero-glitch-line {
    width: calc(100% - 24px);
    left: 12px;
  }

  .naccl-hero-glitch::before,
  .naccl-hero-glitch::after {
    line-height: 1.35;
    white-space: normal;
    width: 100%;
    text-align: center;
  }

  .naccl-hero-avatar {
    width: 52px;
    height: 52px;
    right: 16px;
    bottom: 96px;
  }

  .naccl-hero-scroll {
    bottom: 108px;
    width: 44px;
    height: 44px;
  }

  .naccl-hero-wave1 {
    height: 56px;
  }

  .naccl-hero-wave2 {
    height: 68px;
  }
}

@media (max-width: 390px) {
  .naccl-hero-glitch {
    font-size: clamp(13px, 4.5vw, 18px);
    max-width: 92vw;
  }
}
</style>
