<template>
  <div>
    <div class="nblog-page-title m-box">友人帐</div>
    <div class="nblog-friend-grid">
      <a
        v-for="link in friendLinks"
        :key="link.id"
        :href="link.url"
        target="_blank"
        rel="noopener noreferrer"
        class="nblog-friend-item m-box"
      >
        <img
          v-if="link.avatar"
          :src="link.avatar"
          :alt="link.name"
          class="nblog-friend-avatar"
        />
        <div v-else class="nblog-friend-avatar nblog-friend-avatar-placeholder">
          <el-icon size="24"><Link /></el-icon>
        </div>
        <div class="nblog-friend-name">{{ link.name }}</div>
        <div v-if="link.description" class="nblog-friend-desc">{{ link.description }}</div>
      </a>
    </div>
    <div v-if="!friendLinks.length" class="nblog-empty m-box">暂无友链</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Link } from '@element-plus/icons-vue'
import { useSettingsStore } from '@/stores/settings'
import type { FriendLink } from '@/types'

const friendLinks = ref<FriendLink[]>([])
const settingsStore = useSettingsStore()

onMounted(async () => {
  try {
    friendLinks.value = await settingsStore.fetchFriendLinks()
  } catch {
    friendLinks.value = []
  }
})
</script>

<style scoped>
.nblog-page-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  padding: 16px 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
  border-top: 3px solid #48dbfb;
}

.nblog-friend-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.nblog-friend-item {
  background: #fff;
  border-radius: 4px;
  padding: 20px 16px;
  text-align: center;
  transition: transform 0.2s;
}

.nblog-friend-item:hover {
  transform: translateY(-2px);
}

.nblog-friend-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  margin: 0 auto 10px;
  object-fit: cover;
}

.nblog-friend-avatar-placeholder {
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.nblog-friend-name {
  font-weight: 600;
  color: #333;
}

.nblog-friend-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.nblog-empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background: #fff;
  border-radius: 4px;
}
</style>
