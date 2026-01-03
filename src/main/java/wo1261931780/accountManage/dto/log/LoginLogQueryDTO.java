package wo1261931780.accountManage.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 登录日志查询DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "登录日志查询条件")
public class LoginLogQueryDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "登录类型: LOGIN-登录, LOGOUT-登出")
    private String loginType;

    @Schema(description = "登录状态: 0-失败, 1-成功")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;
}
