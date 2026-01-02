package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wo1261931780.accountManage.dto.response.DashboardStatsVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.entity.PlatformType;
import wo1261931780.accountManage.entity.Tag;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.mapper.PlatformTypeMapper;
import wo1261931780.accountManage.mapper.TagMapper;
import wo1261931780.accountManage.service.DashboardService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计仪表盘服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;
    private final PlatformTypeMapper platformTypeMapper;
    private final TagMapper tagMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 总体统计
        stats.setTotalAccounts(getTotalAccounts());
        stats.setTotalPlatforms(getTotalPlatforms());
        stats.setTotalPlatformTypes(getTotalPlatformTypes());
        stats.setTotalTags(getTotalTags());

        // 账号状态统计
        Map<Integer, Long> statusStats = getAccountStatusStats();
        stats.setNormalAccounts(statusStats.getOrDefault(1, 0L));
        stats.setFrozenAccounts(statusStats.getOrDefault(2, 0L));
        stats.setCancelledAccounts(statusStats.getOrDefault(0, 0L));
        stats.setPendingAccounts(statusStats.getOrDefault(3, 0L));

        // 重要程度统计
        Map<Integer, Long> importanceStats = getImportanceStats();
        stats.setCriticalAccounts(importanceStats.getOrDefault(4, 0L));
        stats.setHighAccounts(importanceStats.getOrDefault(3, 0L));
        stats.setMediumAccounts(importanceStats.getOrDefault(2, 0L));
        stats.setLowAccounts(importanceStats.getOrDefault(1, 0L));

        // 密码过期统计
        stats.setExpiredPasswordAccounts(getExpiredPasswordCount());
        stats.setExpiringPasswordAccounts(getExpiringPasswordCount(7));
        stats.setExpiring30DaysAccounts(getExpiringPasswordCount(30));

        // 平台类型统计
        stats.setPlatformTypeStats(getPlatformTypeStats());

        // Top 10 平台统计
        stats.setTopPlatformStats(getTopPlatformStats(10));

        // 最近7天趋势
        stats.setRecentTrend(getRecentTrend(7));

        return stats;
    }

    @Override
    public Long getTotalAccounts() {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0);
        return accountMapper.selectCount(wrapper);
    }

    @Override
    public Long getTotalPlatforms() {
        LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Platform::getDeleted, 0);
        return platformMapper.selectCount(wrapper);
    }

    @Override
    public DashboardStatsVO.PlatformTypeStatsVO getPasswordExpiryStats() {
        // 这个方法保留用于特殊场景
        return null;
    }

    private Long getTotalPlatformTypes() {
        LambdaQueryWrapper<PlatformType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlatformType::getDeleted, 0);
        return platformTypeMapper.selectCount(wrapper);
    }

    private Long getTotalTags() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getDeleted, 0);
        return tagMapper.selectCount(wrapper);
    }

    private Map<Integer, Long> getAccountStatusStats() {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0)
                .select(Account::getAccountStatus);

        List<Account> accounts = accountMapper.selectList(wrapper);
        return accounts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAccountStatus() != null ? a.getAccountStatus() : -1,
                        Collectors.counting()
                ));
    }

    private Map<Integer, Long> getImportanceStats() {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0)
                .select(Account::getImportanceLevel);

        List<Account> accounts = accountMapper.selectList(wrapper);
        return accounts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getImportanceLevel() != null ? a.getImportanceLevel() : 0,
                        Collectors.counting()
                ));
    }

    private Long getExpiredPasswordCount() {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0)
                .isNotNull(Account::getPasswordValidDays)
                .gt(Account::getPasswordValidDays, 0)
                .apply("DATE_ADD(password_update_time, INTERVAL password_valid_days DAY) < NOW()");
        return accountMapper.selectCount(wrapper);
    }

    private Long getExpiringPasswordCount(int days) {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0)
                .isNotNull(Account::getPasswordValidDays)
                .gt(Account::getPasswordValidDays, 0)
                .apply("DATE_ADD(password_update_time, INTERVAL password_valid_days DAY) >= NOW()")
                .apply("DATE_ADD(password_update_time, INTERVAL password_valid_days DAY) <= DATE_ADD(NOW(), INTERVAL " + days + " DAY)");
        return accountMapper.selectCount(wrapper);
    }

    private List<DashboardStatsVO.PlatformTypeStatsVO> getPlatformTypeStats() {
        // 获取所有平台类型
        LambdaQueryWrapper<PlatformType> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(PlatformType::getDeleted, 0);
        List<PlatformType> types = platformTypeMapper.selectList(typeWrapper);

        // 获取每个类型下的平台数量
        LambdaQueryWrapper<Platform> platformWrapper = new LambdaQueryWrapper<>();
        platformWrapper.eq(Platform::getDeleted, 0);
        List<Platform> platforms = platformMapper.selectList(platformWrapper);
        Map<Long, Long> platformCountByType = platforms.stream()
                .collect(Collectors.groupingBy(Platform::getPlatformTypeId, Collectors.counting()));

        // 获取每个平台的账号数量
        LambdaQueryWrapper<Account> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper.eq(Account::getDeleted, 0);
        List<Account> accounts = accountMapper.selectList(accountWrapper);
        Map<Long, Long> accountCountByPlatform = accounts.stream()
                .filter(a -> a.getPlatformId() != null)
                .collect(Collectors.groupingBy(Account::getPlatformId, Collectors.counting()));

        // 计算每个类型的账号数量
        Map<Long, Integer> accountCountByType = platforms.stream()
                .collect(Collectors.groupingBy(
                        Platform::getPlatformTypeId,
                        Collectors.summingInt(p -> accountCountByPlatform.getOrDefault(p.getId(), 0L).intValue())
                ));

        return types.stream()
                .map(type -> {
                    DashboardStatsVO.PlatformTypeStatsVO vo = new DashboardStatsVO.PlatformTypeStatsVO();
                    vo.setPlatformTypeId(type.getId());
                    vo.setTypeName(type.getTypeName());
                    vo.setIcon(type.getIcon());
                    vo.setPlatformCount(platformCountByType.getOrDefault(type.getId(), 0L).intValue());
                    vo.setAccountCount(accountCountByType.getOrDefault(type.getId(), 0));
                    return vo;
                })
                .sorted((a, b) -> b.getAccountCount() - a.getAccountCount())
                .toList();
    }

    private List<DashboardStatsVO.PlatformStatsVO> getTopPlatformStats(int limit) {
        // 获取平台
        LambdaQueryWrapper<Platform> platformWrapper = new LambdaQueryWrapper<>();
        platformWrapper.eq(Platform::getDeleted, 0);
        List<Platform> platforms = platformMapper.selectList(platformWrapper);

        // 获取账号数量
        LambdaQueryWrapper<Account> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper.eq(Account::getDeleted, 0);
        List<Account> accounts = accountMapper.selectList(accountWrapper);
        Map<Long, Long> accountCountByPlatform = accounts.stream()
                .filter(a -> a.getPlatformId() != null)
                .collect(Collectors.groupingBy(Account::getPlatformId, Collectors.counting()));

        return platforms.stream()
                .map(p -> {
                    DashboardStatsVO.PlatformStatsVO vo = new DashboardStatsVO.PlatformStatsVO();
                    vo.setPlatformId(p.getId());
                    vo.setPlatformName(p.getPlatformName());
                    vo.setPlatformIcon(p.getPlatformIcon());
                    vo.setAccountCount(accountCountByPlatform.getOrDefault(p.getId(), 0L).intValue());
                    return vo;
                })
                .filter(vo -> vo.getAccountCount() > 0)
                .sorted((a, b) -> b.getAccountCount() - a.getAccountCount())
                .limit(limit)
                .toList();
    }

    private List<DashboardStatsVO.DailyStatsVO> getRecentTrend(int days) {
        List<DashboardStatsVO.DailyStatsVO> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            // 新增账号数
            LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Account::getDeleted, 0)
                    .ge(Account::getCreateTime, startOfDay)
                    .lt(Account::getCreateTime, endOfDay);
            long newCount = accountMapper.selectCount(wrapper);

            DashboardStatsVO.DailyStatsVO dailyStats = new DashboardStatsVO.DailyStatsVO();
            dailyStats.setDate(date.format(formatter));
            dailyStats.setNewAccounts((int) newCount);
            dailyStats.setActiveAccounts(0); // 可以后续扩展

            trend.add(dailyStats);
        }

        return trend;
    }
}
