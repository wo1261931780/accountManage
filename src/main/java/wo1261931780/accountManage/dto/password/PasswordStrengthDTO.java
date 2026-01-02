package wo1261931780.accountManage.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码强度检查请求DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "密码强度检查请求")
public class PasswordStrengthDTO {

    /**
     * 待检查的密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "待检查的密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
