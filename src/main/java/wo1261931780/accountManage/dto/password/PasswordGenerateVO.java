package wo1261931780.accountManage.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 密码生成结果VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Builder
@Schema(description = "密码生成结果")
public class PasswordGenerateVO {

    /**
     * 生成的密码列表
     */
    @Schema(description = "生成的密码列表")
    private List<String> passwords;

    /**
     * 密码强度评估
     */
    @Schema(description = "密码强度评估")
    private PasswordStrength strength;

    /**
     * 密码强度详情
     */
    @Data
    @Builder
    @Schema(description = "密码强度详情")
    public static class PasswordStrength {

        /**
         * 强度等级: WEAK, MEDIUM, STRONG, VERY_STRONG
         */
        @Schema(description = "强度等级", example = "STRONG")
        private String level;

        /**
         * 强度分数 (0-100)
         */
        @Schema(description = "强度分数", example = "85")
        private Integer score;

        /**
         * 强度描述
         */
        @Schema(description = "强度描述", example = "密码强度较高")
        private String description;

        /**
         * 预估破解时间
         */
        @Schema(description = "预估破解时间", example = "约需数百年")
        private String crackTime;
    }
}
