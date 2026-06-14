/**
 * 上线前 API 冒烟测试（需后端运行在 API_BASE）
 * 用法: node scripts/test-api.mjs
 * 环境变量: API_BASE=http://localhost:8080/api
 */
const API_BASE = process.env.API_BASE || 'http://localhost:8080/api'

const PUBLIC_GETS = [
  '/about',
  '/settings',
  '/tags',
  '/categories',
  '/articles?pageNum=1&pageSize=5',
  '/articles/latest?limit=5',
  '/articles/random?limit=3',
  '/articles/archives',
  '/friend-links',
  '/moments/public',
  '/statistics/site',
  '/comments/public?pageType=1&pageNum=1&pageSize=5'
]

let passed = 0
let failed = 0

async function check(name, fn) {
  try {
    await fn()
    console.log(`  ✓ ${name}`)
    passed++
  } catch (err) {
    console.error(`  ✗ ${name}`)
    console.error(`    ${err.message}`)
    failed++
  }
}

async function getJson(path) {
  const res = await fetch(`${API_BASE}${path}`)
  if (!res.ok) {
    throw new Error(`${path} -> HTTP ${res.status}`)
  }
  const json = await res.json()
  if (json.code !== 200) {
    throw new Error(`${path} -> code ${json.code}: ${json.message}`)
  }
  return json.data
}

console.log(`\nAPI 冒烟测试  ${API_BASE}\n`)

await check('GET /about 含 musicId', async () => {
  const data = await getJson('/about')
  if (!data?.musicId) throw new Error('musicId 缺失')
})

await check('GET /settings 含 siteName', async () => {
  const data = await getJson('/settings')
  if (!data?.siteName) throw new Error('siteName 缺失')
})

await check('GET /tags 返回数组', async () => {
  const data = await getJson('/tags')
  if (!Array.isArray(data)) throw new Error('tags 不是数组')
  if (data.length === 0) throw new Error('tags 为空')
})

await check('GET /categories 返回数组', async () => {
  const data = await getJson('/categories')
  if (!Array.isArray(data)) throw new Error('categories 不是数组')
})

for (const path of PUBLIC_GETS) {
  const label = `GET ${path.split('?')[0]}`
  if (['/about', '/settings', '/tags', '/categories'].some((p) => path.startsWith(p))) {
    continue
  }
  await check(label, async () => {
    await getJson(path)
  })
}

await check('POST /auth/login 管理员', async () => {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'admin', password: 'admin123' })
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  const json = await res.json()
  if (json.code !== 200 || !json.data?.token) {
    throw new Error('登录未返回 token')
  }
})

await check('POST /auth/login 错误密码应失败', async () => {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'admin', password: 'wrong' })
  })
  if (res.ok) throw new Error('错误密码不应返回 200')
})

console.log(`\n结果: ${passed} 通过, ${failed} 失败\n`)
process.exit(failed > 0 ? 1 : 0)
