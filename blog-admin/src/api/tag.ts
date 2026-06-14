import { request } from './request'
import type { Tag, TagRequest } from '@/types'

export function getTagList(): Promise<Tag[]> {
  return request.get('/tags')
}

export function getTagDetail(id: number): Promise<Tag> {
  return request.get(`/tags/${id}`)
}

export function createTag(data: TagRequest): Promise<number> {
  return request.post('/tags', data)
}

export function updateTag(data: TagRequest): Promise<void> {
  return request.put('/tags', data)
}

export function deleteTag(id: number): Promise<void> {
  return request.delete(`/tags/${id}`)
}