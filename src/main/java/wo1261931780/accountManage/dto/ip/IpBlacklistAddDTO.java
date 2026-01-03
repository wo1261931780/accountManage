package wo1261931780.accountManage.dto.ip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 添加IP黑名单请求DTO
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Data
@Schema(description = "添加IP黑名单请求")
public class IpBlacklistAddDTO {

    @Schema(description = "IP地址", example = "192.168.1.100", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "IP地址不能为空")
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$",
            message = "IP地址格式不正确")
    private String ipAddress;

    @Schema(description = "封禁原因", example = "恶意攻击")
    private String reason;

    @Schema(description = "是否永久封禁", example = "false")
    private Boolean isPermanent = false;

    @Schema(description = "封禁时长（分钟），永久封禁时无效", example = "1440")
    private Integer durationMinutes = 1440; // 默认24小时

    @Schema(description = "备注", example = "多次尝试暴力破解")
    private String remark;
}
