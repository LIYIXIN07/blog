<template>
  <header class="naccl-nav" :class="{ transparent: transparent, solid: !transparent }">
    <div class="naccl-nav-inner">
      <router-link to="/" class="naccl-brand">{{ siteName }}</router-link>

      <nav class="naccl-menu">
        <router-link to="/" class="naccl-menu-item" :class="{ active: isActive('Home') }">
          <el-icon><HomeFilled /></el-icon>首页
        </router-link>

        <el-dropdown trigger="hover" @command="goCategory">
          <span class="naccl-menu-item" :class="{ active: route.path.startsWith('/category') }">
            <el-icon><Sunny /></el-icon>分类<el-icon class="caret"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="">全部分类</el-dropdown-item>
              <el-dropdown-item v-for="item in categories" :key="item.id" :command="String(item.id)">
                {{ item.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <router-link to="/archives" class="naccl-menu-item" :class="{ active: route.path.startsWith('/archives') }">
          <el-icon><FolderOpened /></el-icon>归档
        </router-link>
        <router-link to="/moments" class="naccl-menu-item" :class="{ active: route.path.startsWith('/moments') }">
          <el-icon><ChatDotRound /></el-icon>动态
        </router-link>
        <router-link to="/friends" class="naccl-menu-item" :class="{ active: route.path.startsWith('/friends') }">
          <el-icon><UserFilled /></el-icon>友人帐
        </router-link>
        <router-link to="/about" class="naccl-menu-item" :class="{ active: route.path.startsWith('/about') }">
          <el-icon><InfoFilled /></el-icon>关于我
        </router-link>
      </nav>

      <form class="naccl-search" @submit.prevent="handleSearch">
        <el-icon class="naccl-search-icon"><Search /></el-icon>
        <input v-model="keyword" placeholder="Search..." />
      </form>

      <button
        type="button"
        class="naccl-menu-toggle"
        aria-label="打开菜单"
        @click="menuOpen = !menuOpen"
      >
        <el-icon><Menu v-if="!menuOpen" /><Close v-else /></el-icon>
      </button>
    </div>

    <nav class="naccl-menu-mobile" :class="{ open: menuOpen }">
      <router-link to="/" class="naccl-menu-mobile-item" @click="menuOpen = false">首页</router-link>
      <router-link to="/category" class="naccl-menu-mobile-item" @click="menuOpen = false">分类</router-link>
      <router-link to="/archives" class="naccl-menu-mobile-item" @click="menuOpen = false">归档</router-link>
      <router-link to="/moments" class="naccl-menu-mobile-item" @click="menuOpen = false">动态</router-link>
      <router-link to="/friends" class="naccl-menu-mobile-item" @click="menuOpen = false">友人帐</router-link>
      <router-link to="/about" class="naccl-menu-mobile-item" @click="menuOpen = false">关于我</router-link>
      <form class="naccl-menu-mobile-search" @submit.prevent="handleSearchMobile">
        <input v-model="keyword" placeholder="搜索文章..." />
        <button type="submit">搜索</button>
      </form>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  HomeFilled, Sunny, ArrowDown, FolderOpened, ChatDotRound,
  UserFilled, InfoFilled, Search, Menu, Close
} from '@element-plus/icons-vue'
import { useSettingsStore } from '@/stores/settings'
import { getCategoryList } from '@/api/category'
import type { Category } from '@/types'

defineProps<{ transparent?: boolean }>()

const route = useRoute()
const router = useRouter()
const settingsStore = useSettingsStore()

const siteName = ref('Blog')
const keyword = ref('')
const categories = ref<Category[]>([])
const menuOpen = ref(false)

const isActive = (name: string) => route.name === name

const goCategory = (id: string) => router.push(id ? `/category/${id}` : '/category')

const handleSearch = () => {
  const q = keyword.value.trim()
  if (q) {
    menuOpen.value = false
    router.push({ path: '/search', query: { keyword: q } })
  }
}

const handleSearchMobile = () => handleSearch()

watch(() => route.path, () => {
  menuOpen.value = false
})

onMounted(async () => {
  const settings = await settingsStore.fetchSettings()
  if (settings?.siteName) siteName.value = settings.siteName
  try {
    categories.value = await getCategoryList()
  } catch {
    categories.value = []
  }
})
</script>

<style scoped>
.naccl-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 60px;
  transition: background 0.3s ease, box-shadow 0.3s ease, backdrop-filter 0.3s ease;
}

.naccl-nav.transparent {
  background: rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(6px);
  box-shadow: none;
}

.naccl-nav.solid {
  background: #1b1c1d;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.naccl-nav-inner {
  max-width: 1400px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 24px;
}

.naccl-brand {
  color: #48dbfb;
  font-size: 20px;
  font-weight: 700;
  white-space: nowrap;
  letter-spacing: 0.5px;
}

.naccl-menu {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.naccl-menu-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  color: rgba(255, 255, 255, 0.92);
  font-size: 14px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  white-space: nowrap;
}

.naccl-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.naccl-menu-item.active {
  background: rgba(72, 219, 251, 0.22);
  color: #fff;
}

.naccl-nav.solid .naccl-menu-item.active {
  background: rgba(72, 219, 251, 0.18);
}

.caret {
  font-size: 12px;
  margin-left: 2px;
}

.naccl-search {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 180px;
}

.naccl-search-icon {
  color: rgba(255, 255, 255, 0.85);
  font-size: 16px;
  flex-shrink: 0;
}

.naccl-search input {
  flex: 1;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.92);
  outline: none;
  font-size: 14px;
  min-width: 0;
}

.naccl-search input::placeholder {
  color: rgba(255, 255, 255, 0.55);
}

.naccl-menu-toggle {
  display: none;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  cursor: pointer;
  align-items: center;
  justify-content: center;
}

.naccl-menu-mobile {
  display: none;
}

@media (max-width: 1100px) {
  .naccl-nav {
    height: auto;
    min-height: 60px;
  }

  .naccl-nav-inner {
    grid-template-columns: minmax(0, 1fr) auto;
    gap: 12px;
    padding: 0 16px;
  }

  .naccl-brand {
    font-size: 16px;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: calc(100vw - 88px);
  }

  .naccl-menu {
    display: none;
  }

  .naccl-search {
    display: none;
  }

  .naccl-menu-toggle {
    display: inline-flex;
  }

  .naccl-menu-mobile {
    display: none;
    background: #1b1c1d;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
  }

  .naccl-menu-mobile.open {
    display: block;
    padding: 8px 16px 16px;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.18);
  }

  .naccl-menu-mobile-item {
    display: block;
    padding: 12px 8px;
    color: rgba(255, 255, 255, 0.92);
    font-size: 15px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  }

  .naccl-menu-mobile-item.router-link-active {
    color: #48dbfb;
  }

  .naccl-menu-mobile-search {
    display: flex;
    gap: 8px;
    margin-top: 12px;
  }

  .naccl-menu-mobile-search input {
    flex: 1;
    min-width: 0;
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 6px;
    background: rgba(255, 255, 255, 0.08);
    color: #fff;
    padding: 10px 12px;
    outline: none;
  }

  .naccl-menu-mobile-search button {
    border: none;
    border-radius: 6px;
    background: #48dbfb;
    color: #1b1c1d;
    padding: 0 14px;
    font-weight: 600;
    cursor: pointer;
  }
}

:deep(.el-dropdown) {
  color: inherit;
}
</style>

<style>
.el-dropdown-menu {
  background: #1b1c1d !important;
  border: none !important;
  margin-top: 4px !important;
}

.el-dropdown-menu__item {
  color: rgba(255, 255, 255, 0.9) !important;
}

.el-dropdown-menu__item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}
</style>
