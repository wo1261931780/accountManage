# 🔐 AccountManage Backend - 账号密码管理系统后端

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MyBatis%20Plus-3.5.9-orange" alt="MyBatis Plus">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479a1" alt="MySQL">
  <img src="https://img.shields.io/badge/JWT-0.12.6-purple" alt="JWT">
  <img src="https://img.shields.io/badge/Knife4j-4.5.0-blue" alt="Knife4j">
  <img src="https://img.shields.io/badge/License-AGPL--3.0-blue" alt="License">
</p>

<p align="center">
  账号密码管理系统的后端 API 服务，基于 Spring Boot 3 构建，提供安全可靠的 RESTful API。
</p>

---

## 📋 目录

- [项目简介](#-项目简介)
- [功能特性](#-功能特性)
- [技术架构](#-技术架构)
- [项目结构](#-项目结构)
- [快速开始](#-快速开始)
- [API 文档](#-api-文档)
- [安全特性](#-安全特性)
- [开发计划](#-开发计划)
- [相关项目](#-相关项目)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)

---

## 📖 项目简介

这是账号密码管理系统的后端项目，提供完整的 RESTful API 服务。

前端项目请访问: [account_manager_vue3](https://github.com/wo1261931780/account_manager_vue3)

---

## ✨ 功能特性

### 核心功能

| 模块 | 功能描述 |
|------|----------|
| 🔑 **账号管理** | 支持多平台账号的增删改查，密码 AES-256 加密存储 |
| 🏢 **平台管理** | 管理各类平台信息，支持平台分类、图标设置、URL 记录 |
| 📊 **仪表盘** | 提供数据统计 API，支持图表数据聚合 |
| ⭐ **收藏功能** | 常用账号收藏，支持置顶排序 |
| 🛠️ **密码工具** | 随机密码生成，密码强度检测，过期提醒 |
| 🔍 **智能搜索** | 多条件组合查询，分页排序 |

### 安全特性

- 🔒 **AES-256 加密**: 所有账号密码采用 AES-256 对称加密存储
- 🎫 **JWT 认证**: 基于 JWT 的无状态身份认证
- 🔄 **Token 刷新**: 支持 Access Token / Refresh Token 双令牌机制
- 🚦 **接口限流**: 基于注解的接口限流，防止暴力破解
- 📝 **操作日志**: 完整的操作日志记录，支持审计追溯
- 🔐 **密钥轮换**: 支持加密密钥定期轮换

---

## 🏗️ 技术架构

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.1 | 后端框架 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| JWT (jjwt) | 0.12.6 | 身份认证 |
| Knife4j | 4.5.0 | API 文档 |
| Hutool | 5.8.34 | 工具库 |
| EasyExcel | 4.0.3 | Excel 导入导出 |
| Lombok | - | 简化代码 |

### 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端 (Vue 3 + Element Plus)               │
│                 仓库: account_manager_vue3                        │
└──────────────────────────────┬──────────────────────────────────┘
                               │ HTTP/REST API
┌──────────────────────────────▼──────────────────────────────────┐
│                     后端 (Spring Boot 3)                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ Controller  │──│   Service   │──│       Mapper            │  │
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
├── src/main/java/wo1261931780/accountManage/
│   ├── AccountManageApplication.java  # 启动类
│   ├── annotation/                    # 自定义注解
│   │   └── RateLimit.java             # 限流注解
│   ├── aspect/                        # AOP 切面
│   │   └── RateLimitAspect.java       # 限流切面
│   ├── common/                        # 公共模块
│   │   ├── config/                    # 配置类
│   │   │   └── CorsConfig.java        # 跨域配置
│   │   ├── exception/                 # 异常处理
│   │   └── result/                    # 统一响应
│   ├── config/                        # 业务配置
│   │   └── SecurityConfig.java        # 安全配置
│   ├── controller/                    # 控制器层
│   │   ├── AccountController.java     # 账号管理
│   │   ├── AuthController.java        # 认证授权
│   │   ├── DashboardController.java   # 仪表盘
│   │   ├── PlatformController.java    # 平台管理
│   │   └── PasswordController.java    # 密码工具
│   ├── dto/                           # 数据传输对象
│   ├── entity/                        # 实体类
│   ├── mapper/                        # MyBatis Mapper
│   ├── security/                      # 安全模块
│   │   ├── JwtTokenProvider.java      # JWT 提供者
│   │   └── JwtAuthenticationFilter.java # JWT 过滤器
│   ├── service/                       # 服务层
│   │   ├── impl/                      # 服务实现
│   │   └── ...Service.java            # 服务接口
│   └── util/                          # 工具类
│       └── AesUtils.java              # AES 加密工具
│
├── src/main/resources/
│   ├── application.yaml               # 应用配置
│   └── mapper/                        # MyBatis XML
│       ├── AccountMapper.xml
│       └── PlatformMapper.xml
│
├── sql/                               # SQL 脚本
│   ├── 01_schema.sql                  # 表结构
│   ├── 02_init_data.sql               # 初始数据
│   ├── 03_security_tables.sql         # 安全相关表
│   ├── 04_password_expiration.sql     # 密码过期
│   ├── 05_backup_and_history.sql      # 备份历史
│   └── 06_favorites_and_recent.sql    # 收藏和访问记录
│
├── docs/                              # 文档
│   ├── DEVELOPMENT_PLAN.md            # 开发计划
│   └── HTTPS_CONFIG.md                # HTTPS 配置
│
├── pom.xml                            # Maven 配置
└── README.md                          # 项目说明
```

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
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

### 5. 访问系统

- **API 文档**: http://localhost:8080/doc.html
- **默认账号**: `admin` / `admin123`

---

## 📚 API 文档

启动后端后访问 http://localhost:8080/doc.html 查看完整的 API 文档（Knife4j）。

### 主要接口

| 模块 | 接口 | 方法 | 描述 |
|------|------|------|------|
| 认证 | `/api/v1/auth/login` | POST | 用户登录 |
| 认证 | `/api/v1/auth/refresh` | POST | 刷新令牌 |
| 认证 | `/api/v1/auth/logout` | POST | 用户登出 |
| 认证 | `/api/v1/auth/me` | GET | 获取当前用户 |
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

### 跨域配置

已配置 CORS 支持前端跨域访问，配置位于 `CorsConfig.java`：

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // ...
    }
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

### v1.3 - 计划中 🚧
- [ ] 数据导入/导出
- [ ] 密码过期自动提醒
- [ ] 双因素认证 (2FA)

### v2.0 - 远期规划 📋
- [ ] 团队协作
- [ ] 密码共享
- [ ] Redis 缓存集成

---

## 🤝 相关项目

| 项目 | 说明 |
|------|------|
| [account_manager_vue3](https://github.com/wo1261931780/account_manager_vue3) | 前端项目 (Vue 3 + Element Plus) |

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

---

## 👨‍💻 作者

**wo1261931780**

- GitHub: [@wo1261931780](https://github.com/wo1261931780)

---

<p align="center">
  如果这个项目对你有帮助，请给一个 ⭐ Star 支持一下！
</p>
