import dayjs from 'dayjs'

export function formatDate(date: string, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  return dayjs(date).format(format)
}

export function formatRelativeTime(date: string): string {
  const now = dayjs()
  const target = dayjs(date)
  const diff = now.diff(target, 'day')
  
  if (diff === 0) {
    const hourDiff = now.diff(target, 'hour')
    if (hourDiff === 0) {
      const minuteDiff = now.diff(target, 'minute')
      return minuteDiff <= 0 ? '刚刚' : `${minuteDiff}分钟前`
    }
    return `${hourDiff}小时前`
  } else if (diff === 1) {
    return '昨天'
  } else if (diff < 7) {
    return `${diff}天前`
  } else {
    return formatDate(date, 'YYYY-MM-DD')
  }
}

export function getToday(): string {
  return dayjs().format('YYYY-MM-DD')
}

export function getWeekStart(): string {
  return dayjs().startOf('week').format('YYYY-MM-DD')
}

export function getMonthStart(): string {
  return dayjs().startOf('month').format('YYYY-MM-DD')
}

export function getYearStart(): string {
  return dayjs().startOf('year').format('YYYY-MM-DD')
}