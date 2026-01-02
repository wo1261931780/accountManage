package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import wo1261931780.accountManage.dto.response.RecentAccountVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.AccountAccessLog;
import wo1261931780.accountManage.entity.AccountFavorite;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.mapper.AccountAccessLogMapper;
import wo1261931780.accountManage.mapper.AccountFavoriteMapper;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.AccountAccessService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 账号访问记录服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountAccessServiceImpl implements AccountAccessService {

    private final AccountAccessLogMapper accessLogMapper;
    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;
    private final AccountFavoriteMapper accountFavoriteMapper;

    @Override
    @Async
    @Transactional
    public void recordAccess(Long accountId, Integer accessType) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return;
        }

        AccountAccessLog accessLog = new AccountAccessLog();
        accessLog.setUserId(userId);
        accessLog.setAccountId(accountId);
        accessLog.setAccessType(accessType != null ? accessType : AccountAccessLog.TYPE_VIEW);
        accessLog.setAccessTime(LocalDateTime.now());

        // 获取请求信息
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                accessLog.setIpAddress(getClientIp(request));
                accessLog.setUserAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            log.debug("获取请求信息失败", e);
        }

        accessLogMapper.insert(accessLog);
        log.debug("记录账号访问, userId: {}, accountId: {}, type: {}", userId, accountId, accessType);
    }

    @Override
    public List<RecentAccountVO> getRecentAccounts(int limit) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return new ArrayList<>();
        }

        // 查询最近访问记录
        List<Map<String, Object>> recentList = accessLogMapper.selectRecentAccountIds(userId, limit);
        if (recentList.isEmpty()) {
            return new ArrayList<>();
        }

        return buildRecentAccountVOs(recentList, userId);
    }

    @Override
    public List<RecentAccountVO> getMostAccessedAccounts(int limit) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> mostAccessedList = accessLogMapper.selectMostAccessedAccounts(userId, limit);
        if (mostAccessedList.isEmpty()) {
            return new ArrayList<>();
        }

        return buildRecentAccountVOs(mostAccessedList, userId);
    }

    @Override
    @Transactional
    public void clearAccessLog(Long accountId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return;
        }

        LambdaQueryWrapper<AccountAccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountAccessLog::getUserId, userId)
                .eq(AccountAccessLog::getAccountId, accountId);

        accessLogMapper.delete(wrapper);
        log.info("清除账号访问记录, userId: {}, accountId: {}", userId, accountId);
    }

    @Override
    @Transactional
    public void clearAllAccessLogs() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return;
        }

        LambdaQueryWrapper<AccountAccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountAccessLog::getUserId, userId);

        int deleted = accessLogMapper.delete(wrapper);
        log.info("清除用户所有访问记录, userId: {}, deleted: {}", userId, deleted);
    }

    @Override
    @Transactional
    public int clearOldAccessLogs(int days) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);

        LambdaQueryWrapper<AccountAccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(AccountAccessLog::getAccessTime, cutoffTime);

        int deleted = accessLogMapper.delete(wrapper);
        log.info("清除{}天前的访问记录, deleted: {}", days, deleted);
        return deleted;
    }

    private List<RecentAccountVO> buildRecentAccountVOs(List<Map<String, Object>> accessList, Long userId) {
        // 获取账号ID列表
        List<Long> accountIds = accessList.stream()
                .map(m -> ((Number) m.get("account_id")).longValue())
                .toList();

        // 批量查询账号
        Map<Long, Account> accountMap = accountMapper.selectBatchIds(accountIds).stream()
                .filter(a -> a.getDeleted() == 0)
                .collect(Collectors.toMap(Account::getId, a -> a));

        // 批量查询平台
        List<Long> platformIds = accountMap.values().stream()
                .map(Account::getPlatformId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        Map<Long, Platform> platformMap = platformMapper.selectBatchIds(platformIds).stream()
                .collect(Collectors.toMap(Platform::getId, p -> p));

        // 获取收藏状态
        Set<Long> favoriteIds = getFavoriteAccountIds(accountIds, userId);

        // 构建VO
        return accessList.stream()
                .map(m -> {
                    Long accountId = ((Number) m.get("account_id")).longValue();
                    Account account = accountMap.get(accountId);
                    if (account == null) {
                        return null;
                    }

                    RecentAccountVO vo = new RecentAccountVO();
                    vo.setAccountId(accountId);
                    vo.setAccountName(account.getAccountName());
                    vo.setAccountAlias(account.getAccountAlias());
                    vo.setAccountStatus(account.getAccountStatus());
                    vo.setAccountStatusName(getStatusName(account.getAccountStatus()));
                    vo.setIsFavorite(favoriteIds.contains(accountId));

                    // 访问信息
                    if (m.get("last_access_time") != null) {
                        vo.setLastAccessTime((LocalDateTime) m.get("last_access_time"));
                    }
                    if (m.get("access_count") != null) {
                        vo.setAccessCount(((Number) m.get("access_count")).intValue());
                    }

                    // 平台信息
                    if (account.getPlatformId() != null) {
                        Platform platform = platformMap.get(account.getPlatformId());
                        if (platform != null) {
                            vo.setPlatformName(platform.getPlatformName());
                            vo.setPlatformIcon(platform.getPlatformIcon());
                        }
                    }

                    return vo;
                })
                .filter(vo -> vo != null)
                .toList();
    }

    private Set<Long> getFavoriteAccountIds(List<Long> accountIds, Long userId) {
        if (accountIds.isEmpty()) {
            return Set.of();
        }

        LambdaQueryWrapper<AccountFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountFavorite::getUserId, userId)
                .in(AccountFavorite::getAccountId, accountIds);

        return accountFavoriteMapper.selectList(wrapper).stream()
                .map(AccountFavorite::getAccountId)
                .collect(Collectors.toSet());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "已注销";
            case 1 -> "正常";
            case 2 -> "已冻结";
            case 3 -> "待验证";
            default -> "未知";
        };
    }
}
