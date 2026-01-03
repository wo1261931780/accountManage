package wo1261931780.accountManage.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户请求DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateDTO {

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称", example = "更新昵称")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "updated@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138002")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "状态: 0-禁用, 1-启用", example = "1")
    private Integer status;
}
