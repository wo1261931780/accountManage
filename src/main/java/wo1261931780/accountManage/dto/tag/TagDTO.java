package wo1261931780.accountManage.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 标签创建/更新DTO
 */
@Data
@Schema(description = "标签创建/更新请求")
public class TagDTO {

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称最多50个字符")
    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tagName;

    @Schema(description = "标签颜色", example = "#409EFF")
    private String tagColor;

    @Schema(description = "排序")
    private Integer sortOrder;
}
