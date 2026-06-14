/**
 * 认证状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/auth'
import { login as loginApi, logout as logoutApi, getCurrentUser } from '@/api/user'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string | null>(getToken())
  const userInfo = ref<UserInfo | null>(getUserInfo())
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const nickname = computed(() => userInfo.value?.nickname || '')
  const avatar = computed(() => userInfo.value?.avatar || '')
  
  /**
   * 登录
   */
  async function login(username: string, password: string) {
    const response = await loginApi({ username, password })
    
    // 保存token和用户信息
    token.value = response.token
    userInfo.value = response.userInfo
    
    setToken(response.token)
    setUserInfo(response.userInfo)
    
    return response
  }
  
  /**
   * 登出
   */
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 清除本地状态
      token.value = null
      userInfo.value = null
      removeToken()
      removeUserInfo()
    }
  }
  
  /**
   * 获取用户信息
   */
  async function fetchUserInfo() {
    if (!token.value) return null
    
    try {
      const user = await getCurrentUser()
      userInfo.value = user
      setUserInfo(user)
      return user
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }
  
  /**
   * 检查登录状态
   */
  function checkAuth() {
    return !!token.value
  }
  
  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isLoggedIn,
    username,
    nickname,
    avatar,
    // 方法
    login,
    logout,
    fetchUserInfo,
    checkAuth
  }
})