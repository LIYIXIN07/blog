import { request } from './request'
import type { Category, CategoryRequest } from '@/types'

export function getCategoryList(): Promise<Category[]> {
  return request.get('/categories')
}

export function getCategoryDetail(id: number): Promise<Category> {
  return request.get(`/categories/${id}`)
}

export function createCategory(data: CategoryRequest): Promise<number> {
  return request.post('/categories', data)
}

export function updateCategory(data: CategoryRequest): Promise<void> {
  return request.put('/categories', data)
}

export function deleteCategory(id: number): Promise<void> {
  return request.delete(`/categories/${id}`)
}