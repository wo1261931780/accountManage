import request from '@/utils/request'
import type { Result, DashboardStats, FavoriteAccount, RecentAccount, PageResult, PasswordStrength, PasswordGenerateRequest } from '@/types'

// ========== 仪表盘 ==========

// 获取仪表盘统计
export function getDashboardStats(): Promise<Result<DashboardStats>> {
  return request.get('/dashboard/stats')
}

// 获取账号总数
export function getTotalAccounts(): Promise<Result<number>> {
  return request.get('/dashboard/total-accounts')
}

// 获取平台总数
export function getTotalPlatforms(): Promise<Result<number>> {
  return request.get('/dashboard/total-platforms')
}

// ========== 收藏 ==========

// 获取收藏列表
export function getFavorites(page = 1, size = 20): Promise<Result<PageResult<FavoriteAccount>>> {
  return request.get('/favorites', { params: { current: page, size } })
}

// 添加收藏
export function addFavorite(accountId: number): Promise<Result<void>> {
  return request.post(`/favorites/${accountId}`)
}

// 取消收藏
export function removeFavorite(accountId: number): Promise<Result<void>> {
  return request.delete(`/favorites/${accountId}`)
}

// 置顶收藏
export function pinFavorite(accountId: number): Promise<Result<void>> {
  return request.post(`/favorites/${accountId}/pin`)
}

// 检查是否收藏
export function checkFavorite(accountId: number): Promise<Result<boolean>> {
  return request.get(`/favorites/${accountId}/check`)
}

// ========== 最近访问 ==========

// 获取最近访问
export function getRecentAccounts(limit = 10): Promise<Result<RecentAccount[]>> {
  return request.get('/recent', { params: { limit } })
}

// 获取最常访问
export function getFrequentAccounts(limit = 10): Promise<Result<RecentAccount[]>> {
  return request.get('/recent/frequent', { params: { limit } })
}

// ========== 密码工具 ==========

// 生成密码
export function generatePassword(options?: PasswordGenerateRequest): Promise<Result<string>> {
  if (options) {
    return request.post('/password/generate', options)
  }
  return request.get('/password/quick')
}

// 检测密码强度
export function checkPasswordStrength(password: string): Promise<Result<PasswordStrength>> {
  return request.post('/password/strength', { password })
}

// 获取密码过期提醒
export function getPasswordExpiration(): Promise<Result<any[]>> {
  return request.get('/password/expiration')
}
