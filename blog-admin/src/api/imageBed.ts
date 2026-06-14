import { request } from './request'
import type { ImageBedConfig, ImageBedConfigRequest } from '@/types'

export function getImageBedConfigs(): Promise<ImageBedConfig[]> {
  return request.get('/image-bed')
}

export function getImageBedConfig(type: string): Promise<ImageBedConfig> {
  return request.get(`/image-bed/${type}`)
}

export function updateImageBedConfig(type: string, data: ImageBedConfigRequest): Promise<void> {
  return request.put(`/image-bed/${type}`, data)
}
