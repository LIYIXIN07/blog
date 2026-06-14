export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageRequest {
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  totalPages: number
}

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
  status: number
  createdAt: string
  updatedAt: string
}

export enum ArticleStatus {
  DRAFT = 0,
  PUBLISHED = 1
}

export interface ArticleRequest {
  id?: number
  title: string
  summary: string
  content: string
  coverImage?: string
  categoryId: number
  tagIds: number[]
  status: number
  isTop?: boolean
  isRecommend?: boolean
}

export interface ArticleListItem {
  id: number
  title: string
  summary: string
  coverImage: string
  categoryId?: number
  categoryName: string
  tags: Tag[]
  author: string
  viewCount: number
  status?: number
  isTop?: boolean
  isRecommend?: boolean
  password?: string
  appreciation?: boolean
  commentEnabled?: boolean
  createdAt: string
  updatedAt?: string
}

export interface ArticleVisibilityRequest {
  appreciation?: boolean
  isRecommend?: boolean
  commentEnabled?: boolean
  isTop?: boolean
  published?: boolean
  password?: string
}

export interface Category {
  id: number
  name: string
  description?: string
  sortOrder?: number
  articleCount?: number
  createdAt: string
}

export interface CategoryRequest {
  id?: number
  name: string
  description?: string
  sortOrder?: number
}

export interface Tag {
  id: number
  name: string
  color?: string
  articleCount?: number
}

export interface TagRequest {
  id?: number
  name: string
  color?: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  email?: string
  role: string
  createdAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

export interface Settings {
  id?: number
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
}

export interface FriendLink {
  id: number
  name: string
  url: string
  avatar?: string
  description?: string
  sortOrder?: number
}

export interface FriendLinkRequest {
  id?: number
  name: string
  url: string
  avatar?: string
  description?: string
  sortOrder?: number
}

export interface CategoryStat {
  id: number
  name: string
  count: number
  color: string
}

export interface TrafficStat {
  date: string
  pv: number
  uv: number
}

export interface CityVisitor {
  city: string
  uv: number
}

export interface DashboardStats {
  todayPv: number
  todayUv: number
  categories: CategoryStat[]
  tags: Tag[]
  traffic: TrafficStat[]
  totalArticles: number
  totalComments: number
  totalViews: number
  totalVisitors: number
  cityVisitors: CityVisitor[]
}

export interface SiteStats {
  totalPv: number
  totalUv: number
  totalArticles: number
  runDays: number
}

export interface VisitRecord {
  id: number
  visitorUuid: string
  ip: string
  ipSource: string
  os: string
  browser: string
  firstVisit: string
  lastVisit: string
  pv: number
}

export interface ImageBedConfig {
  type: string
  typeName: string
  enabled: boolean
  config: Record<string, string>
}

export interface ImageBedConfigRequest {
  enabled?: boolean
  config?: Record<string, string>
}

export interface ScheduledTask {
  id: number
  beanName: string
  methodName: string
  params?: string
  cronExpression: string
  status: number
  remark?: string
  createdAt: string
}

export interface ScheduledTaskRequest {
  beanName?: string
  methodName?: string
  params?: string
  cronExpression?: string
  status?: number
  remark?: string
}

export interface AccountUpdateRequest {
  nickname?: string
  email?: string
  avatar?: string
  oldPassword?: string
  newPassword?: string
}

export interface LoginLog {
  id: number
  username: string
  ip: string
  ipSource: string
  os: string
  browser: string
  status: number
  message: string
  createdAt: string
}

export interface OperationLog {
  id: number
  username: string
  module: string
  description: string
  method: string
  uri: string
  ip: string
  params?: string
  createdAt: string
}

export interface ExceptionLog {
  id: number
  username: string
  uri: string
  method: string
  exceptionName: string
  message: string
  stackTrace?: string
  ip: string
  createdAt: string
}

export interface AccessLog {
  id: number
  visitorUuid: string
  uri: string
  ip: string
  ipSource: string
  os: string
  browser: string
  createdAt: string
}

export interface TaskLog {
  id: number
  taskName: string
  status: number
  message: string
  durationMs: number
  createdAt: string
}

export type LogType = 'login' | 'operation' | 'exception' | 'access' | 'task'

export interface Moment {
  id: number
  content: string
  likes: number
  published: boolean
  createdAt: string
  updatedAt?: string
}

export interface MomentRequest {
  id?: number
  content: string
  likes?: number
  published?: boolean
  createdAt?: string
}

export interface Comment {
  id: number
  nickname: string
  email?: string
  content: string
  avatar?: string
  website?: string
  ip?: string
  ipSource?: string
  published: boolean
  adminComment?: boolean
  pageType?: number
  notice?: boolean
  parentId?: number
  articleId?: number
  articleTitle?: string
  qq?: string
  createdAt: string
  replies?: Comment[]
}

export interface CommentRequest {
  id?: number
  nickname: string
  email?: string
  content: string
  avatar?: string
  website?: string
  ip?: string
}