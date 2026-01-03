package wo1261931780.accountManage.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志视图对象
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "登录日志信息")
public class LoginLogVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录类型: LOGIN-登录, LOGOUT-登出")
    private String loginType;

    @Schema(description = "登录类型文本")
    private String loginTypeText;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录状态: 0-失败, 1-成功")
    private Integer status;

    @Schema(description = "登录状态文本")
    private String statusText;

    @Schema(description = "提示消息")
    private String message;

    @Schema(description = "登录时间")
    private LocalDateTime createTime;

    /**
     * 获取登录类型文本
     */
    public String getLoginTypeText() {
        if (loginType == null) {
            return "未知";
        }
        return switch (loginType) {
            case "LOGIN" -> "登录";
            case "LOGOUT" -> "登出";
            default -> loginType;
        };
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "成功" : "失败";
    }
}
