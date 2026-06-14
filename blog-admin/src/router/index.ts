import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { public: true, title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/views/Layout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: 'Dashboard', breadcrumb: ['Dashboard'] }
        },
        {
          path: 'articles/create',
          name: 'ArticleCreate',
          component: () => import('@/views/articles/Edit.vue'),
          meta: { title: '写文章', breadcrumb: ['Dashboard', '博客管理', '写文章'] }
        },
        {
          path: 'articles',
          name: 'Articles',
          component: () => import('@/views/articles/List.vue'),
          meta: { title: '文章管理', breadcrumb: ['Dashboard', '博客管理', '文章管理'] }
        },
        {
          path: 'articles/edit/:id',
          name: 'ArticleEdit',
          component: () => import('@/views/articles/Edit.vue'),
          meta: { title: '编辑文章', breadcrumb: ['Dashboard', '博客管理', '编辑文章'] }
        },
        {
          path: 'categories',
          name: 'Categories',
          component: () => import('@/views/categories/List.vue'),
          meta: { title: '分类管理', breadcrumb: ['Dashboard', '博客管理', '分类管理'] }
        },
        {
          path: 'tags',
          name: 'Tags',
          component: () => import('@/views/tags/List.vue'),
          meta: { title: '标签管理', breadcrumb: ['Dashboard', '博客管理', '标签管理'] }
        },
        {
          path: 'moments/create',
          name: 'MomentCreate',
          component: () => import('@/views/moments/Edit.vue'),
          meta: { title: '写动态', breadcrumb: ['Dashboard', '博客管理', '写动态'] }
        },
        {
          path: 'moments/edit/:id',
          name: 'MomentEdit',
          component: () => import('@/views/moments/Edit.vue'),
          meta: { title: '编辑动态', breadcrumb: ['Dashboard', '博客管理', '编辑动态'] }
        },
        {
          path: 'moments',
          name: 'Moments',
          component: () => import('@/views/moments/List.vue'),
          meta: { title: '动态管理', breadcrumb: ['Dashboard', '博客管理', '动态管理'] }
        },
        {
          path: 'comments',
          name: 'Comments',
          component: () => import('@/views/comments/List.vue'),
          meta: { title: '评论管理', breadcrumb: ['Dashboard', '博客管理', '评论管理'] }
        },
        {
          path: 'settings',
          name: 'Settings',
          component: () => import('@/views/Settings.vue'),
          meta: { title: '站点设置', breadcrumb: ['Dashboard', '页面管理', '站点设置'] }
        },
        {
          path: 'friend-links',
          name: 'FriendLinks',
          component: () => import('@/views/FriendLinks.vue'),
          meta: { title: '友链管理', breadcrumb: ['Dashboard', '页面管理', '友链管理'] }
        },
        {
          path: 'about',
          name: 'AboutPage',
          component: () => import('@/views/page/About.vue'),
          meta: { title: '关于我', breadcrumb: ['Dashboard', '页面管理', '关于我'] }
        },
        {
          path: 'image-bed/:type',
          name: 'ImageBed',
          component: () => import('@/views/image-bed/Edit.vue'),
          meta: { title: '图床管理', breadcrumb: ['Dashboard', '图床管理'] }
        },
        {
          path: 'system/account',
          name: 'SystemAccount',
          component: () => import('@/views/system/Account.vue'),
          meta: { title: '修改账户', breadcrumb: ['Dashboard', '系统管理', '修改账户'] }
        },
        {
          path: 'system/tasks',
          name: 'SystemTasks',
          component: () => import('@/views/system/Tasks.vue'),
          meta: { title: '定时任务', breadcrumb: ['Dashboard', '系统管理', '定时任务'] }
        },
        {
          path: 'logs/:type',
          name: 'Logs',
          component: () => import('@/views/logs/List.vue'),
          meta: { title: '日志管理', breadcrumb: ['Dashboard', '日志管理'] }
        },
        {
          path: 'statistics',
          name: 'Statistics',
          component: () => import('@/views/statistics/Overview.vue'),
          meta: { title: '数据概览', breadcrumb: ['Dashboard', '数据统计', '数据概览'] }
        },
        {
          path: 'statistics/visitors',
          name: 'StatisticsVisitors',
          component: () => import('@/views/statistics/Visitors.vue'),
          meta: { title: '访客统计', breadcrumb: ['Dashboard', '数据统计', '访客统计'] }
        },
        {
          path: 'visitors',
          redirect: '/statistics/visitors'
        },
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ]
})

const imageBedTitles: Record<string, string> = {
  local: '配置',
  github: 'GitHub',
  upyun: '又拍云',
  tencent: '腾讯云'
}

const logTitles: Record<string, string> = {
  task: '任务日志',
  login: '登录日志',
  operation: '操作日志',
  exception: '异常日志',
  access: '访问日志'
}

router.beforeEach((to) => {
  if (to.name === 'ImageBed') {
    const type = String(to.params.type || 'local')
    to.meta.title = imageBedTitles[type] || '图床管理'
    to.meta.breadcrumb = ['Dashboard', '图床管理', imageBedTitles[type] || type]
  }
  if (to.name === 'Logs') {
    const type = String(to.params.type || 'login')
    to.meta.title = logTitles[type] || '日志管理'
    to.meta.breadcrumb = ['Dashboard', '日志管理', logTitles[type] || type]
  }

  document.title = `${to.meta.title || 'Dashboard'} - DollMeowOnly Admin`
  if (to.meta.public) return true
  if (!useAuthStore().isLoggedIn) return '/login'
  return true
})

export default router
