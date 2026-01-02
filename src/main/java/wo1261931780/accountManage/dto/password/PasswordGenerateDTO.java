package wo1261931780.accountManage.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 密码生成请求DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "密码生成请求")
public class PasswordGenerateDTO {

    /**
     * 密码长度
     */
    @Min(value = 6, message = "密码长度不能小于6位")
    @Max(value = 64, message = "密码长度不能大于64位")
    @Schema(description = "密码长度", example = "16", defaultValue = "16")
    private Integer length = 16;

    /**
     * 是否包含大写字母
     */
    @Schema(description = "是否包含大写字母", example = "true", defaultValue = "true")
    private Boolean includeUppercase = true;

    /**
     * 是否包含小写字母
     */
    @Schema(description = "是否包含小写字母", example = "true", defaultValue = "true")
    private Boolean includeLowercase = true;

    /**
     * 是否包含数字
     */
    @Schema(description = "是否包含数字", example = "true", defaultValue = "true")
    private Boolean includeNumbers = true;

    /**
     * 是否包含特殊字符
     */
    @Schema(description = "是否包含特殊字符", example = "true", defaultValue = "true")
    private Boolean includeSpecialChars = true;

    /**
     * 排除相似字符 (如 0/O, 1/l/I)
     */
    @Schema(description = "排除相似字符", example = "false", defaultValue = "false")
    private Boolean excludeSimilar = false;

    /**
     * 排除歧义字符 (如 {}, [], (), /)
     */
    @Schema(description = "排除歧义字符", example = "false", defaultValue = "false")
    private Boolean excludeAmbiguous = false;

    /**
     * 自定义特殊字符集
     */
    @Schema(description = "自定义特殊字符集", example = "!@#$%^&*")
    private String customSpecialChars;

    /**
     * 生成数量
     */
    @Min(value = 1, message = "生成数量不能小于1")
    @Max(value = 100, message = "生成数量不能大于100")
    @Schema(description = "生成数量", example = "1", defaultValue = "1")
    private Integer count = 1;
}
