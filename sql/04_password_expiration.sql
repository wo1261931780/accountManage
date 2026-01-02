-- =====================================================
-- 添加密码过期相关字段到账号表
-- =====================================================

-- 添加密码最后修改时间字段
ALTER TABLE `sys_account`
ADD COLUMN `password_update_time` DATETIME DEFAULT NULL COMMENT '密码最后修改时间' AFTER `password_salt`;

-- 添加密码有效期字段（天数，0表示永不过期）
ALTER TABLE `sys_account`
ADD COLUMN `password_valid_days` INT DEFAULT 0 COMMENT '密码有效期(天)，0表示永不过期' AFTER `password_update_time`;

-- 为已有数据设置默认密码更新时间为创建时间
UPDATE `sys_account` SET `password_update_time` = `create_time` WHERE `password_update_time` IS NULL;

-- 添加索引以支持过期查询
ALTER TABLE `sys_account` ADD INDEX `idx_password_expiration` (`password_valid_days`, `password_update_time`);
