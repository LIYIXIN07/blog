/**
 * 通用类型定义
 */

/**
 * 统一API响应格式
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/**
 * 分页请求参数
 */
export interface PageRequest {
  pageNum: number
  pageSize: number
}

/**
 * 分页响应数据
 */
export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  totalPages: number
}

/**
 * 通用ID参数
 */
export interface IdRequest {
  id: number
}

/**
 * 批量ID参数
 */
export interface IdsRequest {
  ids: number[]
}