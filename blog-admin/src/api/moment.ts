import { request } from './request'
import type { Moment, MomentRequest, PageResult } from '@/types'

export function getMomentList(params: { pageNum: number; pageSize: number }): Promise<PageResult<Moment>> {
  return request.get('/moments', params)
}

export function getMomentDetail(id: number): Promise<Moment> {
  return request.get(`/moments/${id}`)
}

export function createMoment(data: MomentRequest): Promise<number> {
  return request.post('/moments', data)
}

export function updateMoment(data: MomentRequest): Promise<void> {
  return request.put('/moments', data)
}

export function updateMomentPublished(id: number, published: boolean): Promise<void> {
  return request.put(`/moments/${id}/published?published=${published}`)
}

export function deleteMoment(id: number): Promise<void> {
  return request.delete(`/moments/${id}`)
}
