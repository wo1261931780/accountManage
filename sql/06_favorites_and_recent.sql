-- =====================================================
-- 账号收藏和最近使用表
-- 用于存储用户收藏的账号和访问记录
-- =====================================================

USE account_manage;

-- -------------------------------------------
-- 账号收藏表
-- -------------------------------------------
DROP TABLE IF EXISTS sys_account_favorite;
CREATE TABLE sys_account_favorite (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    account_id BIGINT NOT NULL COMMENT '账号ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序(越小越靠前)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_account (user_id, account_id),
    KEY idx_user_id (user_id),
    KEY idx_account_id (account_id),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号收藏表';

-- -------------------------------------------
-- 账号访问记录表（最近使用）
-- -------------------------------------------
DROP TABLE IF EXISTS sys_account_access_log;
CREATE TABLE sys_account_access_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    account_id BIGINT NOT NULL COMMENT '账号ID',
    access_type TINYINT NOT NULL DEFAULT 1 COMMENT '访问类型: 1-查看, 2-复制密码, 3-编辑',
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_account_id (account_id),
    KEY idx_access_time (access_time),
    KEY idx_user_account_time (user_id, account_id, access_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号访问记录表';

-- -------------------------------------------
-- 用户最近访问账号视图（每个账号只保留最新访问时间）
-- -------------------------------------------
CREATE OR REPLACE VIEW v_recent_accounts AS
SELECT
    user_id,
    account_id,
    MAX(access_time) AS last_access_time,
    COUNT(*) AS access_count
FROM sys_account_access_log
GROUP BY user_id, account_id;

-- -------------------------------------------
-- 统计相关视图
-- -------------------------------------------

-- 平台类型账号统计视图
CREATE OR REPLACE VIEW v_platform_type_stats AS
SELECT
    pt.id AS platform_type_id,
    pt.type_name,
    pt.icon,
    COUNT(DISTINCT p.id) AS platform_count,
    COUNT(DISTINCT a.id) AS account_count
FROM sys_platform_type pt
LEFT JOIN sys_platform p ON pt.id = p.platform_type_id AND p.deleted = 0
LEFT JOIN sys_account a ON p.id = a.platform_id AND a.deleted = 0
WHERE pt.deleted = 0
GROUP BY pt.id, pt.type_name, pt.icon;

-- 平台账号统计视图
CREATE OR REPLACE VIEW v_platform_stats AS
SELECT
    p.id AS platform_id,
    p.platform_name,
    p.platform_icon,
    pt.type_name AS platform_type_name,
    COUNT(a.id) AS account_count
FROM sys_platform p
LEFT JOIN sys_platform_type pt ON p.platform_type_id = pt.id
LEFT JOIN sys_account a ON p.id = a.platform_id AND a.deleted = 0
WHERE p.deleted = 0
GROUP BY p.id, p.platform_name, p.platform_icon, pt.type_name;

-- 账号状态统计视图
CREATE OR REPLACE VIEW v_account_status_stats AS
SELECT
    account_status,
    CASE account_status
        WHEN 0 THEN '已注销'
        WHEN 1 THEN '正常'
        WHEN 2 THEN '已冻结'
        WHEN 3 THEN '待验证'
        ELSE '未知'
    END AS status_name,
    COUNT(*) AS account_count
FROM sys_account
WHERE deleted = 0
GROUP BY account_status;

-- 账号重要程度统计视图
CREATE OR REPLACE VIEW v_account_importance_stats AS
SELECT
    importance_level,
    CASE importance_level
        WHEN 1 THEN '低'
        WHEN 2 THEN '中'
        WHEN 3 THEN '高'
        WHEN 4 THEN '极高'
        ELSE '未设置'
    END AS importance_name,
    COUNT(*) AS account_count
FROM sys_account
WHERE deleted = 0
GROUP BY importance_level;

-- 密码过期统计视图
CREATE OR REPLACE VIEW v_password_expiry_stats AS
SELECT
    CASE
        WHEN password_valid_days = 0 OR password_valid_days IS NULL THEN '永不过期'
        WHEN DATEDIFF(DATE_ADD(password_update_time, INTERVAL password_valid_days DAY), CURDATE()) < 0 THEN '已过期'
        WHEN DATEDIFF(DATE_ADD(password_update_time, INTERVAL password_valid_days DAY), CURDATE()) <= 7 THEN '即将过期'
        WHEN DATEDIFF(DATE_ADD(password_update_time, INTERVAL password_valid_days DAY), CURDATE()) <= 30 THEN '30天内过期'
        ELSE '正常'
    END AS expiry_status,
    COUNT(*) AS account_count
FROM sys_account
WHERE deleted = 0
GROUP BY expiry_status;
