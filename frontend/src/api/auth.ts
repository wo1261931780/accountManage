import request from '@/utils/request'
import type { Result, LoginRequest, LoginResponse, UserInfo } from '@/types'

// 用户登录
export function login(data: LoginRequest): Promise<Result<LoginResponse>> {
  return request.post('/auth/login', data)
}

// 用户登出
export function logout(): Promise<Result<void>> {
  return request.post('/auth/logout')
}

// 刷新Token
export function refreshToken(refreshToken: string): Promise<Result<LoginResponse>> {
  return request.post('/auth/refresh', { refreshToken })
}

// 获取当前用户信息
export function getCurrentUser(): Promise<Result<UserInfo>> {
  return request.get('/auth/me')
}

// 修改密码
export function changePassword(oldPassword: string, newPassword: string): Promise<Result<void>> {
  return request.put('/auth/password', { oldPassword, newPassword })
}
