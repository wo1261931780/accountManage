package wo1261931780.accountManage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 平台查询请求
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "平台查询请求")
public class PlatformQueryDTO extends PageRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台名称（模糊查询）
     */
    @Schema(description = "平台名称（模糊查询）", example = "微信")
    private String platformName;

    /**
     * 平台类型ID
     */
    @Schema(description = "平台类型ID")
    private Long platformTypeId;

    /**
     * 国家/地区代码
     */
    @Schema(description = "国家/地区代码", example = "CN")
    private String country;

    /**
     * 状态: 0-禁用, 1-启用
     */
    @Schema(description = "状态: 0-禁用, 1-启用", example = "1")
    private Integer status;
}
