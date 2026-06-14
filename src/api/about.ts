import { request } from './request'

export interface AboutInfo {
  title: string
  musicId?: string
  content: string
  commentEnabled: boolean
}

export function getAboutInfo(): Promise<AboutInfo> {
  return request.get('/about')
}
