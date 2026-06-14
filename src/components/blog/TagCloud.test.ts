import { describe, it, expect } from 'vitest'

const PALETTE = [
  '#21ba45', '#a5673f', '#f2711c', '#2185d0', '#6435c9', '#db2828'
]

function tagColor(color: string | undefined, index: number): string {
  return color || PALETTE[index % PALETTE.length]
}

describe('tag cloud colors', () => {
  it('uses tag color when provided', () => {
    expect(tagColor('#ff0000', 0)).toBe('#ff0000')
  })

  it('falls back to palette by index', () => {
    expect(tagColor(undefined, 1)).toBe(PALETTE[1])
    expect(tagColor(undefined, 7)).toBe(PALETTE[1])
  })
})

describe('tag cloud collapse', () => {
  const COLLAPSED_HEIGHT = 118

  function shouldShowToggle(scrollHeight: number): boolean {
    return scrollHeight > COLLAPSED_HEIGHT + 8
  }

  it('shows toggle when content overflows', () => {
    expect(shouldShowToggle(200)).toBe(true)
    expect(shouldShowToggle(120)).toBe(false)
  })
})
