<template>
  <div v-if="ready && musicId" class="music-shell">
    <meting-js
      server="netease"
      type="song"
      :id="musicId"
      theme="#25CCF7"
      mutex="true"
      preload="metadata"
      lock="true"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useMusicStore } from '@/stores/music'

const musicStore = useMusicStore()
const { musicId } = storeToRefs(musicStore)
const ready = ref(false)

function cleanupLegacyPlayers() {
  document.querySelectorAll('#persistent-music-host, .about-music-host, meting-js').forEach((el) => {
    el.remove()
  })
}

onMounted(async () => {
  cleanupLegacyPlayers()
  await musicStore.ensureMusicId()

  const wait = () => {
    if ((window as Window & { APlayer?: unknown }).APlayer && customElements.get('meting-js')) {
      ready.value = true
      return
    }
    requestAnimationFrame(wait)
  }
  wait()
})
</script>

<style>
.music-shell {
  position: fixed;
  bottom: 0;
  right: 0;
  left: auto;
  z-index: 9999;
  width: min(480px, 66vw);
}

.music-shell .aplayer {
  margin: 0 !important;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.15);
  border-radius: 4px 0 0 0;
}

.music-shell .aplayer-lrc p {
  text-align: unset;
}

@media (max-width: 768px) {
  .music-shell {
    left: 0;
    right: 0;
    width: 100%;
    max-width: none;
  }

  .music-shell .aplayer {
    border-radius: 0;
  }

  .music-shell .aplayer-body {
    max-width: 100%;
  }

  .music-shell .aplayer-info {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .nblog-site {
    padding-bottom: 72px;
  }
}
</style>
