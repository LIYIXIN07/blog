import { request } from './request'
import type { PageResult } from '@/types'

export const COMMENT_PAGE_ABOUT = 1
export const COMMENT_PAGE_ARTICLE = 0
export const COMMENT_PAGE_FRIENDS = 2

export interface PublicComment {
  id: number
  nickname: string
  content: string
  avatar?: string
  website?: string
  adminComment?: boolean
  parentId?: number
  parentNickname?: string
  createdAt: string
  replies?: PublicComment[]
}

export interface PublicCommentPage {
  allComment: number
  closeComment: number
  comments: PageResult<PublicComment>
}

export interface CommentSubmitRequest {
  content: string
  nickname: string
  email: string
  website?: string
  notice?: boolean
  pageType: number
  articleId?: number
  parentId?: number
}

export function getPublicComments(params: {
  pageType: number
  articleId?: number
  pageNum: number
  pageSize: number
}): Promise<PublicCommentPage> {
  return request.get('/comments/public', params)
}

export function submitPublicComment(data: CommentSubmitRequest): Promise<void> {
  return request.post('/comments/public', data)
}
