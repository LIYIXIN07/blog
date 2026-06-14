<template>
  <div class="min-h-screen flex bg-gray-50">
    <aside 
      class="fixed left-0 top-0 h-screen w-64 bg-gray-900 text-white z-50 flex flex-col transition-all duration-300"
      :class="{ '-translate-x-full': collapsed, 'translate-x-0': !collapsed }"
    >
      <div class="h-16 flex items-center justify-between px-4 border-b border-gray-700">
        <div class="flex items-center space-x-3" :class="{ 'w-0 overflow-hidden': collapsed }">
          <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
            <FileText class="w-5 h-5 text-white" />
          </div>
          <span class="font-bold text-lg">博客管理</span>
        </div>
        <button 
          class="p-2 hover:bg-gray-800 rounded-lg transition-colors"
          @click="collapsed = !collapsed"
        >
          <ChevronLeft v-if="!collapsed" class="w-5 h-5" />
          <ChevronRight v-else class="w-5 h-5" />
        </button>
      </div>
      
      <nav class="flex-1 py-4 px-3 overflow-y-auto">
        <div class="space-y-1">
          <div v-for="item in menuItems" :key="item.path">
            <router-link 
              v-if="!item.children"
              :to="item.path"
              class="flex items-center space-x-3 px-3 py-2.5 rounded-lg transition-all duration-200"
              :class="isActive(item.path) ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800'"
            >
              <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
              <span v-if="!collapsed" class="text-sm font-medium">{{ item.label }}</span>
            </router-link>
            
            <div v-else class="relative">
              <button 
                class="w-full flex items-center space-x-3 px-3 py-2.5 rounded-lg transition-all duration-200"
                :class="isActive(item.path) ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-800'"
                @click="toggleSubMenu(item.path)"
              >
                <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
                <span v-if="!collapsed" class="text-sm font-medium">{{ item.label }}</span>
                <ChevronDown 
                  v-if="!collapsed" 
                  class="w-4 h-4 ml-auto transition-transform duration-200"
                  :class="{ 'rotate-180': expandedMenus.includes(item.path) }"
                />
              </button>
              
              <div 
                v-if="!collapsed && expandedMenus.includes(item.path)" 
                class="ml-8 mt-1 space-y-1"
              >
                <router-link 
                  v-for="child in item.children" 
                  :key="child.path"
                  :to="child.path"
                  class="flex items-center space-x-2 px-3 py-2 rounded-lg text-sm text-gray-400 hover:text-white hover:bg-gray-800 transition-colors"
                  :class="{ 'text-white bg-gray-800': isActive(child.path) }"
                >
                  <Circle class="w-2 h-2" />
                  <span>{{ child.label }}</span>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </nav>
      
      <div class="p-4 border-t border-gray-700">
        <div class="flex items-center space-x-3" :class="{ 'justify-center': collapsed }">
          <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
            <User class="w-5 h-5 text-white" />
          </div>
          <div v-if="!collapsed" class="flex-1 min-w-0">
            <p class="text-sm font-medium truncate">{{ authStore.nickname || '管理员' }}</p>
            <p class="text-xs text-gray-400">管理员</p>
          </div>
          <button 
            v-if="!collapsed"
            class="p-2 text-gray-400 hover:text-white hover:bg-gray-800 rounded-lg transition-colors"
            @click="handleLogout"
          >
            <Logout class="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
    
    <div 
      v-if="collapsed === false"
      class="fixed inset-0 bg-black/50 z-40 lg:hidden"
      @click="collapsed = true"
    ></div>
    
    <div class="flex-1 min-h-screen lg:ml-64">
      <header class="fixed top-0 right-0 left-0 lg:left-64 h-16 bg-white shadow-sm z-30">
        <div class="h-full px-6 flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <button 
              class="lg:hidden p-2 hover:bg-gray-100 rounded-lg"
              @click="collapsed = !collapsed"
            >
              <Menu class="w-6 h-6 text-gray-600" />
            </button>
            <div class="flex items-center space-x-2">
              <router-link to="/admin/dashboard" class="flex items-center text-gray-500 hover:text-gray-700">
                <Home class="w-4 h-4" />
              </router-link>
              <ChevronRight class="w-4 h-4 text-gray-400" />
              <template v-for="(crumb, index) in breadcrumbs" :key="index">
                <router-link 
                  v-if="index < breadcrumbs.length - 1" 
                  :to="crumb.path" 
                  class="text-blue-500 hover:text-blue-600 text-sm"
                >
                  {{ crumb.label }}
                </router-link>
                <span v-else class="text-gray-700 text-sm font-medium">{{ crumb.label }}</span>
                <ChevronRight v-if="index < breadcrumbs.length - 1" class="w-4 h-4 text-gray-400" />
              </template>
            </div>
          </div>
        </div>
      </header>
      
      <main class="pt-16 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, markRaw } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Document as FileText,
  ArrowLeft as ChevronLeft,
  ArrowRight as ChevronRight,
  ArrowDown as ChevronDown,
  User,
  SwitchButton as Logout,
  Menu,
  House as Home,
  CircleCheck as Circle,
  Odometer,
  Folder,
  CollectionTag as Tags,
  Setting as Settings
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const collapsed = ref(false)
const expandedMenus = ref<string[]>(['/admin/articles'])

const menuItems = [
  { path: '/admin/dashboard', label: '仪表盘', icon: markRaw(Odometer) },
  {
    path: '/admin/articles',
    label: '文章管理',
    icon: markRaw(FileText),
    children: [
      { path: '/admin/articles', label: '文章列表' },
      { path: '/admin/articles/create', label: '新增文章' }
    ]
  },
  { path: '/admin/categories', label: '分类管理', icon: markRaw(Folder) },
  { path: '/admin/tags', label: '标签管理', icon: markRaw(Tags) },
  { path: '/admin/settings', label: '系统设置', icon: markRaw(Settings) }
]

const breadcrumbs = computed(() => {
  const items: Array<{ path: string; label: string }> = []
  const title = route.meta.title as string | undefined
  if (title) {
    items.push({ path: route.fullPath, label: title })
  }
  return items
})

const isActive = (path: string) => {
  if (path === '/admin/articles') {
    return route.path.startsWith('/admin/articles')
  }
  return route.path === path
}

const toggleSubMenu = (path: string) => {
  const index = expandedMenus.value.indexOf(path)
  if (index >= 0) {
    expandedMenus.value.splice(index, 1)
  } else {
    expandedMenus.value.push(path)
  }
}

const handleLogout = async () => {
  try {
    await authStore.logout()
    ElMessage.success('退出成功')
    router.push('/admin/login')
  } catch {
    ElMessage.error('退出失败')
  }
}
</script>
