# 🔐 AccountManage - 账号密码管理系统

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.4-blue" alt="Vue 3">
  <img src="https://img.shields.io/badge/Element%20Plus-2.5-409eff" alt="Element Plus">
  <img src="https://img.shields.io/badge/MyBatis%20Plus-3.5.9-orange" alt="MyBatis Plus">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479a1" alt="MySQL">
  <img src="https://img.shields.io/badge/License-AGPL--3.0-blue" alt="License">
</p>

<p align="center">
  一个安全、便捷、高效的账号密码管理系统，帮助您安全存储和管理各类平台账号信息。
</p>

---

## 📋 目录

- [功能特性](#-功能特性)
- [技术架构](#-技术架构)
- [项目结构](#-项目结构)
- [快速开始](#-快速开始)
- [系统截图](#-系统截图)
- [API 文档](#-api-文档)
- [安全特性](#-安全特性)
- [开发计划](#-开发计划)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)

---

## ✨ 功能特性

### 核心功能

| 模块 | 功能描述 |
|------|----------|
| 🔑 **账号管理** | 支持多平台账号的增删改查，密码加密存储，支持密码查看与复制 |
| 🏢 **平台管理** | 管理各类平台信息，支持平台分类、图标设置、URL 记录 |
| 📊 **仪表盘** | 数据统计可视化，快捷操作入口，最近访问记录 |
| ⭐ **收藏功能** | 常用账号收藏，支持置顶排序 |
| 🛠️ **密码工具** | 随机密码生成器，密码强度检测，过期提醒 |
| 🔍 **智能搜索** | 全局搜索，多条件筛选，搜索建议 |

### 安全特性

- 🔒 **AES-256 加密**: 所有密码采用 AES-256 对称加密存储
- 🎫 **JWT 认证**: 基于 JWT 的无状态身份认证
- 🔄 **Token 刷新**: 支持 Access Token / Refresh Token 双令牌机制
- 🚦 **接口限流**: 基于注解的接口限流，防止暴力破解
- 📝 **操作日志**: 完整的操作日志记录，支持审计追溯
- 🔐 **密钥轮换**: 支持加密密钥定期轮换

---

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.1 | 后端框架 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.0+ | 缓存（可选） |
| JWT | - | 身份认证 |
| Knife4j | 4.5.0 | API 文档 |
| Lombok | - | 简化代码 |
| BCrypt | - | 用户密码哈希 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.4 | 前端框架 |
| TypeScript | 5.4 | 类型安全 |
| Vite | 5.1 | 构建工具 |
| Element Plus | 2.5 | UI 组件库 |
| Pinia | 2.1 | 状态管理 |
| Vue Router | 4.3 | 路由管理 |
| Axios | 1.6 | HTTP 客户端 |
| ECharts | 5.5 | 数据可视化 |

### 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端 (Vue 3 + Element Plus)                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐ │
│  │  登录页   │  │  仪表盘   │  │ 账号管理  │  │ 平台/收藏/工具   │ │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘ │
└──────────────────────────────┬──────────────────────────────────┘
                               │ HTTP/REST API
┌──────────────────────────────▼──────────────────────────────────┐
│                     后端 (Spring Boot 3)                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ Controller  │──│   Service   │──│       Repository        │  │
│  │  (REST API) │  │ (业务逻辑)  │  │   (MyBatis Plus DAO)    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ JWT Filter  │  │  AES 加密   │  │      限流 Aspect        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                       数据存储层                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                      MySQL 8.0+                            │  │
│  │  sys_user | platform_type | platform | account | favorites │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 项目结构

```
accountManage/
├── frontend/                          # 前端项目
│   ├── src/
│   │   ├── api/                       # API 接口
│   │   ├── components/                # 公共组件
│   │   ├── layout/                    # 布局组件
│   │   ├── router/                    # 路由配置
│   │   ├── stores/                    # Pinia 状态管理
│   │   ├── styles/                    # 全局样式
│   │   ├── types/                     # TypeScript 类型
│   │   ├── utils/                     # 工具函数
│   │   └── views/                     # 页面视图
│   │       ├── login/                 # 登录页
│   │       ├── dashboard/             # 仪表盘
│   │       ├── account/               # 账号管理
│   │       ├── platform/              # 平台管理
│   │       ├── platform-type/         # 平台类型
│   │       ├── favorites/             # 收藏管理
│   │       ├── password-tools/        # 密码工具
│   │       └── settings/              # 系统设置
│   ├── package.json
│   └── vite.config.ts
│
├── src/main/java/wo1261931780/accountManage/
│   ├── config/                        # 配置类
│   ├── controller/                    # 控制器
│   ├── dto/                           # 数据传输对象
│   ├── entity/                        # 实体类
│   ├── mapper/                        # MyBatis Mapper
│   ├── service/                       # 服务层
│   ├── security/                      # 安全认证
│   ├── aspect/                        # AOP 切面
│   ├── annotation/                    # 自定义注解
│   └── util/                          # 工具类
│
├── sql/                               # SQL 脚本
│   ├── 01_schema.sql                  # 表结构
│   ├── 02_init_data.sql               # 初始数据
│   ├── 03_security_tables.sql         # 安全相关表
│   ├── 04_password_expiration.sql     # 密码过期
│   ├── 05_backup_and_history.sql      # 备份历史
│   └── 06_favorites_and_recent.sql    # 收藏和访问记录
│
├── pom.xml                            # Maven 配置
└── README.md                          # 项目说明
```

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Maven**: 3.8+

### 1. 克隆项目

```bash
git clone https://github.com/wo1261931780/accountManage.git
cd accountManage
```

### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS account_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行初始化脚本
for f in sql/*.sql; do mysql -u root -p account_manage < "$f"; done
```

### 3. 配置数据库连接

编辑 `src/main/resources/application.yaml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/account_manage?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的数据库密码
```

### 4. 启动后端服务

```bash
# 使用 Maven 启动
./mvnw spring-boot:run

# 或者打包后运行
./mvnw clean package -DskipTests
java -jar target/accountManage-0.0.1-SNAPSHOT.jar
```

后端服务将启动在 http://localhost:8080

### 5. 启动前端服务

```bash
cd frontend

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 或者构建生产版本
npm run build
```

前端服务将启动在 http://localhost:3000

### 6. 访问系统

- **前端地址**: http://localhost:3000
- **API 文档**: http://localhost:8080/doc.html
- **默认账号**: `admin` / `admin123`

---

## 📸 系统截图

### 登录页面
美观的渐变背景登录界面，支持记住登录状态。

### 仪表盘
- 四个统计卡片：账号总数、平台数量、即将过期、最近访问
- ECharts 图表：平台类型分布饼图、账号状态柱状图
- 快捷操作入口和最近访问记录

### 账号管理
- 支持多条件搜索：关键词、平台类型、平台、状态、重要程度
- 表格展示：平台、账号信息、密码（加密显示/查看/复制）、绑定信息
- 完整的 CRUD 操作和批量操作

### 密码工具
- 随机密码生成器：自定义长度、字符类型
- 密码强度检测：实时检测并给出改进建议
- 密码过期提醒：列出即将过期的密码

---

## 📚 API 文档

启动后端后访问 http://localhost:8080/doc.html 查看完整的 API 文档。

### 主要接口

| 模块 | 接口 | 方法 | 描述 |
|------|------|------|------|
| 认证 | `/api/v1/auth/login` | POST | 用户登录 |
| 认证 | `/api/v1/auth/refresh` | POST | 刷新令牌 |
| 认证 | `/api/v1/auth/logout` | POST | 用户登出 |
| 账号 | `/api/v1/accounts` | GET | 分页查询账号 |
| 账号 | `/api/v1/accounts/{id}` | GET | 获取账号详情 |
| 账号 | `/api/v1/accounts` | POST | 创建账号 |
| 账号 | `/api/v1/accounts/{id}` | PUT | 更新账号 |
| 账号 | `/api/v1/accounts/{id}` | DELETE | 删除账号 |
| 账号 | `/api/v1/accounts/{id}/password` | GET | 获取解密密码 |
| 平台 | `/api/v1/platforms` | GET | 获取平台列表 |
| 平台 | `/api/v1/platform-types` | GET | 获取平台类型 |
| 收藏 | `/api/v1/favorites` | GET | 获取收藏列表 |
| 收藏 | `/api/v1/favorites/{accountId}` | POST | 添加收藏 |
| 仪表盘 | `/api/v1/dashboard/stats` | GET | 获取统计数据 |
| 密码 | `/api/v1/password/generate` | POST | 生成随机密码 |
| 密码 | `/api/v1/password/strength` | POST | 检测密码强度 |

---

## 🔐 安全特性

### 密码加密

所有账号密码使用 **AES-256-CBC** 对称加密算法存储：

```java
// 加密存储
String encrypted = AesUtils.encrypt(plainPassword, secretKey);

// 解密查看
String decrypted = AesUtils.decrypt(encrypted, secretKey);
```

### JWT 认证流程

```
1. 用户登录 → 返回 accessToken + refreshToken
2. 请求 API → Header 携带 Authorization: Bearer {accessToken}
3. Token 过期 → 使用 refreshToken 刷新获取新 Token
4. 双 Token 都过期 → 重新登录
```

### 接口限流

使用自定义注解实现接口限流：

```java
@RateLimit(limit = 5, period = 60)  // 每分钟最多 5 次
@PostMapping("/login")
public Result<LoginResponse> login(@RequestBody LoginRequest request) {
    // ...
}
```

---

## 📅 开发计划

### v1.0 - 基础版本 ✅
- [x] 用户认证（登录/登出）
- [x] 账号 CRUD
- [x] 平台管理
- [x] 密码加密存储

### v1.1 - 安全增强 ✅
- [x] JWT 双令牌认证
- [x] 接口限流
- [x] 操作日志
- [x] 密钥轮换

### v1.2 - 功能完善 ✅
- [x] 收藏功能
- [x] 最近访问
- [x] 仪表盘统计
- [x] 密码工具
- [x] 前端界面

### v1.3 - 计划中 🚧
- [ ] 数据导入/导出
- [ ] 密码过期自动提醒
- [ ] 双因素认证 (2FA)
- [ ] 移动端适配

### v2.0 - 远期规划 📋
- [ ] 团队协作
- [ ] 密码共享
- [ ] 浏览器插件
- [ ] 本地客户端

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📄 许可证

本项目采用 [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE) 开源许可证。

### AGPL-3.0 要点

- ✅ 允许商业使用、修改、分发
- ✅ 允许私人使用和专利授权
- ⚠️ **网络使用披露**：如果您通过网络提供服务，必须向用户提供源代码
- ⚠️ **相同许可证**：修改后的版本必须使用相同的 AGPL-3.0 许可证
- ⚠️ **声明变更**：必须记录对代码的修改

---

## 👨‍💻 作者

**wo1261931780**

- GitHub: [@wo1261931780](https://github.com/wo1261931780)

---

<p align="center">
  如果这个项目对你有帮助，请给一个 ⭐ Star 支持一下！
</p>
