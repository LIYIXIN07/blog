/**
 * 文章相关类型定义
 */

/**
 * 文章接口
 */
export interface Article {
  id: number
  title: string
  summary: string
  content: string
  coverImage: string
  categoryId: number
  categoryName: string
  tags: Tag[]
  author: string
  viewCount: number
  status: ArticleStatus
  isTop?: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 文章状态枚举
 */
export enum ArticleStatus {
  DRAFT = 0,      // 草稿
  PUBLISHED = 1   // 已发布
}

/**
 * 文章创建/编辑请求参数
 */
export interface ArticleRequest {
  id?: number
  title: string
  summary: string
  content: string
  coverImage?: string
  categoryId: number
  tagIds: number[]
  status: ArticleStatus
}

/**
 * 文章查询参数
 */
export interface ArticleQueryParams {
  pageNum: number
  pageSize: number
  title?: string
  categoryId?: number
  tagId?: number
  status?: ArticleStatus
  year?: number
  month?: number
}

/**
 * 文章列表项（简化版）
 */
export interface ArticleListItem {
  id: number
  title: string
  summary: string
  coverImage: string
  categoryName: string
  tags: Tag[]
  author: string
  viewCount: number
  status?: ArticleStatus
  isTop?: boolean
  createdAt: string
}

export interface ArchiveStat {
  year: number
  month: number
  label: string
  count: number
}

/**
 * 标签接口
 */
export interface Tag {
  id: number
  name: string
  color?: string
  articleCount?: number
}

/**
 * 标签请求参数
 */
export interface TagRequest {
  id?: number
  name: string
  color?: string
}