package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@TableName("sys_login_log")
@Schema(description = "登录日志")
public class SysLoginLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 登录类型: LOGIN-登录, LOGOUT-登出
     */
    @Schema(description = "登录类型: LOGIN-登录, LOGOUT-登出")
    private String loginType;

    /**
     * 登录IP
     */
    @Schema(description = "登录IP")
    private String loginIp;

    /**
     * 登录地点
     */
    @Schema(description = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @Schema(description = "浏览器类型")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * 登录状态: 0-失败, 1-成功
     */
    @Schema(description = "登录状态: 0-失败, 1-成功")
    private Integer status;

    /**
     * 提示消息
     */
    @Schema(description = "提示消息")
    private String message;

    /**
     * 登录时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "登录时间")
    private LocalDateTime createTime;
}
