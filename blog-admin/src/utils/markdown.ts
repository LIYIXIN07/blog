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

  // 行内出现的 Markdown 标题，拆到新行
  text = text.replace(/([^\n#])(#{1,6})(?=\s)/g, '$1\n$2')

  // # 后缺少空格
  text = text.replace(/^(#{1,6})([^\s#])/gm, '$1 $2')

  // 句号后的中文序号标题拆行，例如：……能力。四、标题
  text = text.replace(
    new RegExp(`([。！？!?])\\s*(${CN_NUM})[、．.]`, 'g'),
    '$1\n\n$2、'
  )

  // 句号或逗号后的独立加粗标题拆行
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
