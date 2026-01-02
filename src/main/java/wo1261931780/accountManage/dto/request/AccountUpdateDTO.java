package wo1261931780.accountManage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 账号更新请求
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@Schema(description = "账号更新请求")
public class AccountUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @NotNull(message = "ID不能为空")
    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 平台ID
     */
    @Schema(description = "平台ID")
    private Long platformId;

    /**
     * 账号名称/登录名
     */
    @Size(max = 200, message = "账号名称长度不能超过200")
    @Schema(description = "账号名称/登录名", example = "user@example.com")
    private String accountName;

    /**
     * 账号别名/昵称
     */
    @Size(max = 200, message = "账号别名长度不能超过200")
    @Schema(description = "账号别名/昵称", example = "我的微信号")
    private String accountAlias;

    /**
     * 用户唯一标识/UID
     */
    @Size(max = 100, message = "UID长度不能超过100")
    @Schema(description = "用户唯一标识/UID", example = "123456789")
    private String uid;

    /**
     * 编号码/序列号
     */
    @Size(max = 100, message = "编号码长度不能超过100")
    @Schema(description = "编号码/序列号")
    private String serialNumber;

    /**
     * 绑定手机号
     */
    @Size(max = 50, message = "绑定手机号长度不能超过50")
    @Schema(description = "绑定手机号", example = "13800138000")
    private String bindPhone;

    /**
     * 绑定邮箱
     */
    @Size(max = 200, message = "绑定邮箱长度不能超过200")
    @Schema(description = "绑定邮箱", example = "user@example.com")
    private String bindEmail;

    /**
     * 新密码（如果为空则不更新密码）
     */
    @Schema(description = "新密码（明文，为空则不更新密码）")
    private String password;

    /**
     * 是否绑定密保工具
     */
    @Schema(description = "是否绑定密保工具: 0-否, 1-是", example = "0")
    private Integer hasSecurityTool;

    /**
     * 密保工具类型
     */
    @Size(max = 100, message = "密保工具类型长度不能超过100")
    @Schema(description = "密保工具类型", example = "Google验证器")
    private String securityToolType;

    /**
     * 账号等级/会员等级
     */
    @Size(max = 50, message = "账号等级长度不能超过50")
    @Schema(description = "账号等级/会员等级", example = "VIP1")
    private String accountLevel;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态: 0-已注销, 1-正常, 2-已冻结, 3-待验证", example = "1")
    private Integer accountStatus;

    /**
     * 账号用途/作用
     */
    @Size(max = 200, message = "账号用途长度不能超过200")
    @Schema(description = "账号用途/作用", example = "日常社交")
    private String accountPurpose;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间", example = "2020-01-01")
    private LocalDate registerTime;

    /**
     * 账号/会员到期时间
     */
    @Schema(description = "账号/会员到期时间", example = "2025-12-31")
    private LocalDate expireTime;

    /**
     * 重要程度
     */
    @Schema(description = "重要程度: 1-低, 2-中, 3-高, 4-极高", example = "2")
    private Integer importanceLevel;

    /**
     * 是否主账号
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
    @Size(max = 500, message = "标签长度不能超过500")
    @Schema(description = "标签(逗号分隔)", example = "工作,重要")
    private String tags;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
