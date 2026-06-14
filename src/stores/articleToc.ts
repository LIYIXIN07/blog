import { defineStore } from 'pinia'
import type { TocHeading } from '@/utils/markdown'

export const useArticleTocStore = defineStore('articleToc', {
  state: () => ({
    headings: [] as TocHeading[],
    activeId: '' as string,
  }),
  actions: {
    setHeadings(headings: TocHeading[]) {
      this.headings = headings
      this.activeId = headings[0]?.id ?? ''
    },
    setActiveId(id: string) {
      this.activeId = id
    },
    clear() {
      this.headings = []
      this.activeId = ''
    },
  },
})
