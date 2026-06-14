/**
 * 网站设置状态管理
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserSettings, FriendLink } from '@/types'
import { getSettings, updateSettings, getFriendLinks } from '@/api/user'

export const useSettingsStore = defineStore('settings', () => {
  // 状态
  const settings = ref<UserSettings | null>(null)
  const friendLinks = ref<FriendLink[]>([])
  const loading = ref(false)
  
  /**
   * 获取网站设置
   */
  async function fetchSettings() {
    loading.value = true
    try {
      const data = await getSettings()
      settings.value = data
      return data
    } catch (error) {
      console.error('获取设置失败:', error)
      return null
    } finally {
      loading.value = false
    }
  }
  
  /**
   * 更新网站设置
   */
  async function saveSettings(data: UserSettings) {
    loading.value = true
    try {
      await updateSettings(data)
      settings.value = data
      return true
    } catch (error) {
      console.error('保存设置失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }
  
  /**
   * 获取友情链接
   */
  async function fetchFriendLinks() {
    try {
      const data = await getFriendLinks()
      friendLinks.value = data
      return data
    } catch (error) {
      console.error('获取友情链接失败:', error)
      return []
    }
  }
  
  return {
    // 状态
    settings,
    friendLinks,
    loading,
    // 方法
    fetchSettings,
    saveSettings,
    fetchFriendLinks
  }
})