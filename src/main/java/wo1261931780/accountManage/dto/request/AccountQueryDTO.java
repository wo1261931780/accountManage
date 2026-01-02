package wo1261931780.accountManage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 账号查询请求
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "账号查询请求")
public class AccountQueryDTO extends PageRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台ID
     */
    @Schema(description = "平台ID")
    private Long platformId;

    /**
     * 平台类型ID
     */
    @Schema(description = "平台类型ID")
    private Long platformTypeId;

    /**
     * 账号名称（模糊查询）
     */
    @Schema(description = "账号名称（模糊查询）")
    private String accountName;

    /**
     * 账号别名（模糊查询）
     */
    @Schema(description = "账号别名（模糊查询）")
    private String accountAlias;

    /**
     * 绑定手机号
     */
    @Schema(description = "绑定手机号")
    private String bindPhone;

    /**
     * 绑定邮箱
     */
    @Schema(description = "绑定邮箱")
    private String bindEmail;

    /**
     * 是否绑定密保工具
     */
    @Schema(description = "是否绑定密保工具: 0-否, 1-是")
    private Integer hasSecurityTool;

    /**
     * 账号状态
     */
    @Schema(description = "账号状态: 0-已注销, 1-正常, 2-已冻结, 3-待验证")
    private Integer accountStatus;

    /**
     * 重要程度
     */
    @Schema(description = "重要程度: 1-低, 2-中, 3-高, 4-极高")
    private Integer importanceLevel;

    /**
     * 是否主账号
     */
    @Schema(description = "是否主账号: 0-否, 1-是")
    private Integer isMainAccount;

    /**
     * 标签（模糊查询）
     */
    @Schema(description = "标签")
    private String tags;

    /**
     * 关键词（综合搜索：账号名、别名、备注）
     */
    @Schema(description = "关键词（综合搜索）")
    private String keyword;
}
