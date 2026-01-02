package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据备份记录实体
 */
@Data
@TableName("sys_backup")
@Schema(description = "数据备份记录")
public class SysBackup {

    @TableId(type = IdType.AUTO)
    @Schema(description = "备份ID")
    private Long id;

    @Schema(description = "备份名称")
    private String backupName;

    @Schema(description = "备份类型: 1-手动备份 2-自动备份")
    private Integer backupType;

    @Schema(description = "备份文件路径")
    private String backupPath;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "备份表数量")
    private Integer tableCount;

    @Schema(description = "备份记录总数")
    private Integer recordCount;

    @Schema(description = "状态: 0-进行中 1-成功 2-失败")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long createBy;

    // 备份类型常量
    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_AUTO = 2;

    // 状态常量
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 2;
}
