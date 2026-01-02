package wo1261931780.accountManage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 平台类型创建请求
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@Schema(description = "平台类型创建请求")
public class PlatformTypeCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型编码
     */
    @NotBlank(message = "类型编码不能为空")
    @Size(max = 50, message = "类型编码长度不能超过50")
    @Schema(description = "类型编码", example = "SOCIAL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String typeCode;

    /**
     * 类型名称
     */
    @NotBlank(message = "类型名称不能为空")
    @Size(max = 100, message = "类型名称长度不能超过100")
    @Schema(description = "类型名称", example = "社交通讯", requiredMode = Schema.RequiredMode.REQUIRED)
    private String typeName;

    /**
     * 类型英文名称
     */
    @Size(max = 100, message = "类型英文名称长度不能超过100")
    @Schema(description = "类型英文名称", example = "Social & Communication")
    private String typeNameEn;

    /**
     * 类型图标
     */
    @Size(max = 255, message = "类型图标长度不能超过255")
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
    private Integer status = 1;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
