package wo1261931780.accountManage.dto.ip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IP黑名单响应VO
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Data
@Schema(description = "IP黑名单信息")
public class IpBlacklistVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "封禁原因")
    private String reason;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "来源描述")
    private String sourceDesc;

    @Schema(description = "失败次数")
    private Integer failCount;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "是否永久封禁")
    private Boolean isPermanent;

    @Schema(description = "状态：0-已解封, 1-封禁中")
    private Boolean status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "剩余封禁时间（秒）")
    private Long remainingSeconds;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;

    @Schema(description = "备注")
    private String remark;
}
