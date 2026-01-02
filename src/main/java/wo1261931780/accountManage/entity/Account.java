package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账号信息实体
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@TableName("sys_account")
@Schema(description = "账号信息")
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 平台ID
     */
    @Schema(description = "平台ID")
    private Long platformId;

    /**
     * 账号名称/登录名
     */
    @Schema(description = "账号名称/登录名", example = "user@example.com")
    private String accountName;

    /**
     * 账号别名/昵称
     */
    @Schema(description = "账号别名/昵称", example = "我的微信号")
    private String accountAlias;

    /**
     * 用户唯一标识/UID
     */
    @Schema(description = "用户唯一标识/UID", example = "123456789")
    private String uid;

    /**
     * 编号码/序列号
     */
    @Schema(description = "编号码/序列号")
    private String serialNumber;

    /**
     * 绑定手机号
     */
    @Schema(description = "绑定手机号", example = "13800138000")
    private String bindPhone;

    /**
     * 绑定邮箱
     */
    @Schema(description = "绑定邮箱", example = "user@example.com")
    private String bindEmail;

    /**
     * 加密后的密码
     */
    @Schema(description = "加密后的密码")
    private String passwordEncrypted;

    /**
     * 密码加密盐值
     */
    @Schema(description = "密码加密盐值")
    private String passwordSalt;

    /**
     * 是否绑定密保工具: 0-否, 1-是
     */
    @Schema(description = "是否绑定密保工具: 0-否, 1-是", example = "0")
    private Integer hasSecurityTool;

    /**
     * 密保工具类型
     */
    @Schema(description = "密保工具类型", example = "Google验证器")
    private String securityToolType;

    /**
     * 账号等级/会员等级
     */
    @Schema(description = "账号等级/会员等级", example = "VIP1")
    private String accountLevel;

    /**
     * 账号状态: 0-已注销, 1-正常, 2-已冻结, 3-待验证
     */
    @Schema(description = "账号状态: 0-已注销, 1-正常, 2-已冻结, 3-待验证", example = "1")
    private Integer accountStatus;

    /**
     * 账号用途/作用
     */
    @Schema(description = "账号用途/作用", example = "日常社交")
    private String accountPurpose;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间", example = "2020-01-01")
    private LocalDate registerTime;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 账号/会员到期时间
     */
    @Schema(description = "账号/会员到期时间", example = "2025-12-31")
    private LocalDate expireTime;

    /**
     * 密码最后修改时间
     */
    @Schema(description = "密码最后修改时间")
    private LocalDateTime passwordUpdateTime;

    /**
     * 密码有效期(天)，0表示永不过期
     */
    @Schema(description = "密码有效期(天)，0表示永不过期", example = "90")
    private Integer passwordValidDays;

    /**
     * 重要程度: 1-低, 2-中, 3-高, 4-极高
     */
    @Schema(description = "重要程度: 1-低, 2-中, 3-高, 4-极高", example = "2")
    private Integer importanceLevel;

    /**
     * 是否主账号: 0-否, 1-是
     */
    @Schema(description = "是否主账号: 0-否, 1-是", example = "1")
    private Integer isMainAccount;

    /**
     * 关联账号ID
     */
    @Schema(description = "关联账号ID(小号关联主号)")
    private Long relatedAccountId;

    /**
     * 标签
     */
    @Schema(description = "标签(逗号分隔)", example = "工作,重要")
    private String tags;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "逻辑删除: 0-未删除, 1-已删除")
    private Integer deleted;

    // ============ 非数据库字段 ============

    /**
     * 平台名称（关联查询用）
     */
    @TableField(exist = false)
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * 平台类型名称（关联查询用）
     */
    @TableField(exist = false)
    @Schema(description = "平台类型名称")
    private String platformTypeName;

    /**
     * 解密后的密码（仅用于前端展示，不存储）
     */
    @TableField(exist = false)
    @Schema(description = "解密后的密码")
    private String passwordPlain;
}
