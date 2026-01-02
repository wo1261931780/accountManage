// 通用响应结构
export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页请求
export interface PageRequest {
  current: number
  size: number
}

// 分页响应
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 用户信息
export interface UserInfo {
  id: number
  username: string
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
  status: number
  createTime: string
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应
export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

// 平台类型
export interface PlatformType {
  id: number
  typeCode: string
  typeName: string
  typeNameEn?: string
  icon?: string
  sortOrder: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

// 平台
export interface Platform {
  id: number
  platformCode: string
  platformName: string
  platformNameEn?: string
  platformTypeId: number
  platformTypeName?: string
  platformUrl?: string
  platformIcon?: string
  country?: string
  countryName?: string
  description?: string
  sortOrder: number
  status: number
  statusText?: string
  remark?: string
  createTime?: string
  updateTime?: string
  accountCount?: number
}

// 账号
export interface Account {
  id: number
  platformId: number
  platformName?: string
  platformTypeId?: number
  platformTypeName?: string
  platformUrl?: string
  accountName: string
  accountAlias?: string
  uid?: string
  serialNumber?: string
  bindPhone?: string
  bindEmail?: string
  password?: string
  hasSecurityTool: number
  securityToolType?: string
  accountLevel?: string
  accountStatus: number
  accountStatusText?: string
  accountPurpose?: string
  registerTime?: string
  lastLoginTime?: string
  expireTime?: string
  importanceLevel: number
  importanceLevelText?: string
  isMainAccount: number
  relatedAccountId?: number
  tags?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

// 账号创建/更新请求
export interface AccountForm {
  id?: number
  platformId: number
  accountName: string
  accountAlias?: string
  uid?: string
  serialNumber?: string
  bindPhone?: string
  bindEmail?: string
  password: string
  hasSecurityTool?: number
  securityToolType?: string
  accountLevel?: string
  accountStatus?: number
  accountPurpose?: string
  registerTime?: string
  lastLoginTime?: string
  expireTime?: string
  importanceLevel?: number
  isMainAccount?: number
  relatedAccountId?: number
  tags?: string
  remark?: string
  passwordValidDays?: number
}

// 账号查询请求
export interface AccountQuery extends PageRequest {
  keyword?: string
  platformId?: number
  platformTypeId?: number
  accountStatus?: number
  importanceLevel?: number
}

// 密码生成请求
export interface PasswordGenerateRequest {
  length?: number
  includeUppercase?: boolean
  includeLowercase?: boolean
  includeNumbers?: boolean
  includeSymbols?: boolean
  excludeChars?: string
}

// 密码强度
export interface PasswordStrength {
  score: number
  level: string
  suggestions: string[]
}

// 收藏账号
export interface FavoriteAccount {
  id: number
  accountId: number
  accountName: string
  platformName: string
  platformIcon?: string
  sortOrder: number
  createTime: string
}

// 最近访问账号
export interface RecentAccount {
  accountId: number
  accountName: string
  platformName: string
  platformIcon?: string
  lastAccessTime: string
  accessCount: number
}

// 仪表盘统计
export interface DashboardStats {
  totalAccounts: number
  totalPlatforms: number
  totalPlatformTypes: number
  activeAccounts: number
  expiringPasswords: number
  recentAccessCount: number
  platformTypeStats: PlatformTypeStats[]
  platformStats: PlatformStats[]
  dailyStats: DailyStats[]
}

export interface PlatformTypeStats {
  platformTypeId: number
  platformTypeName: string
  accountCount: number
  percentage: number
}

export interface PlatformStats {
  platformId: number
  platformName: string
  accountCount: number
}

export interface DailyStats {
  date: string
  accessCount: number
  createCount: number
}

// 标签
export interface Tag {
  id: number
  tagName: string
  tagColor?: string
  accountCount?: number
}
