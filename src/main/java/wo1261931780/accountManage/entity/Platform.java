package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台实体
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@TableName("sys_platform")
@Schema(description = "平台")
public class Platform implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 平台编码
     */
    @Schema(description = "平台编码", example = "WECHAT")
    private String platformCode;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称", example = "微信")
    private String platformName;

    /**
     * 平台英文名称
     */
    @Schema(description = "平台英文名称", example = "WeChat")
    private String platformNameEn;

    /**
     * 平台类型ID
     */
    @Schema(description = "平台类型ID")
    private Long platformTypeId;

    /**
     * 平台官网地址
     */
    @Schema(description = "平台官网地址", example = "https://weixin.qq.com")
    private String platformUrl;

    /**
     * 平台图标URL
     */
    @Schema(description = "平台图标URL")
    private String platformIcon;

    /**
     * 国家/地区代码
     */
    @Schema(description = "国家/地区代码", example = "CN")
    private String country;

    /**
     * 国家/地区名称
     */
    @Schema(description = "国家/地区名称", example = "中国")
    private String countryName;

    /**
     * 平台描述
     */
    @Schema(description = "平台描述")
    private String description;

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

    // ============ 非数据库字段 ============

    /**
     * 平台类型名称（关联查询用）
     */
    @TableField(exist = false)
    @Schema(description = "平台类型名称")
    private String platformTypeName;
}
