-- ============================================
-- 登录安全增强 - 添加登录失败锁定机制
-- ============================================

-- 为 sys_user 表添加登录安全相关字段
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS login_fail_count INT DEFAULT 0 COMMENT '登录失败次数';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS lock_time DATETIME DEFAULT NULL COMMENT '账号锁定时间';

-- 更新现有用户的默认值
UPDATE sys_user SET login_fail_count = 0 WHERE login_fail_count IS NULL;

-- 添加索引以便快速查询锁定状态
CREATE INDEX IF NOT EXISTS idx_sys_user_lock_time ON sys_user(lock_time);

-- ============================================
-- 说明：
-- 1. login_fail_count: 连续登录失败次数，成功登录后重置为0
-- 2. lock_time: 账号锁定时间，锁定期间无法登录
-- 3. 锁定策略：连续5次登录失败，锁定账号30分钟
-- 4. 管理员可以手动解锁用户账号
-- ============================================
