<template>
  <div class="nblog-site">
    <Header :transparent="navTransparent" />

    <HeroHeader v-if="isHome" />

    <main class="nblog-main" :class="{ 'nblog-main-home': isHome, 'nblog-main-page': !isHome }">
      <div class="nblog-container">
        <div class="nblog-grid">
          <LeftSidebar class="nblog-left" />
          <section class="nblog-center">
            <router-view />
          </section>
          <RightSidebar class="nblog-right" />
        </div>
      </div>
    </main>

    <GlobalMusicPlayer />
    <Footer />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/blog/Header.vue'
import HeroHeader from '@/components/blog/HeroHeader.vue'
import Footer from '@/components/blog/Footer.vue'
import GlobalMusicPlayer from '@/components/blog/GlobalMusicPlayer.vue'
import LeftSidebar from '@/components/blog/LeftSidebar.vue'
import RightSidebar from '@/components/blog/RightSidebar.vue'

const route = useRoute()
const scrollY = ref(0)
const viewportWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 1200)

const isHome = computed(() => route.name === 'Home')
const navTransparent = computed(() =>
  isHome.value && scrollY.value < window.innerHeight * 0.5 && viewportWidth.value > 768
)

const onScroll = () => {
  scrollY.value = window.scrollY
}

const onResize = () => {
  viewportWidth.value = window.innerWidth
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  window.addEventListener('resize', onResize, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('resize', onResize)
})
</script>

<style scoped>
.nblog-site {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #efefef;
}

.nblog-main {
  flex: 1;
  padding: 24px 0 0;
}

.nblog-main-home {
  margin-top: 0;
  padding-top: 0;
}

.nblog-main-page {
  padding-top: 60px;
}

.nblog-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.nblog-grid {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr) 280px;
  gap: 20px;
  align-items: start;
}

@media (max-width: 1100px) {
  .nblog-grid {
    grid-template-columns: minmax(0, 1fr) 280px;
  }
  .nblog-left {
    display: none;
  }
}

@media (max-width: 768px) {
  .nblog-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .nblog-right {
    order: 3;
  }

  .nblog-container {
    padding: 0 12px 24px;
  }

  .nblog-main {
    padding-top: 12px;
  }

  .nblog-main-page {
    padding-top: 68px;
  }
}

@media (max-width: 480px) {
  .nblog-container {
    padding: 0 10px 20px;
  }
}
</style>
