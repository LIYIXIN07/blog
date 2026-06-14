import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAboutInfo } from '@/api/about'

/** Childhood Dreams - Gaminl */
export const DEFAULT_MUSIC_ID = '1473552803'
const LEGACY_MUSIC_ID = '423015580'

export const useMusicStore = defineStore('music', () => {
  const musicId = ref('')
  const loaded = ref(false)

  async function ensureMusicId() {
    if (loaded.value) {
      return musicId.value
    }
    try {
      const data = await getAboutInfo()
      const id = data.musicId || DEFAULT_MUSIC_ID
      musicId.value = id === LEGACY_MUSIC_ID ? DEFAULT_MUSIC_ID : id
    } catch {
      musicId.value = DEFAULT_MUSIC_ID
    }
    loaded.value = true
    return musicId.value
  }

  return {
    musicId,
    loaded,
    ensureMusicId
  }
})
