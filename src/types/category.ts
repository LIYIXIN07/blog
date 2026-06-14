/**
 * 分类相关类型定义
 */

/**
 * 分类接口
 */
export interface Category {
  id: number
  name: string
  description?: string
  sortOrder?: number
  articleCount?: number
  createdAt: string
}

/**
 * 分类请求参数
 */
export interface CategoryRequest {
  id?: number
  name: string
  description?: string
  sortOrder?: number
}