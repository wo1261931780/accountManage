import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, LoginRequest } from '@/types'
import { login as loginApi, logout as logoutApi, getCurrentUser } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshToken = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<UserInfo | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')

  // 设置Token
  function setToken(accessToken: string, refresh?: string) {
    token.value = accessToken
    localStorage.setItem('token', accessToken)
    if (refresh) {
      refreshToken.value = refresh
      localStorage.setItem('refreshToken', refresh)
    }
  }

  // 清除Token
  function clearToken() {
    token.value = ''
    refreshToken.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  // 登录
  async function login(loginForm: LoginRequest) {
    try {
      const res = await loginApi(loginForm)
      if (res.code === 200 && res.data) {
        setToken(res.data.accessToken, res.data.refreshToken)
        await fetchUserInfo()
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 登出
  async function logout() {
    try {
      if (token.value) {
        await logoutApi()
      }
    } catch (error) {
      console.error('登出错误:', error)
    } finally {
      clearToken()
      userInfo.value = null
      router.push('/login')
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await getCurrentUser()
      if (res.code === 200 && res.data) {
        userInfo.value = res.data
        return res.data
      }
      return null
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }

  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    username,
    nickname,
    setToken,
    clearToken,
    login,
    logout,
    fetchUserInfo,
  }
})
