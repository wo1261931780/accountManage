package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 密钥轮转结果VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "密钥轮转结果")
public class KeyRotationResultVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "处理的账号总数")
    private Integer totalAccounts;

    @Schema(description = "成功转换的账号数")
    private Integer successCount;

    @Schema(description = "失败的账号数")
    private Integer failedCount;

    @Schema(description = "失败的账号ID列表")
    private java.util.List<Long> failedAccountIds;

    @Schema(description = "轮转开始时间")
    private LocalDateTime startTime;

    @Schema(description = "轮转完成时间")
    private LocalDateTime endTime;

    @Schema(description = "耗时（毫秒）")
    private Long durationMs;

    @Schema(description = "备份文件路径")
    private String backupFilePath;

    @Schema(description = "错误信息")
    private String errorMessage;
}
