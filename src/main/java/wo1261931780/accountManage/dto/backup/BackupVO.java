package wo1261931780.accountManage.dto.backup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备份信息VO
 */
@Data
@Schema(description = "备份信息")
public class BackupVO {

    @Schema(description = "备份ID")
    private Long id;

    @Schema(description = "备份名称")
    private String backupName;

    @Schema(description = "备份类型: 1-手动备份 2-自动备份")
    private Integer backupType;

    @Schema(description = "备份类型名称")
    private String backupTypeName;

    @Schema(description = "备份文件路径")
    private String backupPath;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "文件大小(可读格式)")
    private String fileSizeReadable;

    @Schema(description = "备份表数量")
    private Integer tableCount;

    @Schema(description = "备份记录总数")
    private Integer recordCount;

    @Schema(description = "状态: 0-进行中 1-成功 2-失败")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long createBy;

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(Long bytes) {
        if (bytes == null || bytes <= 0) {
            return "0 B";
        }
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", size, units[unitIndex]);
    }
}
