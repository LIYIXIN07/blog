/**
 * Markdown解析工具
 */

import { marked } from 'marked'
import hljs from 'highlight.js'

const CN_NUM = '[一二三四五六七八九十百千万]+'
const IMAGE_EXT = /\.(jpe?g|png|gif|webp|bmp|svg|avif)(\?|#|$)/i
const STANDALONE_URL = /^https?:\/\/\S+$/i

function tryDecode(value: string): string {
  try {
    return decodeURIComponent(value)
  } catch {
    return value
  }
}

/**
 * 从搜索页链接或直接地址中提取可展示的图片 URL
 */
export function extractDirectImageUrl(url: string): string | null {
  const trimmed = url.trim()
  if (!trimmed) return null

  const withoutHash = trimmed.split('#')[0]
  if (IMAGE_EXT.test(withoutHash)) {
    return trimmed
  }

  try {
    const parsed = new URL(trimmed)
    const candidates = [
      parsed.searchParams.get('mediaurl'),
      parsed.searchParams.get('imgurl'),
      parsed.searchParams.get('objurl'),
      parsed.searchParams.get('thumburl'),
      parsed.searchParams.get('src'),
    ]

    for (const candidate of candidates) {
      if (!candidate) continue
      const decoded = tryDecode(candidate)
      if (decoded.startsWith('http')) {
        return decoded
      }
    }
  } catch {
    // ignore invalid URL
  }

  const mediaMatch = trimmed.match(/[?&]mediaurl=([^&]+)/i)
  if (mediaMatch) {
    const decoded = tryDecode(mediaMatch[1])
    if (decoded.startsWith('http')) return decoded
  }

  const objMatch = trimmed.match(/[?&]objurl=([^&]+)/i)
  if (objMatch) {
    const decoded = tryDecode(objMatch[1])
    if (decoded.startsWith('http')) return decoded
  }

  return null
}

function toMarkdownImage(url: string): string {
  return `![](${url})`
}

/**
 * 规范化 Markdown 正文：修复粘贴后标题、加粗、图片链接等格式无法识别的问题
 */
export function normalizeMarkdownContent(content: string): string {
  if (!content?.trim()) return content ?? ''

  let text = content
    .replace(/\r\n/g, '\n')
    .replace(/\r/g, '\n')
    .replace(/[\u200B-\u200D\uFEFF]/g, '')
    .replace(/＃/g, '#')
    .replace(/＊/g, '*')
    .trim()

  text = text.replace(/([^\n#])(#{1,6})(?=\s)/g, '$1\n$2')
  text = text.replace(/^(#{1,6})([^\s#])/gm, '$1 $2')
  text = text.replace(
    new RegExp(`([。！？!?])\\s*(${CN_NUM})[、．.]`, 'g'),
    '$1\n\n$2、'
  )
  text = text.replace(/([。！？!?，,])\s*(\*\*[^*\n]+\*\*)/g, '$1\n\n$2')

  const lines = text.split('\n')
  const result: string[] = []

  for (const line of lines) {
    const trimmed = line.trim()
    if (!trimmed) {
      if (result.length > 0 && result[result.length - 1] !== '') {
        result.push('')
      }
      continue
    }

    if (/^!\[[^\]]*]\([^)]+\)$/.test(trimmed)) {
      result.push(trimmed)
      continue
    }

    if (STANDALONE_URL.test(trimmed)) {
      const imageUrl = extractDirectImageUrl(trimmed)
      if (imageUrl) {
        result.push(toMarkdownImage(imageUrl))
        continue
      }
    }

    if (/^#{1,6}\s/.test(trimmed)) {
      if (result.length > 0 && result[result.length - 1] !== '') {
        result.push('')
      }
      result.push(trimmed)
      continue
    }

    const cnHeading = trimmed.match(new RegExp(`^(${CN_NUM})[、．.]\\s*(.+)$`))
    if (cnHeading && cnHeading[2].length <= 80 && !/^#{1,6}\s/.test(trimmed)) {
      if (result.length > 0 && result[result.length - 1] !== '') {
        result.push('')
      }
      result.push(`## ${cnHeading[1]}、${cnHeading[2]}`)
      continue
    }

    const chapterHeading = trimmed.match(new RegExp(`^第(${CN_NUM}|\\d+)[章节部分篇][：:]?\\s*(.*)$`))
    if (chapterHeading) {
      if (result.length > 0 && result[result.length - 1] !== '') {
        result.push('')
      }
      result.push(`## ${trimmed}`)
      continue
    }

    if (/^\*\*.+\*\*$/.test(trimmed) && trimmed.length <= 120) {
      if (result.length > 0 && result[result.length - 1] !== '') {
        result.push('')
      }
      result.push(trimmed)
      continue
    }

    result.push(trimmed)
  }

  return result.join('\n').replace(/\n{3,}/g, '\n\n').trim()
}

marked.use({
  renderer: {
    code(code: string, infostring: string | undefined) {
      const language = infostring || ''
      if (language && hljs.getLanguage(language)) {
        try {
          const highlighted = hljs.highlight(code, { language }).value
          return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>`
        } catch (err) {
          console.error('代码高亮错误:', err)
        }
      }
      const highlighted = hljs.highlightAuto(code).value
      return `<pre><code class="hljs">${highlighted}</code></pre>`
    },
    image(href: string, title: string | null, text: string) {
      const titleAttr = title ? ` title="${title}"` : ''
      const alt = text || 'image'
      return `<img src="${href}" alt="${alt}"${titleAttr} referrerpolicy="no-referrer" loading="lazy" />`
    },
  },
  gfm: true,
  breaks: true,
})

/**
 * 解析Markdown为HTML，并为标题注入与目录一致的 id
 */
export interface TocHeading {
  level: number
  text: string
  id: string
}

function createHeadingId(text: string, used: Map<string, number>): string {
  let base = text
    .toLowerCase()
    .trim()
    .replace(/\s+/g, '-')
    .replace(/[^\w\u4e00-\u9fa5-]/g, '')
  if (!base) {
    base = 'heading'
  }
  const count = used.get(base) ?? 0
  used.set(base, count + 1)
  return count === 0 ? base : `${base}-${count}`
}

export function extractHeadings(markdown: string): TocHeading[] {
  const normalized = normalizeMarkdownContent(markdown)
  const headings: TocHeading[] = []
  const usedIds = new Map<string, number>()
  const regex = /^(#{1,6})\s+(.+)$/gm
  let match

  while ((match = regex.exec(normalized)) !== null) {
    const level = match[1].length
    const text = match[2].trim()
    headings.push({
      level,
      text,
      id: createHeadingId(text, usedIds),
    })
  }

  return headings
}

function injectHeadingIds(html: string, headings: TocHeading[]): string {
  let index = 0
  return html.replace(/<h([1-6])([^>]*)>([\s\S]*?)<\/h\1>/g, (match, level, attrs, inner) => {
    if (index >= headings.length) {
      return match
    }
    const { id } = headings[index++]
    if (/\bid\s*=/.test(attrs)) {
      return match
    }
    return `<h${level} id="${id}"${attrs}>${inner}</h${level}>`
  })
}

export function parseMarkdown(markdown: string): string {
  const normalized = normalizeMarkdownContent(markdown)
  const headings = extractHeadings(normalized)
  const html = marked.parse(normalized) as string
  return injectHeadingIds(html, headings)
}

/**
 * 获取Markdown中的标题列表（用于目录）
 * @param markdown Markdown文本
 */
export function getHeadings(markdown: string): TocHeading[] {
  return extractHeadings(markdown)
}

/**
 * 从Markdown中提取纯文本
 */
export function extractText(markdown: string, length: number = 200): string {
  const html = parseMarkdown(markdown)
  const text = html.replace(/<[^>]+>/g, '')
  const cleanText = text.replace(/\s+/g, ' ').trim()
  return cleanText.length > length ? cleanText.substring(0, length) + '...' : cleanText
}
