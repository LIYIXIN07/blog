/**
 * 前台博客路由配置
 */

import type { RouteRecordRaw } from 'vue-router'

const blogRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/BlogLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/blog/Home.vue'),
        meta: {
          title: '首页',
          description: '最新文章与技术分享'
        }
      },
      {
        path: 'article/:id',
        name: 'Article',
        component: () => import('@/views/blog/Article.vue'),
        meta: { title: '文章详情', noindex: false }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/blog/Category.vue'),
        meta: { title: '分类', description: '按分类浏览全部文章' }
      },
      {
        path: 'category/:id',
        name: 'CategoryArticles',
        component: () => import('@/views/blog/Category.vue'),
        meta: { title: '分类文章', description: '分类下的文章列表' }
      },
      {
        path: 'tag',
        name: 'Tag',
        component: () => import('@/views/blog/Tag.vue'),
        meta: { title: '标签', description: '按标签浏览全部文章' }
      },
      {
        path: 'tag/:id',
        name: 'TagArticles',
        component: () => import('@/views/blog/Tag.vue'),
        meta: { title: '标签文章', description: '标签下的文章列表' }
      },
      {
        path: 'archives',
        name: 'Archives',
        component: () => import('@/views/blog/Archives.vue'),
        meta: { title: '归档', description: '按时间归档浏览文章' }
      },
      {
        path: 'archives/:year/:month',
        name: 'ArchiveDetail',
        component: () => import('@/views/blog/ArchiveDetail.vue'),
        meta: { title: '归档文章', description: '指定月份的文章列表' }
      },
      {
        path: 'moments',
        name: 'Moments',
        component: () => import('@/views/blog/Moments.vue'),
        meta: { title: '动态', description: '博客动态与随笔' }
      },
      {
        path: 'friends',
        name: 'Friends',
        component: () => import('@/views/blog/Friends.vue'),
        meta: { title: '友人帐', description: '友情链接' }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/blog/Search.vue'),
        meta: { title: '搜索', description: '站内搜索', noindex: true }
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/blog/About.vue'),
        meta: { title: '关于我', description: '关于作者与站点介绍' }
      }
    ]
  }
]

export default blogRoutes
