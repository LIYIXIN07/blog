/**
 * 前台 SEO：title、meta、canonical、Open Graph、JSON-LD
 */

import { useSettingsStore } from '@/stores/settings'

export interface SeoOptions {
  title?: string
  description?: string
  keywords?: string
  image?: string
  url?: string
  type?: 'website' | 'article'
  noindex?: boolean
  jsonLd?: Record<string, unknown> | Record<string, unknown>[] | null
}

const JSON_LD_ID = 'site-json-ld'

const DEFAULT_DESCRIPTION = '记录技术成长，分享学习心得'

export function getSiteOrigin(): string {
  const configured = import.meta.env.VITE_SITE_URL?.trim()
  if (configured) {
    return configured.replace(/\/$/, '')
  }
  if (typeof window !== 'undefined') {
    return window.location.origin
  }
  return ''
}

export function getSiteDefaults() {
  const store = useSettingsStore()
  const settings = store.settings
  return {
    siteName: settings?.siteName || import.meta.env.VITE_APP_TITLE || '个人博客',
    siteDescription: settings?.siteDescription || DEFAULT_DESCRIPTION,
    authorName: settings?.authorName || ''
  }
}

export function buildPageTitle(pageTitle?: string): string {
  const { siteName } = getSiteDefaults()
  if (!pageTitle || pageTitle === siteName) {
    return siteName
  }
  return `${pageTitle} - ${siteName}`
}

export function resolveAbsoluteUrl(url?: string): string | undefined {
  if (!url?.trim()) return undefined
  if (/^https?:\/\//i.test(url)) return url

  const apiBase = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')
  if (url.startsWith('/upload')) {
    return `${apiBase}${url}`
  }

  const origin = getSiteOrigin()
  return `${origin}${url.startsWith('/') ? url : `/${url}`}`
}

function upsertMetaByName(name: string, content: string) {
  let el = document.head.querySelector(`meta[name="${name}"]`) as HTMLMetaElement | null
  if (!el) {
    el = document.createElement('meta')
    el.setAttribute('name', name)
    document.head.appendChild(el)
  }
  el.setAttribute('content', content)
}

function upsertMetaByProperty(property: string, content: string) {
  let el = document.head.querySelector(`meta[property="${property}"]`) as HTMLMetaElement | null
  if (!el) {
    el = document.createElement('meta')
    el.setAttribute('property', property)
    document.head.appendChild(el)
  }
  el.setAttribute('content', content)
}

function upsertCanonical(url: string) {
  let el = document.head.querySelector('link[rel="canonical"]') as HTMLLinkElement | null
  if (!el) {
    el = document.createElement('link')
    el.setAttribute('rel', 'canonical')
    document.head.appendChild(el)
  }
  el.setAttribute('href', url)
}

function removeCanonical() {
  document.head.querySelector('link[rel="canonical"]')?.remove()
}

function upsertJsonLd(data: Record<string, unknown> | Record<string, unknown>[] | null | undefined) {
  document.getElementById(JSON_LD_ID)?.remove()
  if (!data) return

  const script = document.createElement('script')
  script.id = JSON_LD_ID
  script.type = 'application/ld+json'
  script.textContent = JSON.stringify(Array.isArray(data) ? data : data)
  document.head.appendChild(script)
}

export function applySeo(options: SeoOptions = {}) {
  const { siteName, siteDescription, authorName } = getSiteDefaults()
  const origin = getSiteOrigin()
  const pageUrl = options.url || (origin ? `${origin}${window.location.pathname}` : window.location.pathname)
  const pageTitle = options.title || siteName
  const description = (options.description || siteDescription).slice(0, 160)
  const image = resolveAbsoluteUrl(options.image)
  const type = options.type || 'website'

  document.title = buildPageTitle(pageTitle)

  upsertMetaByName('description', description)
  if (options.keywords) {
    upsertMetaByName('keywords', options.keywords)
  }
  upsertMetaByName('robots', options.noindex ? 'noindex, nofollow' : 'index, follow')
  if (authorName) {
    upsertMetaByName('author', authorName)
  }

  upsertMetaByProperty('og:site_name', siteName)
  upsertMetaByProperty('og:title', buildPageTitle(pageTitle))
  upsertMetaByProperty('og:description', description)
  upsertMetaByProperty('og:type', type)
  upsertMetaByProperty('og:url', pageUrl)
  upsertMetaByProperty('og:locale', 'zh_CN')
  if (image) {
    upsertMetaByProperty('og:image', image)
  }

  upsertMetaByName('twitter:card', image ? 'summary_large_image' : 'summary')
  upsertMetaByName('twitter:title', buildPageTitle(pageTitle))
  upsertMetaByName('twitter:description', description)
  if (image) {
    upsertMetaByName('twitter:image', image)
  }

  if (options.noindex) {
    removeCanonical()
  } else if (pageUrl) {
    upsertCanonical(pageUrl)
  }

  upsertJsonLd(options.jsonLd)
}

export function buildWebsiteJsonLd() {
  const { siteName, siteDescription } = getSiteDefaults()
  const origin = getSiteOrigin()
  if (!origin) return null

  return {
    '@context': 'https://schema.org',
    '@type': 'WebSite',
    name: siteName,
    description: siteDescription,
    url: origin,
    inLanguage: 'zh-CN'
  }
}

export function buildBlogPostingJsonLd(article: {
  title: string
  summary?: string
  coverImage?: string
  author?: string
  createdAt?: string
  updatedAt?: string
  id: number
}) {
  const { siteName } = getSiteDefaults()
  const origin = getSiteOrigin()
  if (!origin) return null

  return {
    '@context': 'https://schema.org',
    '@type': 'BlogPosting',
    headline: article.title,
    description: article.summary || article.title,
    image: resolveAbsoluteUrl(article.coverImage),
    author: {
      '@type': 'Person',
      name: article.author || siteName
    },
    publisher: {
      '@type': 'Organization',
      name: siteName
    },
    datePublished: article.createdAt,
    dateModified: article.updatedAt || article.createdAt,
    mainEntityOfPage: {
      '@type': 'WebPage',
      '@id': `${origin}/article/${article.id}`
    }
  }
}

export function applyRouteSeo(meta: {
  title?: string
  description?: string
  noindex?: boolean
}) {
  const { siteDescription } = getSiteDefaults()
  const jsonLd = meta.title === '首页' || meta.title === undefined ? buildWebsiteJsonLd() : null

  applySeo({
    title: meta.title,
    description: meta.description || siteDescription,
    noindex: meta.noindex,
    jsonLd
  })
}
