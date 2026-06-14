import { request } from './request'
import type { LoginRequest, LoginResponse, Settings, UserInfo } from '@/types'

export function login(data: LoginRequest): Promise<LoginResponse> {
  return request.post('/auth/login', data)
}

export function getCurrentUser(): Promise<UserInfo> {
  return request.get('/auth/user')
}

export function getSettings(): Promise<Settings> {
  return request.get('/settings')
}

export function updateSettings(data: Settings): Promise<void> {
  return request.put('/settings', data)
}
