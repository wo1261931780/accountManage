package wo1261931780.accountManage.dto.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "操作日志信息")
public class OperationLogVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "操作用户ID")
    private Long userId;

    @Schema(description = "操作用户名")
    private String username;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作类型文本")
    private String operationTypeText;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "响应结果")
    private String responseResult;

    @Schema(description = "操作IP")
    private String operationIp;

    @Schema(description = "操作地点")
    private String operationLocation;

    @Schema(description = "操作状态: 0-失败, 1-成功")
    private Integer status;

    @Schema(description = "操作状态文本")
    private String statusText;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "执行时长(毫秒)")
    private Long duration;

    @Schema(description = "操作时间")
    private LocalDateTime createTime;

    /**
     * 获取操作类型文本
     */
    public String getOperationTypeText() {
        if (operationType == null) {
            return "未知";
        }
        return switch (operationType) {
            case "CREATE" -> "创建";
            case "UPDATE" -> "更新";
            case "DELETE" -> "删除";
            case "QUERY" -> "查询";
            case "IMPORT" -> "导入";
            case "EXPORT" -> "导出";
            case "OTHER" -> "其他";
            default -> operationType;
        };
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "成功" : "失败";
    }
}
