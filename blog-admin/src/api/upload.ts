import service from './request'

export interface UploadResult {
  url: string
  filename: string
}

export function uploadImage(file: File): Promise<UploadResult> {
  const formData = new FormData()
  formData.append('file', file)
  return service.post('/upload', formData)
}
