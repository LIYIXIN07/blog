import { request } from './request'
import type { Comment, CommentRequest, PageResult } from '@/types'

export function getCommentList(params: {
  pageNum: number
  pageSize: number
  pageType?: number
  articleId?: number
}): Promise<PageResult<Comment>> {
  return request.get('/comments', params)
}

export function updateCommentPublished(id: number, published: boolean): Promise<void> {
  return request.put(`/comments/${id}/published?published=${published}`)
}

export function updateCommentNotice(id: number, notice: boolean): Promise<void> {
  return request.put(`/comments/${id}/notice?notice=${notice}`)
}

export function updateComment(data: CommentRequest): Promise<void> {
  return request.put('/comments', data)
}

export function deleteComment(id: number): Promise<void> {
  return request.delete(`/comments/${id}`)
}
