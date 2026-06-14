import { request } from './request'
import type { Article, ArticleListItem, ArticleRequest, ArticleVisibilityRequest, PageResult } from '@/types'

export function getArticleList(params: any): Promise<PageResult<ArticleListItem>> {
  return request.get('/articles', params)
}

export function getArticleDetail(id: number): Promise<Article> {
  return request.get(`/articles/${id}`)
}

export function createArticle(data: ArticleRequest): Promise<number> {
  return request.post('/articles', data)
}

export function updateArticle(data: ArticleRequest): Promise<void> {
  return request.put('/articles', data)
}

export function deleteArticle(id: number): Promise<void> {
  return request.delete(`/articles/${id}`)
}

export function batchDeleteArticles(ids: number[]): Promise<void> {
  return request.post('/articles/batch-delete', { ids })
}

export function updateArticleTop(id: number, isTop: boolean): Promise<void> {
  return request.put(`/articles/${id}/top`, undefined, { isTop })
}

export function updateArticleRecommend(id: number, isRecommend: boolean): Promise<void> {
  return request.put(`/articles/${id}/recommend`, undefined, { isRecommend })
}

export function updateArticleStatus(id: number, status: number): Promise<void> {
  return request.put(`/articles/${id}/status`, undefined, { status })
}

export function updateArticleVisibility(id: number, data: ArticleVisibilityRequest): Promise<void> {
  return request.put(`/articles/${id}/visibility`, data)
}

export function getLatestArticles(limit: number = 5): Promise<ArticleListItem[]> {
  return request.get('/articles/latest', { limit })
}

export function getHotArticles(limit: number = 5): Promise<ArticleListItem[]> {
  return request.get('/articles/hot', { limit })
}