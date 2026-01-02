package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台类型实体
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@TableName("sys_platform_type")
@Schema(description = "平台类型")
public class PlatformType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码", example = "SOCIAL")
    private String typeCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", example = "社交通讯")
    private String typeName;

    /**
     * 类型英文名称
     */
    @Schema(description = "类型英文名称", example = "Social & Communication")
    private String typeNameEn;

    /**
     * 类型图标
     */
    @Schema(description = "类型图标")
    private String icon;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;

    /**
     * 状态: 0-禁用, 1-启用
     */
    @Schema(description = "状态: 0-禁用, 1-启用", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "逻辑删除: 0-未删除, 1-已删除")
    private Integer deleted;
}
