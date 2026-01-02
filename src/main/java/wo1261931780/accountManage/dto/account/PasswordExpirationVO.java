package wo1261931780.accountManage.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 密码过期提醒VO
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Data
@Builder
@Schema(description = "密码过期提醒结果")
public class PasswordExpirationVO {

    /**
     * 已过期账号列表
     */
    @Schema(description = "已过期账号列表")
    private List<ExpirationDetail> expiredAccounts;

    /**
     * 即将过期账号列表 (7天内)
     */
    @Schema(description = "即将过期账号列表")
    private List<ExpirationDetail> expiringAccounts;

    /**
     * 统计信息
     */
    @Schema(description = "统计信息")
    private Statistics statistics;

    /**
     * 过期详情
     */
    @Data
    @Builder
    @Schema(description = "过期详情")
    public static class ExpirationDetail {

        /**
         * 账号ID
         */
        @Schema(description = "账号ID")
        private Long accountId;

        /**
         * 账号名称
         */
        @Schema(description = "账号名称")
        private String accountName;

        /**
         * 平台名称
         */
        @Schema(description = "平台名称")
        private String platformName;

        /**
         * 平台类型
         */
        @Schema(description = "平台类型")
        private String platformTypeName;

        /**
         * 密码最后更新时间
         */
        @Schema(description = "密码最后更新时间")
        private LocalDateTime passwordUpdateTime;

        /**
         * 密码有效期(天)
         */
        @Schema(description = "密码有效期(天)")
        private Integer passwordValidDays;

        /**
         * 过期时间
         */
        @Schema(description = "过期时间")
        private LocalDateTime expirationTime;

        /**
         * 距离过期天数（负数表示已过期）
         */
        @Schema(description = "距离过期天数（负数表示已过期）")
        private Integer daysUntilExpiration;

        /**
         * 重要程度
         */
        @Schema(description = "重要程度")
        private Integer importanceLevel;
    }

    /**
     * 统计信息
     */
    @Data
    @Builder
    @Schema(description = "统计信息")
    public static class Statistics {

        /**
         * 已过期数量
         */
        @Schema(description = "已过期数量")
        private Integer expiredCount;

        /**
         * 7天内过期数量
         */
        @Schema(description = "7天内过期数量")
        private Integer expiringIn7DaysCount;

        /**
         * 30天内过期数量
         */
        @Schema(description = "30天内过期数量")
        private Integer expiringIn30DaysCount;

        /**
         * 设置了有效期的账号总数
         */
        @Schema(description = "设置了有效期的账号总数")
        private Integer totalWithExpiration;
    }
}
