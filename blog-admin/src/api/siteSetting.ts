import { request } from './request'

export interface SiteSettingItem {
  id?: number
  nameEn: string
  nameZh: string
  value: string
  type: number
  key?: number
}

export interface BadgeValue {
  title: string
  url: string
  subject: string
  value: string
  color: string
}

export interface SiteSettingBadge extends Omit<SiteSettingItem, 'value'> {
  value: BadgeValue
}

export interface SiteSettingGroup {
  type1: SiteSettingItem[]
  type2: SiteSettingItem[]
  type3: SiteSettingBadge[]
}

export function getSiteSettingData(): Promise<SiteSettingGroup> {
  return request.get('/site-settings')
}

export function updateSiteSettings(
  settings: SiteSettingItem[],
  deleteIds: number[]
): Promise<void> {
  return request.put('/site-settings', { settings, deleteIds })
}
