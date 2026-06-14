import { request } from './request'

export interface AboutPage {
  title: string
  musicId?: string
  content: string
  commentEnabled: boolean
}

export function getAbout(): Promise<AboutPage> {
  return request.get('/about')
}

export function updateAbout(data: AboutPage): Promise<void> {
  return request.put('/about', data)
}
