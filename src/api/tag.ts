/**
 * 标签相关API
 */

import { request } from './request'
import type { Tag, TagRequest } from '@/types'

/**
 * 获取所有标签
 */
export function getTagList(): Promise<Tag[]> {
  return request.get('/tags')
}

/**
 * 获取标签详情
 */
export function getTagDetail(id: number): Promise<Tag> {
  return request.get(`/tags/${id}`)
}

/**
 * 创建标签
 */
export function createTag(data: TagRequest): Promise<number> {
  return request.post('/tags', data)
}

/**
 * 更新标签
 */
export function updateTag(data: TagRequest): Promise<void> {
  return request.put('/tags', data)
}

/**
 * 删除标签
 */
export function deleteTag(id: number): Promise<void> {
  return request.delete(`/tags/${id}`)
}