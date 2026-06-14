<template>
  <el-container class="admin-layout">
    <el-aside :width="collapsed ? '64px' : '220px'" class="admin-aside">
      <div class="brand" :class="{ 'brand--collapsed': collapsed }" @click="$router.push('/dashboard')">
        <BrandLogo :size="collapsed ? 32 : 36" />
        <div v-if="!collapsed" class="brand-full">
          <span class="brand-name">DollMeowOnly</span>
          <span class="brand-sub">Blog Admin</span>
        </div>
      </div>

      <el-scrollbar class="menu-scroll">
        <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>Dashboard</span>
          </el-menu-item>

          <el-sub-menu index="blog">
            <template #title>
              <el-icon><Grid /></el-icon>
              <span>博客管理</span>
            </template>
            <el-menu-item index="/articles/create">写文章</el-menu-item>
            <el-menu-item index="/moments/create">写动态</el-menu-item>
            <el-menu-item index="/articles">文章管理</el-menu-item>
            <el-menu-item index="/moments">动态管理</el-menu-item>
            <el-menu-item index="/categories">分类管理</el-menu-item>
            <el-menu-item index="/tags">标签管理</el-menu-item>
            <el-menu-item index="/comments">评论管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="page">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>页面管理</span>
            </template>
            <el-menu-item index="/settings">站点设置</el-menu-item>
            <el-menu-item index="/friend-links">友链管理</el-menu-item>
            <el-menu-item index="/about">关于我</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="image-bed">
            <template #title>
              <el-icon><Picture /></el-icon>
              <span>图床管理</span>
            </template>
            <el-menu-item index="/image-bed/local">配置</el-menu-item>
            <el-menu-item index="/image-bed/github">GitHub</el-menu-item>
            <el-menu-item index="/image-bed/upyun">又拍云</el-menu-item>
            <el-menu-item index="/image-bed/tencent">腾讯云</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Tools /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/account">修改账户</el-menu-item>
            <el-menu-item index="/system/tasks">定时任务</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="logs">
            <template #title>
              <el-icon><Tickets /></el-icon>
              <span>日志管理</span>
            </template>
            <el-menu-item index="/logs/task">任务日志</el-menu-item>
            <el-menu-item index="/logs/login">登录日志</el-menu-item>
            <el-menu-item index="/logs/operation">操作日志</el-menu-item>
            <el-menu-item index="/logs/exception">异常日志</el-menu-item>
            <el-menu-item index="/logs/access">访问日志</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="stats">
            <template #title>
              <el-icon><DataLine /></el-icon>
              <span>数据统计</span>
            </template>
            <el-menu-item index="/statistics">数据概览</el-menu-item>
            <el-menu-item index="/statistics/visitors">访客统计</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="admin-right">
      <el-header class="admin-header">
        <div class="header-left">
          <el-button link @click="collapsed = !collapsed">
            <el-icon size="20"><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
              {{ item }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-avatar :size="32" :src="avatarUrl" />
          <el-button link type="danger" @click="handleLogout">退出</el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Odometer, Grid, Document, Picture, Tools, Tickets, DataLine, Fold, Expand
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import BrandLogo from '@/components/BrandLogo.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const collapsed = ref(false)

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/articles/create')) return '/articles/create'
  if (path.startsWith('/moments/create')) return '/moments/create'
  if (path.startsWith('/moments/edit')) return '/moments'
  if (path.startsWith('/articles/edit') || path.startsWith('/articles')) return '/articles'
  if (path.startsWith('/moments')) return '/moments'
  if (path.startsWith('/statistics')) return path
  if (path.startsWith('/image-bed/')) return path
  if (path.startsWith('/logs/')) return path
  if (path.startsWith('/system/')) return path
  return path
})

const breadcrumbs = computed(() => (route.meta.breadcrumb as string[]) || ['Dashboard'])

const DEFAULT_AVATAR = '/images/author-avatar.png'

const avatarUrl = computed(() => authStore.userInfo?.avatar || DEFAULT_AVATAR)

const handleLogout = async () => {
  await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
  authStore.logout()
  ElMessage.success('已退出')
  router.replace('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.admin-aside {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 200;
  background: linear-gradient(180deg, #2f4050 0%, #344058 100%);
  box-shadow: inset -1px 0 0 rgba(0, 0, 0, 0.06);
  transition: width 0.2s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.admin-right {
  margin-left: v-bind('collapsed ? "64px" : "220px"');
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.2s;
  min-width: 0;
}

.brand {
  flex-shrink: 0;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  padding: 0 14px;
  color: #fff;
  cursor: pointer;
  border-bottom: 1px solid #263445;
  transition: background-color 0.2s ease;
}

.brand:hover {
  background: rgba(255, 255, 255, 0.04);
}

.brand--collapsed {
  justify-content: center;
  padding: 0;
}

.brand-full {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.25;
  min-width: 0;
  flex: 1;
}

.brand-name {
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 196px;
}

.brand-sub {
  font-size: 11px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.72);
  white-space: nowrap;
}

.menu-scroll {
  flex: 1;
  min-height: 0;
}

.menu-scroll :deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

.admin-header {
  position: sticky;
  top: 0;
  z-index: 100;
  flex-shrink: 0;
  height: 56px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-main {
  flex: 1;
  min-height: 0;
  background: #f5f7fa;
  padding: 20px;
  overflow-x: auto;
  overflow-y: auto;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item) {
  position: relative;
  margin: 4px 8px;
  border-radius: 8px;
  transition: background-color 0.25s ease, color 0.25s ease;
}

:deep(.el-menu-item.is-active) {
  background: rgba(99, 102, 241, 0.14) !important;
  color: #a5b4fc !important;
}

:deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 22px;
  background: linear-gradient(180deg, #818cf8, #6366f1);
  border-radius: 0 4px 4px 0;
}

:deep(.el-sub-menu__title) {
  margin: 4px 8px;
  border-radius: 8px;
}

:deep(.el-sub-menu__title:hover),
:deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.06) !important;
}
</style>
