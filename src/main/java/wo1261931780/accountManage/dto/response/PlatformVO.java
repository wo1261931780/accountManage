package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台响应VO
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@Schema(description = "平台响应")
public class PlatformVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 平台编码
     */
    @Schema(description = "平台编码")
    private String platformCode;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * 平台英文名称
     */
    @Schema(description = "平台英文名称")
    private String platformNameEn;

    /**
     * 平台类型ID
     */
    @Schema(description = "平台类型ID")
    private Long platformTypeId;

    /**
     * 平台类型名称
     */
    @Schema(description = "平台类型名称")
    private String platformTypeName;

    /**
     * 平台官网地址
     */
    @Schema(description = "平台官网地址")
    private String platformUrl;

    /**
     * 平台图标URL
     */
    @Schema(description = "平台图标URL")
    private String platformIcon;

    /**
     * 国家/地区代码
     */
    @Schema(description = "国家/地区代码")
    private String country;

    /**
     * 国家/地区名称
     */
    @Schema(description = "国家/地区名称")
    private String countryName;

    /**
     * 平台描述
     */
    @Schema(description = "平台描述")
    private String description;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    private Integer sortOrder;

    /**
     * 状态: 0-禁用, 1-启用
     */
    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    /**
     * 状态文本
     */
    @Schema(description = "状态文本")
    private String statusText;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 账号数量（统计用）
     */
    @Schema(description = "账号数量")
    private Integer accountCount;

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) return "";
        return status == 1 ? "启用" : "禁用";
    }
}
