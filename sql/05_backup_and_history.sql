-- ============================================
-- 数据备份与密码历史表
-- 版本: 1.0
-- 创建时间: 2026-01-02
-- ============================================

-- -------------------------------------------
-- 1. 数据备份记录表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS sys_backup (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '备份ID',
    backup_name VARCHAR(100) NOT NULL COMMENT '备份名称',
    backup_type TINYINT NOT NULL DEFAULT 1 COMMENT '备份类型: 1-手动备份 2-自动备份',
    backup_path VARCHAR(500) NOT NULL COMMENT '备份文件路径',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    table_count INT DEFAULT 0 COMMENT '备份表数量',
    record_count INT DEFAULT 0 COMMENT '备份记录总数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-进行中 1-成功 2-失败',
    error_message VARCHAR(500) COMMENT '错误信息',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT COMMENT '创建人ID',
    INDEX idx_backup_type (backup_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据备份记录表';

-- -------------------------------------------
-- 2. 密码修改历史表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS sys_password_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '历史ID',
    account_id BIGINT NOT NULL COMMENT '账号ID',
    password_encrypted VARCHAR(500) NOT NULL COMMENT '加密后的密码',
    password_salt VARCHAR(100) NOT NULL COMMENT '密码盐值',
    change_reason VARCHAR(100) COMMENT '修改原因',
    change_type TINYINT NOT NULL DEFAULT 1 COMMENT '修改类型: 1-用户修改 2-系统重置 3-过期更换 4-批量导入',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT COMMENT '操作人ID',
    INDEX idx_account_id (account_id),
    INDEX idx_create_time (create_time),
    CONSTRAINT fk_password_history_account FOREIGN KEY (account_id) REFERENCES sys_account(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='密码修改历史表';

-- -------------------------------------------
-- 3. 账号标签表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS sys_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_color VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    UNIQUE KEY uk_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号标签表';

-- -------------------------------------------
-- 4. 账号-标签关联表
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS sys_account_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    account_id BIGINT NOT NULL COMMENT '账号ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_account_tag (account_id, tag_id),
    INDEX idx_tag_id (tag_id),
    CONSTRAINT fk_account_tag_account FOREIGN KEY (account_id) REFERENCES sys_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_account_tag_tag FOREIGN KEY (tag_id) REFERENCES sys_tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号-标签关联表';

-- -------------------------------------------
-- 5. 初始化默认标签
-- -------------------------------------------
INSERT INTO sys_tag (tag_name, tag_color, sort_order) VALUES
('重要', '#F56C6C', 1),
('常用', '#E6A23C', 2),
('工作', '#409EFF', 3),
('个人', '#67C23A', 4),
('临时', '#909399', 5);
