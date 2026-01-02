package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 密码查看响应VO（不脱敏）
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "密码查看响应")
public class PasswordViewVO {

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "账号名称")
    private String accountName;

    @Schema(description = "平台名称")
    private String platformName;

    @Schema(description = "密码（明文）")
    private String password;

    @Schema(description = "密码更新时间")
    private String passwordUpdateTime;

    @Schema(description = "密码有效天数")
    private Integer passwordValidDays;

    @Schema(description = "是否即将过期")
    private Boolean willExpire;

    @Schema(description = "剩余有效天数")
    private Integer remainingDays;
}
