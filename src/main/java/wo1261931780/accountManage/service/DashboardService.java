package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.response.DashboardStatsVO;

/**
 * 统计仪表盘服务
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据
     */
    DashboardStatsVO getStats();

    /**
     * 获取账号总数
     *
     * @return 账号总数
     */
    Long getTotalAccounts();

    /**
     * 获取平台总数
     *
     * @return 平台总数
     */
    Long getTotalPlatforms();

    /**
     * 获取密码过期统计
     *
     * @return 过期统计
     */
    DashboardStatsVO.PlatformTypeStatsVO getPasswordExpiryStats();
}
