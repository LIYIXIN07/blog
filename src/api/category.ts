/**
 * 分类相关API
 */

import { request } from './request'
import type { Category, CategoryRequest } from '@/types'

/**
 * 获取所有分类
 */
export function getCategoryList(): Promise<Category[]> {
  return request.get('/categories')
}

/**
 * 获取分类详情
 */
export function getCategoryDetail(id: number): Promise<Category> {
  return request.get(`/categories/${id}`)
}

/**
 * 创建分类
 */
export function createCategory(data: CategoryRequest): Promise<number> {
  return request.post('/categories', data)
}

/**
 * 更新分类
 */
export function updateCategory(data: CategoryRequest): Promise<void> {
  return request.put('/categories', data)
}

/**
 * 删除分类
 */
export function deleteCategory(id: number): Promise<void> {
  return request.delete(`/categories/${id}`)
}