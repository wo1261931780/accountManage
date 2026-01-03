package wo1261931780.accountManage.dto.ip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * IP黑名单查询DTO
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Data
@Schema(description = "IP黑名单查询参数")
public class IpBlacklistQueryDTO {

    @Schema(description = "IP地址（模糊查询）")
    private String ipAddress;

    @Schema(description = "来源：MANUAL, AUTO, LOGIN_FAIL")
    private String source;

    @Schema(description = "状态：true-封禁中, false-已解封")
    private Boolean status;

    @Schema(description = "是否永久封禁")
    private Boolean isPermanent;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}
