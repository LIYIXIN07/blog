import { request } from './request'

export interface CopyrightInfo {
  title: string
  siteName: string
}

export interface SiteBadge {
  title: string
  url: string
  subject: string
  value: string
  color: string
}

export interface PublicSiteInfo {
  blogName?: string
  footerImgTitle: string
  footerImgUrl?: string
  copyright?: CopyrightInfo
  beian?: string
  badges: SiteBadge[]
}

export function getSiteInfo(): Promise<PublicSiteInfo> {
  return request.get('/site-info')
}
