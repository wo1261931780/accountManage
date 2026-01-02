package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 密码修改历史实体
 */
@Data
@TableName("sys_password_history")
@Schema(description = "密码修改历史")
public class PasswordHistory {

    @TableId(type = IdType.AUTO)
    @Schema(description = "历史ID")
    private Long id;

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "加密后的密码")
    private String passwordEncrypted;

    @Schema(description = "密码盐值")
    private String passwordSalt;

    @Schema(description = "修改原因")
    private String changeReason;

    @Schema(description = "修改类型: 1-用户修改 2-系统重置 3-过期更换 4-批量导入")
    private Integer changeType;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "操作人ID")
    private Long createBy;

    // 修改类型常量
    public static final int TYPE_USER_MODIFY = 1;
    public static final int TYPE_SYSTEM_RESET = 2;
    public static final int TYPE_EXPIRED_CHANGE = 3;
    public static final int TYPE_BATCH_IMPORT = 4;
}
