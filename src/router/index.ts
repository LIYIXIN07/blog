/**
 * 路由配置入口
 */

import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import blogRoutes from './blog'
import adminRoutes, { setupRouterGuard } from './admin'
import { trackVisit } from '@/utils/visit'
import { applyRouteSeo } from '@/utils/seo'
import { useSettingsStore } from '@/stores/settings'

// 配置NProgress
NProgress.configure({ showSpinner: false })

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...blogRoutes,
    ...adminRoutes,
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFound.vue'),
      meta: {
        title: '页面未找到',
        noindex: true
      }
    }
  ],
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 设置路由守卫
setupRouterGuard(router)

// 路由进度条
router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  if (!to.path.startsWith('/admin')) {
    const settingsStore = useSettingsStore()
    if (!settingsStore.settings) {
      await settingsStore.fetchSettings()
    }
  }
  next()
})

router.afterEach((to) => {
  NProgress.done()
  if (to.path.startsWith('/admin')) {
    const title = typeof to.meta.title === 'string' ? to.meta.title : '后台管理'
    document.title = title
    return
  }

  trackVisit()
  applyRouteSeo({
    title: typeof to.meta.title === 'string' ? to.meta.title : undefined,
    description: typeof to.meta.description === 'string' ? to.meta.description : undefined,
    noindex: to.meta.noindex === true
  })
})

export default router