/**
 * 用户相关API
 */

import { request } from './request'
import type { LoginRequest, LoginResponse, UserInfo, UserSettings, FriendLink } from '@/types'

/**
 * 用户登录
 */
export function login(data: LoginRequest): Promise<LoginResponse> {
  return request.post('/auth/login', data)
}

/**
 * 用户登出
 */
export function logout(): Promise<void> {
  return request.post('/auth/logout')
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser(): Promise<UserInfo> {
  return request.get('/auth/user')
}

/**
 * 获取网站设置
 */
export function getSettings(): Promise<UserSettings> {
  return request.get('/settings')
}

/**
 * 更新网站设置
 */
export function updateSettings(data: UserSettings): Promise<void> {
  return request.put('/settings', data)
}

/**
 * 获取友情链接列表
 */
export function getFriendLinks(): Promise<FriendLink[]> {
  return request.get('/friend-links')
}

/**
 * 创建友情链接
 */
export function createFriendLink(data: Omit<FriendLink, 'id'>): Promise<number> {
  return request.post('/friend-links', data)
}

/**
 * 更新友情链接
 */
export function updateFriendLink(data: FriendLink): Promise<void> {
  return request.put('/friend-links', data)
}

/**
 * 删除友情链接
 */
export function deleteFriendLink(id: number): Promise<void> {
  return request.delete(`/friend-links/${id}`)
}