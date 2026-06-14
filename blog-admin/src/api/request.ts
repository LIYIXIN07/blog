import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://121.4.27.29:8080/api'

const service = axios.create({
  baseURL: API_BASE,
  timeout: 15000
})

service.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'
    if (error.response?.status === 401) {
      useAuthStore().logout()
      if (!location.pathname.includes('/login')) {
        location.href = '/login'
      }
    } else {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

export const request = {
  get<T = any>(url: string, params?: any): Promise<T> {
    return service.get(url, { params })
  },
  post<T = any>(url: string, data?: any): Promise<T> {
    return service.post(url, data)
  },
  put<T = any>(url: string, data?: any, params?: any): Promise<T> {
    return service.put(url, data, { params })
  },
  delete<T = any>(url: string, params?: any): Promise<T> {
    return service.delete(url, { params })
  }
}

export default service
