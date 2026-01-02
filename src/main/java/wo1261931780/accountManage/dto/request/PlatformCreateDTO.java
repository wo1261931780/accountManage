package wo1261931780.accountManage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 平台创建请求
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@Schema(description = "平台创建请求")
public class PlatformCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台编码
     */
    @NotBlank(message = "平台编码不能为空")
    @Size(max = 50, message = "平台编码长度不能超过50")
    @Schema(description = "平台编码", example = "WECHAT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String platformCode;

    /**
     * 平台名称
     */
    @NotBlank(message = "平台名称不能为空")
    @Size(max = 100, message = "平台名称长度不能超过100")
    @Schema(description = "平台名称", example = "微信", requiredMode = Schema.RequiredMode.REQUIRED)
    private String platformName;

    /**
     * 平台英文名称
     */
    @Size(max = 100, message = "平台英文名称长度不能超过100")
    @Schema(description = "平台英文名称", example = "WeChat")
    private String platformNameEn;

    /**
     * 平台类型ID
     */
    @NotNull(message = "平台类型不能为空")
    @Schema(description = "平台类型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long platformTypeId;

    /**
     * 平台官网地址
     */
    @Schema(description = "平台官网地址", example = "https://weixin.qq.com")
    private String platformUrl;

    /**
     * 平台图标URL
     */
    @Size(max = 500, message = "平台图标URL长度不能超过500")
    @Schema(description = "平台图标URL")
    private String platformIcon;

    /**
     * 国家/地区代码
     */
    @Size(max = 50, message = "国家/地区代码长度不能超过50")
    @Schema(description = "国家/地区代码", example = "CN")
    private String country = "CN";

    /**
     * 国家/地区名称
     */
    @Size(max = 100, message = "国家/地区名称长度不能超过100")
    @Schema(description = "国家/地区名称", example = "中国")
    private String countryName = "中国";

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
    private Integer status = 1;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
