/**
 * 文章相关API
 */

import { request } from './request'
import { getVisitorUuid } from '@/utils/visit'
import type { ArchiveStat, Article, ArticleListItem, ArticleRequest, ArticleQueryParams, PageResult } from '@/types'

/**
 * 获取文章列表
 */
export function getArticleList(params: ArticleQueryParams): Promise<PageResult<ArticleListItem>> {
  return request.get('/articles', params)
}

/**
 * 获取文章详情（携带访客标识，后端会统计阅读量）
 */
export function getArticleDetail(id: number): Promise<Article> {
  return request.get(`/articles/${id}`, undefined, {
    headers: {
      'X-Visitor-Uuid': getVisitorUuid()
    }
  })
}

/**
 * 创建文章
 */
export function createArticle(data: ArticleRequest): Promise<number> {
  return request.post('/articles', data)
}

/**
 * 更新文章
 */
export function updateArticle(data: ArticleRequest): Promise<void> {
  return request.put('/articles', data)
}

/**
 * 删除文章
 */
export function deleteArticle(id: number): Promise<void> {
  return request.delete(`/articles/${id}`)
}

/**
 * 批量删除文章
 */
export function batchDeleteArticles(ids: number[]): Promise<void> {
  return request.post('/articles/batch-delete', { ids })
}

/**
 * 获取最新文章
 */
export function getLatestArticles(limit: number = 5): Promise<ArticleListItem[]> {
  return request.get('/articles/latest', { limit })
}

/**
 * 获取热门文章
 */
export function getHotArticles(limit: number = 5): Promise<ArticleListItem[]> {
  return request.get('/articles/hot', { limit })
}

export function getRandomArticles(limit: number = 5): Promise<ArticleListItem[]> {
  return request.get('/articles/random', { limit })
}

export function getArchives(): Promise<ArchiveStat[]> {
  return request.get('/articles/archives')
}

export function searchArticles(keyword: string, pageNum: number = 1, pageSize: number = 10): Promise<PageResult<ArticleListItem>> {
  return request.get('/articles/search', {
    keyword,
    pageNum,
    pageSize
  })
}

/**
 * 增加文章阅读量，返回更新后的阅读量
 */
export function incrementViewCount(id: number): Promise<number> {
  return request.post(`/articles/${id}/view`)
}