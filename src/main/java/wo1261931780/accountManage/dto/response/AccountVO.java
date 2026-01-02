package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import wo1261931780.accountManage.annotation.Sensitive;
import wo1261931780.accountManage.annotation.Sensitive.SensitiveType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账号响应VO
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@Schema(description = "账号响应")
public class AccountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 平台ID
     */
    @Schema(description = "平台ID")
    private Long platformId;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * 平台类型ID
     */
    @Schema(description = "平台类型ID")
    private Long platformTypeId;

    /**
     * 平台类型名称
     */
    @Schema(description = "平台类型名称")
    private String platformTypeName;

    /**
     * 平台官网地址
     */
    @Schema(description = "平台官网地址")
    private String platformUrl;

    /**
     * 账号名称/登录名
     */
    @Schema(description = "账号名称/登录名")
    private String accountName;

    /**
     * 账号别名/昵称
     */
    @Schema(description = "账号别名/昵称")
    private String accountAlias;

    /**
     * 用户唯一标识/UID
     */
    @Schema(description = "用户唯一标识/UID")
    private String uid;

    /**
     * 编号码/序列号
     */
    @Schema(description = "编号码/序列号")
    private String serialNumber;

    /**
     * 绑定手机号
     */
    @Sensitive(type = SensitiveType.PHONE)
    @Schema(description = "绑定手机号")
    private String bindPhone;

    /**
     * 绑定邮箱
     */
    @Sensitive(type = SensitiveType.EMAIL)
    @Schema(description = "绑定邮箱")
    private String bindEmail;

    /**
     * 解密后的密码（用于前端展示和复制）
     * 注意：列表查询时脱敏，详情查询时可选择返回明文
     */
    @Sensitive(type = SensitiveType.PASSWORD)
    @Schema(description = "密码（脱敏）")
    private String password;

    /**
     * 是否绑定密保工具: 0-否, 1-是
     */
    @Schema(description = "是否绑定密保工具: 0-否, 1-是")
    private Integer hasSecurityTool;

    /**
     * 密保工具类型
     */
    @Schema(description = "密保工具类型")
    private String securityToolType;

    /**
     * 账号等级/会员等级
     */
    @Schema(description = "账号等级/会员等级")
    private String accountLevel;

    /**
     * 账号状态: 0-已注销, 1-正常, 2-已冻结, 3-待验证
     */
    @Schema(description = "账号状态: 0-已注销, 1-正常, 2-已冻结, 3-待验证")
    private Integer accountStatus;

    /**
     * 账号状态文本
     */
    @Schema(description = "账号状态文本")
    private String accountStatusText;

    /**
     * 账号用途/作用
     */
    @Schema(description = "账号用途/作用")
    private String accountPurpose;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    private LocalDate registerTime;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 账号/会员到期时间
     */
    @Schema(description = "账号/会员到期时间")
    private LocalDate expireTime;

    /**
     * 重要程度: 1-低, 2-中, 3-高, 4-极高
     */
    @Schema(description = "重要程度: 1-低, 2-中, 3-高, 4-极高")
    private Integer importanceLevel;

    /**
     * 重要程度文本
     */
    @Schema(description = "重要程度文本")
    private String importanceLevelText;

    /**
     * 是否主账号: 0-否, 1-是
     */
    @Schema(description = "是否主账号: 0-否, 1-是")
    private Integer isMainAccount;

    /**
     * 关联账号ID
     */
    @Schema(description = "关联账号ID")
    private Long relatedAccountId;

    /**
     * 标签
     */
    @Schema(description = "标签")
    private String tags;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 根据状态获取状态文本
     */
    public String getAccountStatusText() {
        if (accountStatus == null) return "";
        return switch (accountStatus) {
            case 0 -> "已注销";
            case 1 -> "正常";
            case 2 -> "已冻结";
            case 3 -> "待验证";
            default -> "未知";
        };
    }

    /**
     * 根据重要程度获取文本
     */
    public String getImportanceLevelText() {
        if (importanceLevel == null) return "";
        return switch (importanceLevel) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            case 4 -> "极高";
            default -> "未知";
        };
    }
}
