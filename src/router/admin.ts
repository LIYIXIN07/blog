/**
 * 后台管理路由配置
 */

import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const adminRoutes: RouteRecordRaw[] = [
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: {
          title: '仪表盘',
          requiresAuth: true
        }
      },
      {
        path: 'articles',
        name: 'ArticleManage',
        component: () => import('@/views/admin/articles/List.vue'),
        meta: {
          title: '文章管理',
          requiresAuth: true
        }
      },
      {
        path: 'articles/create',
        name: 'ArticleCreate',
        component: () => import('@/views/admin/articles/Edit.vue'),
        meta: {
          title: '新增文章',
          requiresAuth: true
        }
      },
      {
        path: 'articles/edit/:id',
        name: 'ArticleEdit',
        component: () => import('@/views/admin/articles/Edit.vue'),
        meta: {
          title: '编辑文章',
          requiresAuth: true
        }
      },
      {
        path: 'categories',
        name: 'CategoryManage',
        component: () => import('@/views/admin/categories/Index.vue'),
        meta: {
          title: '分类管理',
          requiresAuth: true
        }
      },
      {
        path: 'tags',
        name: 'TagManage',
        component: () => import('@/views/admin/tags/Index.vue'),
        meta: {
          title: '标签管理',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/admin/settings/Index.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true
        }
      }
    ]
  }
]

/**
 * 路由守卫
 */
export function setupRouterGuard(router: any) {
  router.beforeEach((to: any, _from: any, next: any) => {
    // 设置页面标题
    document.title = to.meta.title ? `${to.meta.title} - 个人博客` : '个人博客'
    
    // 检查是否需要登录
    if (to.meta.requiresAuth) {
      const authStore = useAuthStore()
      if (!authStore.isLoggedIn) {
        next({
          path: '/admin/login',
          query: { redirect: to.fullPath }
        })
        return
      }
    }
    
    // 如果已登录，访问登录页时重定向到后台首页
    if (to.path === '/admin/login') {
      const authStore = useAuthStore()
      if (authStore.isLoggedIn) {
        next('/admin/dashboard')
        return
      }
    }
    
    next()
  })
}

export default adminRoutes