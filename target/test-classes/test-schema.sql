-- 测试数据库 Schema (H2)

-- 平台类型字典表
CREATE TABLE IF NOT EXISTS `sys_platform_type` (
    `id` BIGINT NOT NULL,
    `type_code` VARCHAR(50) NOT NULL,
    `type_name` VARCHAR(100) NOT NULL,
    `type_name_en` VARCHAR(100) DEFAULT NULL,
    `icon` VARCHAR(255) DEFAULT NULL,
    `sort_order` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `remark` CLOB,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);

-- 平台字典表
CREATE TABLE IF NOT EXISTS `sys_platform` (
    `id` BIGINT NOT NULL,
    `platform_code` VARCHAR(50) NOT NULL,
    `platform_name` VARCHAR(100) NOT NULL,
    `platform_name_en` VARCHAR(100) DEFAULT NULL,
    `platform_type_id` BIGINT NOT NULL,
    `platform_url` CLOB,
    `platform_icon` VARCHAR(500) DEFAULT NULL,
    `country` VARCHAR(50) DEFAULT 'CN',
    `country_name` VARCHAR(100) DEFAULT '中国',
    `description` CLOB,
    `sort_order` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `remark` CLOB,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);

-- 账号信息主表
CREATE TABLE IF NOT EXISTS `sys_account` (
    `id` BIGINT NOT NULL,
    `platform_id` BIGINT NOT NULL,
    `account_name` VARCHAR(200) NOT NULL,
    `account_alias` VARCHAR(200) DEFAULT NULL,
    `uid` VARCHAR(100) DEFAULT NULL,
    `serial_number` VARCHAR(100) DEFAULT NULL,
    `bind_phone` VARCHAR(50) DEFAULT NULL,
    `bind_email` VARCHAR(200) DEFAULT NULL,
    `password_encrypted` VARCHAR(500) NOT NULL,
    `password_salt` VARCHAR(100) NOT NULL,
    `has_security_tool` TINYINT DEFAULT 0,
    `security_tool_type` VARCHAR(100) DEFAULT NULL,
    `account_level` VARCHAR(50) DEFAULT NULL,
    `account_status` TINYINT DEFAULT 1,
    `account_purpose` VARCHAR(200) DEFAULT NULL,
    `register_time` DATE DEFAULT NULL,
    `last_login_time` TIMESTAMP DEFAULT NULL,
    `expire_time` DATE DEFAULT NULL,
    `importance_level` TINYINT DEFAULT 2,
    `is_main_account` TINYINT DEFAULT 1,
    `related_account_id` BIGINT DEFAULT NULL,
    `tags` VARCHAR(500) DEFAULT NULL,
    `remark` CLOB,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);

-- 初始化测试数据
INSERT INTO `sys_platform_type` (`id`, `type_code`, `type_name`, `type_name_en`, `sort_order`, `status`, `deleted`) VALUES
(1, 'SOCIAL', '社交通讯', 'Social & Communication', 1, 1, 0),
(2, 'ECOMMERCE', '电商购物', 'E-Commerce', 2, 1, 0);

INSERT INTO `sys_platform` (`id`, `platform_code`, `platform_name`, `platform_name_en`, `platform_type_id`, `platform_url`, `country`, `country_name`, `sort_order`, `status`, `deleted`) VALUES
(101, 'WECHAT', '微信', 'WeChat', 1, 'https://weixin.qq.com', 'CN', '中国', 1, 1, 0),
(102, 'QQ', 'QQ', 'QQ', 1, 'https://im.qq.com', 'CN', '中国', 2, 1, 0),
(201, 'TAOBAO', '淘宝', 'Taobao', 2, 'https://www.taobao.com', 'CN', '中国', 1, 1, 0);
