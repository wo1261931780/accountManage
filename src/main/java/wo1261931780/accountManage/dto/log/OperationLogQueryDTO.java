package wo1261931780.accountManage.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "操作日志查询条件")
public class OperationLogQueryDTO {

    @Schema(description = "操作用户名")
    private String username;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作类型: CREATE, UPDATE, DELETE, QUERY, IMPORT, EXPORT等")
    private String operationType;

    @Schema(description = "操作状态: 0-失败, 1-成功")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;
}
