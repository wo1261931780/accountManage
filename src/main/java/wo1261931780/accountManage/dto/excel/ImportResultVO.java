package wo1261931780.accountManage.dto.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 导入结果VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Builder
@Schema(description = "导入结果")
public class ImportResultVO {

    /**
     * 总行数
     */
    @Schema(description = "总行数")
    private Integer totalCount;

    /**
     * 成功行数
     */
    @Schema(description = "成功行数")
    private Integer successCount;

    /**
     * 失败行数
     */
    @Schema(description = "失败行数")
    private Integer failCount;

    /**
     * 失败详情
     */
    @Schema(description = "失败详情")
    private List<FailDetail> failDetails;

    /**
     * 失败详情
     */
    @Data
    @Builder
    @Schema(description = "失败详情")
    public static class FailDetail {

        /**
         * 行号
         */
        @Schema(description = "行号")
        private Integer rowNum;

        /**
         * 失败原因
         */
        @Schema(description = "失败原因")
        private String reason;

        /**
         * 原始数据
         */
        @Schema(description = "原始数据")
        private String data;
    }
}
