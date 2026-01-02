package wo1261931780.accountManage.dto.backup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 备份创建DTO
 */
@Data
@Schema(description = "备份创建请求")
public class BackupCreateDTO {

    @Schema(description = "备份名称（可选，默认自动生成）")
    private String backupName;

    @Schema(description = "备注")
    private String remark;
}
