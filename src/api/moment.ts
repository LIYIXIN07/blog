import { request } from './request'
import type { PageResult } from '@/types'

export interface MomentItem {
  id: number
  content: string
  likes: number
  published: boolean
  createdAt: string
}

export function getPublicMoments(pageNum = 1, pageSize = 10): Promise<PageResult<MomentItem>> {
  return request.get('/moments/public', { pageNum, pageSize })
}
