package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号访问记录实体
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@TableName("sys_account_access_log")
@Schema(description = "账号访问记录")
public class AccountAccessLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 账号ID
     */
    @Schema(description = "账号ID")
    private Long accountId;

    /**
     * 访问类型: 1-查看, 2-复制密码, 3-编辑
     */
    @Schema(description = "访问类型: 1-查看, 2-复制密码, 3-编辑")
    private Integer accessType;

    /**
     * 访问时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "访问时间")
    private LocalDateTime accessTime;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    private String userAgent;

    // 访问类型常量
    public static final int TYPE_VIEW = 1;
    public static final int TYPE_COPY_PASSWORD = 2;
    public static final int TYPE_EDIT = 3;
}
