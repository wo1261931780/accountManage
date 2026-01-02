import request from '@/utils/request'
import type { Result, PageResult, Account, AccountQuery, AccountForm } from '@/types'

// 分页查询账号
export function getAccountList(params: AccountQuery): Promise<Result<PageResult<Account>>> {
  return request.get('/accounts', { params })
}

// 获取账号详情
export function getAccountById(id: number): Promise<Result<Account>> {
  return request.get(`/accounts/${id}`)
}

// 创建账号
export function createAccount(data: AccountForm): Promise<Result<Account>> {
  return request.post('/accounts', data)
}

// 更新账号
export function updateAccount(id: number, data: AccountForm): Promise<Result<Account>> {
  return request.put(`/accounts/${id}`, data)
}

// 删除账号
export function deleteAccount(id: number): Promise<Result<void>> {
  return request.delete(`/accounts/${id}`)
}

// 批量删除账号
export function batchDeleteAccounts(ids: number[]): Promise<Result<void>> {
  return request.delete('/accounts/batch', { data: ids })
}

// 获取解密密码
export function getAccountPassword(id: number): Promise<Result<string>> {
  return request.get(`/accounts/${id}/password`)
}

// 二次验证获取密码
export function getPasswordWithVerify(id: number, verifyPassword: string): Promise<Result<string>> {
  return request.post(`/accounts/${id}/password/verify`, { password: verifyPassword })
}

// 高级搜索
export function searchAccounts(data: any): Promise<Result<PageResult<Account>>> {
  return request.post('/search/accounts', data)
}

// 快速搜索
export function quickSearch(keyword: string, page = 1, size = 10): Promise<Result<PageResult<Account>>> {
  return request.get('/search/quick', { params: { keyword, page, size } })
}

// 搜索建议
export function getSearchSuggestions(keyword: string, limit = 10): Promise<Result<string[]>> {
  return request.get('/search/suggestions', { params: { keyword, limit } })
}
