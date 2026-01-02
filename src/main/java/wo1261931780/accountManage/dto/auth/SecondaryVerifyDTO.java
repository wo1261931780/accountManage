package wo1261931780.accountManage.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 二次验证请求DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "二次验证请求")
public class SecondaryVerifyDTO {

    @NotNull(message = "账号ID不能为空")
    @Schema(description = "账号ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long accountId;

    @NotBlank(message = "验证密码不能为空")
    @Schema(description = "当前登录用户的密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "操作原因/备注")
    private String reason;
}
