package wo1261931780.accountManage.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordDTO {

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "新密码", example = "newpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
