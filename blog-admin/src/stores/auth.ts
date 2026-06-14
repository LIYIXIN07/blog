import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/user'
import type { LoginRequest, UserInfo } from '@/types'

const TOKEN_KEY = 'admin_token'
const USER_KEY = 'admin_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const userInfo = ref<UserInfo | null>(readUser())

  const isLoggedIn = computed(() => Boolean(token.value))

  function readUser(): UserInfo | null {
    const raw = localStorage.getItem(USER_KEY)
    if (!raw) return null
    try {
      return JSON.parse(raw)
    } catch {
      return null
    }
  }

  function setSession(newToken: string, user: UserInfo) {
    token.value = newToken
    userInfo.value = user
    localStorage.setItem(TOKEN_KEY, newToken)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }

  async function login(data: LoginRequest) {
    const result = await loginApi(data)
    setSession(result.token, result.userInfo)
  }

  function updateUserInfo(user: Partial<UserInfo>) {
    if (!userInfo.value) return
    userInfo.value = { ...userInfo.value, ...user }
    localStorage.setItem(USER_KEY, JSON.stringify(userInfo.value))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, userInfo, isLoggedIn, login, logout, updateUserInfo }
})
