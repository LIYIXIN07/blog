import { request } from './request'
import type { DashboardStats, SiteStats, VisitRecord, PageResult } from '@/types'

export function getDashboardStats(): Promise<DashboardStats> {
  return request.get('/statistics/dashboard')
}

export function getSiteStats(): Promise<SiteStats> {
  return request.get('/statistics/site')
}

export function getVisitorList(params: {
  pageNum: number
  pageSize: number
  startTime?: string
  endTime?: string
}): Promise<PageResult<VisitRecord>> {
  return request.get('/statistics/visitors', params)
}

export function deleteVisitor(id: number): Promise<void> {
  return request.delete(`/statistics/visitors/${id}`)
}
