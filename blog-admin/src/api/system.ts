import { request } from './request'
import type {
  AccountUpdateRequest,
  PageRequest,
  PageResult,
  ScheduledTask,
  ScheduledTaskRequest
} from '@/types'

export function updateAccount(data: AccountUpdateRequest): Promise<void> {
  return request.put('/system/account', data)
}

export function getScheduledTasks(params: PageRequest): Promise<PageResult<ScheduledTask>> {
  return request.get('/system/tasks', params)
}

export function createScheduledTask(data: ScheduledTaskRequest): Promise<void> {
  return request.post('/system/tasks', data)
}

export function updateScheduledTask(id: number, data: ScheduledTaskRequest): Promise<void> {
  return request.put(`/system/tasks/${id}`, data)
}

export function deleteScheduledTask(id: number): Promise<void> {
  return request.delete(`/system/tasks/${id}`)
}

export function runScheduledTask(id: number): Promise<void> {
  return request.post(`/system/tasks/${id}/run`)
}
