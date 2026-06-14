import { request } from './request'

export interface SiteStats {
  totalPv: number
  totalUv: number
  totalArticles: number
  runDays: number
}

export interface CityVisitor {
  city: string
  uv: number
}

export function getSiteStats(): Promise<SiteStats> {
  return request.get('/statistics/site')
}

export function getVisitorMap(): Promise<CityVisitor[]> {
  return request.get('/statistics/visitor-map')
}
