import { request } from './request'
import type { FriendLink } from '@/types'

export function getFriendLinks(): Promise<FriendLink[]> {
  return request.get('/friend-links')
}

export function createFriendLink(data: Omit<FriendLink, 'id'>): Promise<number> {
  return request.post('/friend-links', data)
}

export function updateFriendLink(data: FriendLink): Promise<void> {
  return request.put('/friend-links', data)
}

export function deleteFriendLink(id: number): Promise<void> {
  return request.delete(`/friend-links/${id}`)
}
