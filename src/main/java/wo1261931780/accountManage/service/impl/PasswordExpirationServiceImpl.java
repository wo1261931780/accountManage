package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.dto.account.PasswordExpirationVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.entity.PlatformType;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.mapper.PlatformTypeMapper;
import wo1261931780.accountManage.service.PasswordExpirationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 密码过期提醒服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordExpirationServiceImpl implements PasswordExpirationService {

    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;
    private final PlatformTypeMapper platformTypeMapper;

    @Override
    public PasswordExpirationVO getExpirationReminder() {
        return getExpiringWithinDays(7);
    }

    @Override
    public PasswordExpirationVO getExpiringWithinDays(int days) {
        // 查询所有设置了有效期的账号
        List<Account> accountsWithExpiration = accountMapper.selectList(
                new LambdaQueryWrapper<Account>()
                        .isNotNull(Account::getPasswordValidDays)
                        .gt(Account::getPasswordValidDays, 0)
                        .orderByDesc(Account::getImportanceLevel)
                        .orderByAsc(Account::getPasswordUpdateTime)
        );

        if (accountsWithExpiration.isEmpty()) {
            return buildEmptyResult();
        }

        // 加载平台信息
        List<Long> platformIds = accountsWithExpiration.stream()
                .map(Account::getPlatformId)
                .distinct()
                .toList();
        Map<Long, Platform> platformMap = platformMapper.selectBatchIds(platformIds).stream()
                .collect(Collectors.toMap(Platform::getId, p -> p));

        // 加载平台类型信息
        List<Long> typeIds = platformMap.values().stream()
                .map(Platform::getPlatformTypeId)
                .distinct()
                .toList();
        Map<Long, PlatformType> typeMap = new HashMap<>();
        if (!typeIds.isEmpty()) {
            typeMap = platformTypeMapper.selectBatchIds(typeIds).stream()
                    .collect(Collectors.toMap(PlatformType::getId, t -> t));
        }

        List<PasswordExpirationVO.ExpirationDetail> expiredAccounts = new ArrayList<>();
        List<PasswordExpirationVO.ExpirationDetail> expiringAccounts = new ArrayList<>();
        int expiringIn30Days = 0;

        LocalDateTime now = LocalDateTime.now();

        for (Account account : accountsWithExpiration) {
            LocalDateTime passwordUpdateTime = account.getPasswordUpdateTime();
            // 如果没有设置密码更新时间，使用创建时间
            if (passwordUpdateTime == null) {
                passwordUpdateTime = account.getCreateTime();
            }
            if (passwordUpdateTime == null) {
                continue;
            }

            // 计算过期时间
            LocalDateTime expirationTime = passwordUpdateTime.plusDays(account.getPasswordValidDays());
            long daysUntil = ChronoUnit.DAYS.between(now, expirationTime);

            PasswordExpirationVO.ExpirationDetail detail = buildDetail(
                    account, platformMap, typeMap, passwordUpdateTime, expirationTime, (int) daysUntil);

            if (daysUntil < 0) {
                // 已过期
                expiredAccounts.add(detail);
            } else if (daysUntil <= days) {
                // 即将过期
                expiringAccounts.add(detail);
            }

            if (daysUntil <= 30) {
                expiringIn30Days++;
            }
        }

        // 构建统计信息
        PasswordExpirationVO.Statistics statistics = PasswordExpirationVO.Statistics.builder()
                .expiredCount(expiredAccounts.size())
                .expiringIn7DaysCount((int) expiringAccounts.stream()
                        .filter(d -> d.getDaysUntilExpiration() <= 7)
                        .count())
                .expiringIn30DaysCount(expiringIn30Days)
                .totalWithExpiration(accountsWithExpiration.size())
                .build();

        return PasswordExpirationVO.builder()
                .expiredAccounts(expiredAccounts)
                .expiringAccounts(expiringAccounts)
                .statistics(statistics)
                .build();
    }

    @Override
    @Transactional
    public void updatePasswordTime(Long accountId) {
        accountMapper.update(null, new LambdaUpdateWrapper<Account>()
                .eq(Account::getId, accountId)
                .set(Account::getPasswordUpdateTime, LocalDateTime.now()));
    }

    @Override
    @Transactional
    public void setPasswordValidDays(Long accountId, Integer validDays) {
        accountMapper.update(null, new LambdaUpdateWrapper<Account>()
                .eq(Account::getId, accountId)
                .set(Account::getPasswordValidDays, validDays));
    }

    /**
     * 构建空结果
     */
    private PasswordExpirationVO buildEmptyResult() {
        return PasswordExpirationVO.builder()
                .expiredAccounts(new ArrayList<>())
                .expiringAccounts(new ArrayList<>())
                .statistics(PasswordExpirationVO.Statistics.builder()
                        .expiredCount(0)
                        .expiringIn7DaysCount(0)
                        .expiringIn30DaysCount(0)
                        .totalWithExpiration(0)
                        .build())
                .build();
    }

    /**
     * 构建过期详情
     */
    private PasswordExpirationVO.ExpirationDetail buildDetail(
            Account account,
            Map<Long, Platform> platformMap,
            Map<Long, PlatformType> typeMap,
            LocalDateTime passwordUpdateTime,
            LocalDateTime expirationTime,
            int daysUntil) {

        String platformName = "";
        String typeName = "";

        Platform platform = platformMap.get(account.getPlatformId());
        if (platform != null) {
            platformName = platform.getPlatformName();
            PlatformType type = typeMap.get(platform.getPlatformTypeId());
            if (type != null) {
                typeName = type.getTypeName();
            }
        }

        return PasswordExpirationVO.ExpirationDetail.builder()
                .accountId(account.getId())
                .accountName(account.getAccountName())
                .platformName(platformName)
                .platformTypeName(typeName)
                .passwordUpdateTime(passwordUpdateTime)
                .passwordValidDays(account.getPasswordValidDays())
                .expirationTime(expirationTime)
                .daysUntilExpiration(daysUntil)
                .importanceLevel(account.getImportanceLevel())
                .build();
    }
}
