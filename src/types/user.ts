/**
 * 用户相关类型定义
 */

/**
 * 用户信息接口
 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  email?: string
  role: UserRole
  createdAt: string
}

/**
 * 用户角色枚举
 */
export enum UserRole {
  ADMIN = 'ADMIN',
  USER = 'USER'
}

/**
 * 登录请求参数
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 登录响应数据
 */
export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

/**
 * 用户设置
 */
export interface UserSettings {
  siteName: string
  siteDescription: string
  authorName: string
  authorAvatar: string
  authorBio: string
  github?: string
  telegram?: string
  twitter?: string
  gitee?: string
  email?: string
  icp?: string
  siteStartDate?: string
  links: FriendLink[]
}

/**
 * 友情链接
 */
export interface FriendLink {
  id: number
  name: string
  url: string
  avatar?: string
  description?: string
}