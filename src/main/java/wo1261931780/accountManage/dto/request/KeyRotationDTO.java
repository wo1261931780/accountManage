package wo1261931780.accountManage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 密钥轮转请求DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "密钥轮转请求")
public class KeyRotationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 新的AES密钥（16/24/32位）
     */
    @NotBlank(message = "新密钥不能为空")
    @Size(min = 16, max = 32, message = "密钥长度必须为16-32位")
    @Schema(description = "新的AES密钥")
    private String newAesKey;

    /**
     * 当前用户密码（二次验证）
     */
    @NotBlank(message = "当前密码不能为空")
    @Schema(description = "当前用户密码（二次验证）")
    private String currentPassword;

    /**
     * 是否备份旧密钥
     */
    @Schema(description = "是否备份旧密钥", defaultValue = "true")
    private Boolean backupOldKey = true;
}
