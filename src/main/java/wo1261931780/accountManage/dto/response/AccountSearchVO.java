package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账号搜索结果VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "账号搜索结果")
public class AccountSearchVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "账号ID")
    private Long id;

    @Schema(description = "平台ID")
    private Long platformId;

    @Schema(description = "平台名称")
    private String platformName;

    @Schema(description = "平台图标")
    private String platformIcon;

    @Schema(description = "平台类型名称")
    private String platformTypeName;

    @Schema(description = "账号名称")
    private String accountName;

    @Schema(description = "账号别名")
    private String accountAlias;

    @Schema(description = "UID")
    private String uid;

    @Schema(description = "绑定手机号（脱敏）")
    private String bindPhone;

    @Schema(description = "绑定邮箱（脱敏）")
    private String bindEmail;

    @Schema(description = "账号状态")
    private Integer accountStatus;

    @Schema(description = "账号状态名称")
    private String accountStatusName;

    @Schema(description = "重要程度")
    private Integer importanceLevel;

    @Schema(description = "重要程度名称")
    private String importanceLevelName;

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

    @Schema(description = "标签列表")
    private List<TagSimpleVO> tags;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessTime;

    @Schema(description = "访问次数")
    private Integer accessCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "密码是否即将过期")
    private Boolean passwordExpiring;

    @Schema(description = "密码剩余有效天数")
    private Integer passwordRemainingDays;

    /**
     * 简化的标签信息
     */
    @Data
    public static class TagSimpleVO {
        private Long id;
        private String name;
        private String color;
    }

    // 状态名称转换
    public static String getStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "已注销";
            case 1 -> "正常";
            case 2 -> "已冻结";
            case 3 -> "待验证";
            default -> "未知";
        };
    }

    // 重要程度名称转换
    public static String getImportanceName(Integer level) {
        if (level == null) return "未设置";
        return switch (level) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            case 4 -> "极高";
            default -> "未设置";
        };
    }
}
