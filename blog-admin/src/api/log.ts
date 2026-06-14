import { request } from './request'
import type {
  AccessLog,
  ExceptionLog,
  LoginLog,
  LogType,
  OperationLog,
  PageResult,
  TaskLog
} from '@/types'

type LogRecord = LoginLog | OperationLog | ExceptionLog | AccessLog | TaskLog

const logEndpoints: Record<LogType, string> = {
  login: '/logs/login',
  operation: '/logs/operation',
  exception: '/logs/exception',
  access: '/logs/access',
  task: '/logs/task'
}

export function getLogList<T extends LogRecord>(
  type: LogType,
  params: {
    pageNum: number
    pageSize: number
    startTime?: string
    endTime?: string
    visitorUuid?: string
  }
): Promise<PageResult<T>> {
  return request.get(logEndpoints[type], params)
}

export function deleteLog(type: LogType, id: number): Promise<void> {
  return request.delete(`/logs/${type}/${id}`)
}
