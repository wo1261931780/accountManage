package wo1261931780.accountManage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@TableName("sys_operation_log")
@Schema(description = "操作日志")
public class SysOperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 操作用户ID
     */
    @Schema(description = "操作用户ID")
    private Long userId;

    /**
     * 操作用户名
     */
    @Schema(description = "操作用户名")
    private String username;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块", example = "账号管理")
    private String module;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;

    /**
     * 操作描述
     */
    @Schema(description = "操作描述", example = "创建账号")
    private String description;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;

    /**
     * 请求URL
     */
    @Schema(description = "请求URL")
    private String requestUrl;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    private String requestParams;

    /**
     * 响应结果
     */
    @Schema(description = "响应结果")
    private String responseResult;

    /**
     * 操作IP
     */
    @Schema(description = "操作IP")
    private String operationIp;

    /**
     * 操作地点
     */
    @Schema(description = "操作地点")
    private String operationLocation;

    /**
     * 操作状态: 0-失败, 1-成功
     */
    @Schema(description = "操作状态: 0-失败, 1-成功")
    private Integer status;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * 执行时长(毫秒)
     */
    @Schema(description = "执行时长(毫秒)")
    private Long duration;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "操作时间")
    private LocalDateTime createTime;
}
