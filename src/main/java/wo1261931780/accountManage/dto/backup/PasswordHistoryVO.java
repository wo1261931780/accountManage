package wo1261931780.accountManage.dto.backup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 密码历史VO
 */
@Data
@Schema(description = "密码历史信息")
public class PasswordHistoryVO {

    @Schema(description = "历史ID")
    private Long id;

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "账号名")
    private String accountName;

    @Schema(description = "平台名称")
    private String platformName;

    @Schema(description = "修改原因")
    private String changeReason;

    @Schema(description = "修改类型: 1-用户修改 2-系统重置 3-过期更换 4-批量导入")
    private Integer changeType;

    @Schema(description = "修改类型名称")
    private String changeTypeName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "操作人ID")
    private Long createBy;

    @Schema(description = "操作人名称")
    private String createByName;

    /**
     * 获取修改类型名称
     */
    public static String getChangeTypeName(Integer changeType) {
        if (changeType == null) {
            return "未知";
        }
        return switch (changeType) {
            case 1 -> "用户修改";
            case 2 -> "系统重置";
            case 3 -> "过期更换";
            case 4 -> "批量导入";
            default -> "未知";
        };
    }
}
