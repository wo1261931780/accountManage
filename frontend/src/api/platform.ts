import request from '@/utils/request'
import type { Result, PageResult, Platform, PlatformType, PageRequest } from '@/types'

// ========== 平台类型 ==========

// 获取所有平台类型
export function getAllPlatformTypes(): Promise<Result<PlatformType[]>> {
  return request.get('/platform-types')
}

// 获取平台类型详情
export function getPlatformTypeById(id: number): Promise<Result<PlatformType>> {
  return request.get(`/platform-types/${id}`)
}

// 创建平台类型
export function createPlatformType(data: Partial<PlatformType>): Promise<Result<PlatformType>> {
  return request.post('/platform-types', data)
}

// 更新平台类型
export function updatePlatformType(id: number, data: Partial<PlatformType>): Promise<Result<PlatformType>> {
  return request.put(`/platform-types/${id}`, data)
}

// 删除平台类型
export function deletePlatformType(id: number): Promise<Result<void>> {
  return request.delete(`/platform-types/${id}`)
}

// ========== 平台 ==========

// 分页查询平台
export function getPlatformList(params: PageRequest & { platformTypeId?: number; keyword?: string }): Promise<Result<PageResult<Platform>>> {
  return request.get('/platforms', { params })
}

// 获取平台详情
export function getPlatformById(id: number): Promise<Result<Platform>> {
  return request.get(`/platforms/${id}`)
}

// 获取平台类型下的所有平台
export function getPlatformsByType(typeId: number): Promise<Result<Platform[]>> {
  return request.get(`/platforms/type/${typeId}`)
}

// 创建平台
export function createPlatform(data: Partial<Platform>): Promise<Result<Platform>> {
  return request.post('/platforms', data)
}

// 更新平台
export function updatePlatform(id: number, data: Partial<Platform>): Promise<Result<Platform>> {
  return request.put(`/platforms/${id}`, data)
}

// 删除平台
export function deletePlatform(id: number): Promise<Result<void>> {
  return request.delete(`/platforms/${id}`)
}
