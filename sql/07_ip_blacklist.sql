-- =====================================================
-- IP黑名单表
-- 用于封禁恶意IP，支持手动添加和自动封禁
-- 创建时间：2026-01-03
-- =====================================================

-- IP黑名单表
CREATE TABLE IF NOT EXISTS ip_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    ip_address VARCHAR(45) NOT NULL COMMENT 'IP地址（支持IPv6）',
    reason VARCHAR(500) COMMENT '封禁原因',
    source VARCHAR(50) NOT NULL DEFAULT 'MANUAL' COMMENT '来源：MANUAL-手动添加, AUTO-自动封禁, LOGIN_FAIL-登录失败过多',
    fail_count INT DEFAULT 0 COMMENT '失败次数（用于自动封禁）',
    expire_time DATETIME COMMENT '过期时间，NULL表示永久封禁',
    is_permanent TINYINT(1) DEFAULT 0 COMMENT '是否永久封禁：0-临时, 1-永久',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：0-已解封, 1-封禁中',
    created_by BIGINT COMMENT '创建人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_ip_address (ip_address),
    KEY idx_status (status),
    KEY idx_expire_time (expire_time),
    KEY idx_source (source)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IP黑名单表';

-- IP访问记录表（用于统计和分析）
CREATE TABLE IF NOT EXISTS ip_access_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    ip_address VARCHAR(45) NOT NULL COMMENT 'IP地址',
    request_uri VARCHAR(500) COMMENT '请求路径',
    request_method VARCHAR(10) COMMENT '请求方法',
    user_agent VARCHAR(500) COMMENT '用户代理',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被拦截',
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    KEY idx_ip_address (ip_address),
    KEY idx_access_time (access_time),
    KEY idx_is_blocked (is_blocked)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IP访问记录表';

-- 登录失败记录表（用于自动封禁）
CREATE TABLE IF NOT EXISTS login_fail_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    ip_address VARCHAR(45) NOT NULL COMMENT 'IP地址',
    username VARCHAR(100) COMMENT '尝试的用户名',
    fail_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '失败时间',
    KEY idx_ip_address (ip_address),
    KEY idx_fail_time (fail_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录失败记录表';

-- 定时清理过期数据的事件（需要开启事件调度器）
-- SET GLOBAL event_scheduler = ON;

DELIMITER //

-- 自动解封过期IP的事件
CREATE EVENT IF NOT EXISTS event_auto_unblock_ip
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- 解封过期的临时封禁IP
    UPDATE ip_blacklist
    SET status = 0
    WHERE is_permanent = 0
    AND expire_time IS NOT NULL
    AND expire_time <= NOW()
    AND status = 1;

    -- 清理7天前的访问记录
    DELETE FROM ip_access_log WHERE access_time < DATE_SUB(NOW(), INTERVAL 7 DAY);

    -- 清理30天前的登录失败记录
    DELETE FROM login_fail_record WHERE fail_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
END //

DELIMITER ;
