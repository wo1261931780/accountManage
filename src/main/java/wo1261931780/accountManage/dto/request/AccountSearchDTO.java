package wo1261931780.accountManage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 账号搜索增强请求DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "账号搜索请求")
public class AccountSearchDTO extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词（全文搜索）
     */
    @Schema(description = "搜索关键词（支持账号名、别名、备注、UID、手机、邮箱模糊匹配）")
    private String keyword;

    /**
     * 平台类型ID列表
     */
    @Schema(description = "平台类型ID列表")
    private List<Long> platformTypeIds;

    /**
     * 平台ID列表
     */
    @Schema(description = "平台ID列表")
    private List<Long> platformIds;

    /**
     * 标签ID列表
     */
    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    /**
     * 账号状态列表
     */
    @Schema(description = "账号状态列表: 0-已注销, 1-正常, 2-已冻结, 3-待验证")
    private List<Integer> accountStatuses;

    /**
     * 重要程度列表
     */
    @Schema(description = "重要程度列表: 1-低, 2-中, 3-高, 4-极高")
    private List<Integer> importanceLevels;

    /**
     * 是否只看收藏
     */
    @Schema(description = "是否只看收藏")
    private Boolean favoritesOnly;

    /**
     * 密码是否即将过期
     */
    @Schema(description = "密码是否即将过期")
    private Boolean passwordExpiring;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段: createTime, updateTime, accountName, lastLoginTime, importanceLevel")
    private String sortField;

    /**
     * 排序方向
     */
    @Schema(description = "排序方向: asc, desc")
    private String sortOrder;
}
