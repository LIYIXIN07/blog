const VISITOR_KEY = 'blog_visitor_uuid'

function createUuid(): string {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID()
  }
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

export function getVisitorUuid(): string {
  let uuid = localStorage.getItem(VISITOR_KEY)
  if (!uuid) {
    uuid = createUuid()
    localStorage.setItem(VISITOR_KEY, uuid)
  }
  return uuid
}

export function trackVisit(): void {
  const visitorUuid = getVisitorUuid()
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api'
  const pageUri = window.location.pathname + window.location.search

  fetch(`${baseUrl}/statistics/visit`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ visitorUuid, pageUri }),
    keepalive: true
  }).catch(() => {
    // 静默失败，不影响浏览体验
  })
}
