import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useMusicStore, DEFAULT_MUSIC_ID } from '@/stores/music'

vi.mock('@/api/about', () => ({
  getAboutInfo: vi.fn()
}))

import { getAboutInfo } from '@/api/about'

describe('music store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.mocked(getAboutInfo).mockReset()
  })

  it('uses default music id when api fails', async () => {
    vi.mocked(getAboutInfo).mockRejectedValue(new Error('network'))
    const store = useMusicStore()
    const id = await store.ensureMusicId()
    expect(id).toBe(DEFAULT_MUSIC_ID)
  })

  it('maps legacy music id to default', async () => {
    vi.mocked(getAboutInfo).mockResolvedValue({
      title: '关于我',
      musicId: '423015580',
      content: '',
      commentEnabled: true
    })
    const store = useMusicStore()
    const id = await store.ensureMusicId()
    expect(id).toBe(DEFAULT_MUSIC_ID)
  })

  it('caches music id after first load', async () => {
    vi.mocked(getAboutInfo).mockResolvedValue({
      title: '关于我',
      musicId: '1473552803',
      content: '',
      commentEnabled: true
    })
    const store = useMusicStore()
    await store.ensureMusicId()
    await store.ensureMusicId()
    expect(getAboutInfo).toHaveBeenCalledTimes(1)
  })
})
