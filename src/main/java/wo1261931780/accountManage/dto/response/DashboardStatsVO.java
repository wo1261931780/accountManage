package wo1261931780.accountManage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 统计仪表盘数据VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Schema(description = "统计仪表盘数据")
public class DashboardStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========== 总体统计 ==========

    @Schema(description = "账号总数")
    private Long totalAccounts;

    @Schema(description = "平台总数")
    private Long totalPlatforms;

    @Schema(description = "平台类型总数")
    private Long totalPlatformTypes;

    @Schema(description = "标签总数")
    private Long totalTags;

    // ========== 账号状态统计 ==========

    @Schema(description = "正常账号数")
    private Long normalAccounts;

    @Schema(description = "已冻结账号数")
    private Long frozenAccounts;

    @Schema(description = "已注销账号数")
    private Long cancelledAccounts;

    @Schema(description = "待验证账号数")
    private Long pendingAccounts;

    // ========== 密码安全统计 ==========

    @Schema(description = "密码已过期账号数")
    private Long expiredPasswordAccounts;

    @Schema(description = "密码即将过期账号数（7天内）")
    private Long expiringPasswordAccounts;

    @Schema(description = "密码30天内过期账号数")
    private Long expiring30DaysAccounts;

    // ========== 重要程度统计 ==========

    @Schema(description = "极高重要程度账号数")
    private Long criticalAccounts;

    @Schema(description = "高重要程度账号数")
    private Long highAccounts;

    @Schema(description = "中重要程度账号数")
    private Long mediumAccounts;

    @Schema(description = "低重要程度账号数")
    private Long lowAccounts;

    // ========== 详细统计列表 ==========

    @Schema(description = "按平台类型统计")
    private List<PlatformTypeStatsVO> platformTypeStats;

    @Schema(description = "按平台统计（Top 10）")
    private List<PlatformStatsVO> topPlatformStats;

    @Schema(description = "最近7天新增账号趋势")
    private List<DailyStatsVO> recentTrend;

    /**
     * 平台类型统计
     */
    @Data
    public static class PlatformTypeStatsVO {
        @Schema(description = "平台类型ID")
        private Long platformTypeId;

        @Schema(description = "平台类型名称")
        private String typeName;

        @Schema(description = "图标")
        private String icon;

        @Schema(description = "平台数量")
        private Integer platformCount;

        @Schema(description = "账号数量")
        private Integer accountCount;
    }

    /**
     * 平台统计
     */
    @Data
    public static class PlatformStatsVO {
        @Schema(description = "平台ID")
        private Long platformId;

        @Schema(description = "平台名称")
        private String platformName;

        @Schema(description = "平台图标")
        private String platformIcon;

        @Schema(description = "账号数量")
        private Integer accountCount;
    }

    /**
     * 每日统计
     */
    @Data
    public static class DailyStatsVO {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "新增账号数")
        private Integer newAccounts;

        @Schema(description = "活跃账号数")
        private Integer activeAccounts;
    }
}
