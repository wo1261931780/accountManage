-- ========================================
-- 账号密码管理系统 - 数据库初始化脚本
-- 数据库: account_manage
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_general_ci
--
-- 使用说明:
-- 1. 在 MySQL 中执行此脚本
-- 2. 确保 MySQL 版本 >= 8.0
-- 3. 修改数据库密码后再使用
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `account_manage`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE `account_manage`;

-- ========================================
-- 1. 平台类型字典表 (sys_platform_type)
-- ========================================
DROP TABLE IF EXISTS `sys_platform_type`;
CREATE TABLE `sys_platform_type` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `type_code` VARCHAR(50) NOT NULL COMMENT '类型编码',
    `type_name` VARCHAR(100) NOT NULL COMMENT '类型名称',
    `type_name_en` VARCHAR(100) DEFAULT NULL COMMENT '类型英文名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '类型图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `remark` LONGTEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='平台类型字典表';

-- ========================================
-- 2. 平台字典表 (sys_platform)
-- ========================================
DROP TABLE IF EXISTS `sys_platform`;
CREATE TABLE `sys_platform` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `platform_code` VARCHAR(50) NOT NULL COMMENT '平台编码',
    `platform_name` VARCHAR(100) NOT NULL COMMENT '平台名称',
    `platform_name_en` VARCHAR(100) DEFAULT NULL COMMENT '平台英文名称',
    `platform_type_id` BIGINT NOT NULL COMMENT '平台类型ID',
    `platform_url` LONGTEXT COMMENT '平台官网地址',
    `platform_icon` VARCHAR(500) DEFAULT NULL COMMENT '平台图标URL',
    `country` VARCHAR(50) DEFAULT 'CN' COMMENT '国家/地区代码: CN-中国, US-美国, 等',
    `country_name` VARCHAR(100) DEFAULT '中国' COMMENT '国家/地区名称',
    `description` LONGTEXT COMMENT '平台描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `remark` LONGTEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_platform_code` (`platform_code`),
    KEY `idx_platform_type_id` (`platform_type_id`),
    KEY `idx_country` (`country`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='平台字典表';

-- ========================================
-- 3. 账号信息主表 (sys_account)
-- ========================================
DROP TABLE IF EXISTS `sys_account`;
CREATE TABLE `sys_account` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `platform_id` BIGINT NOT NULL COMMENT '平台ID',
    `account_name` VARCHAR(200) NOT NULL COMMENT '账号名称/登录名',
    `account_alias` VARCHAR(200) DEFAULT NULL COMMENT '账号别名/昵称',
    `uid` VARCHAR(100) DEFAULT NULL COMMENT '用户唯一标识/UID',
    `serial_number` VARCHAR(100) DEFAULT NULL COMMENT '编号码/序列号',
    `bind_phone` VARCHAR(50) DEFAULT NULL COMMENT '绑定手机号',
    `bind_email` VARCHAR(200) DEFAULT NULL COMMENT '绑定邮箱',
    `password_encrypted` VARCHAR(500) NOT NULL COMMENT '加密后的密码',
    `password_salt` VARCHAR(100) NOT NULL COMMENT '密码加密盐值',
    `has_security_tool` TINYINT DEFAULT 0 COMMENT '是否绑定密保工具: 0-否, 1-是',
    `security_tool_type` VARCHAR(100) DEFAULT NULL COMMENT '密保工具类型: 如Google验证器/手机验证等',
    `account_level` VARCHAR(50) DEFAULT NULL COMMENT '账号等级/会员等级',
    `account_status` TINYINT DEFAULT 1 COMMENT '账号状态: 0-已注销, 1-正常, 2-已冻结, 3-待验证',
    `account_purpose` VARCHAR(200) DEFAULT NULL COMMENT '账号用途/作用',
    `register_time` DATE DEFAULT NULL COMMENT '注册时间',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `expire_time` DATE DEFAULT NULL COMMENT '账号/会员到期时间',
    `importance_level` TINYINT DEFAULT 2 COMMENT '重要程度: 1-低, 2-中, 3-高, 4-极高',
    `is_main_account` TINYINT DEFAULT 1 COMMENT '是否主账号: 0-否, 1-是',
    `related_account_id` BIGINT DEFAULT NULL COMMENT '关联账号ID(小号关联主号)',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
    `remark` LONGTEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_platform_id` (`platform_id`),
    KEY `idx_account_name` (`account_name`),
    KEY `idx_bind_phone` (`bind_phone`),
    KEY `idx_bind_email` (`bind_email`),
    KEY `idx_account_status` (`account_status`),
    KEY `idx_importance_level` (`importance_level`),
    KEY `idx_is_main_account` (`is_main_account`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='账号信息主表';
