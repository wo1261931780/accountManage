package wo1261931780.accountManage.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import wo1261931780.accountManage.aspect.RateLimitAspect;
import wo1261931780.accountManage.service.AccountAccessService;

/**
 * 定时任务配置
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTaskConfig {

    private final RateLimitAspect rateLimitAspect;
    private final AccountAccessService accountAccessService;

    /**
     * 每5分钟清理过期的限流记录
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void cleanExpiredRateLimitRecords() {
        try {
            rateLimitAspect.cleanExpiredRecords();
        } catch (Exception e) {
            log.error("清理过期限流记录失败", e);
        }
    }

    /**
     * 每天凌晨3点清理30天前的访问记录
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldAccessLogs() {
        try {
            int deleted = accountAccessService.clearOldAccessLogs(30);
            log.info("定时清理访问记录完成, 删除 {} 条", deleted);
        } catch (Exception e) {
            log.error("清理访问记录失败", e);
        }
    }
}
