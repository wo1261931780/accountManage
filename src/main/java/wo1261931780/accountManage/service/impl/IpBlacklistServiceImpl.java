package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import wo1261931780.accountManage.dto.ip.IpBlacklistAddDTO;
import wo1261931780.accountManage.dto.ip.IpBlacklistQueryDTO;
import wo1261931780.accountManage.dto.ip.IpBlacklistVO;
import wo1261931780.accountManage.entity.IpBlacklist;
import wo1261931780.accountManage.entity.LoginFailRecord;
import wo1261931780.accountManage.mapper.IpBlacklistMapper;
import wo1261931780.accountManage.mapper.LoginFailRecordMapper;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.IpBlacklistService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP黑名单服务实现
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IpBlacklistServiceImpl implements IpBlacklistService {

    private final IpBlacklistMapper ipBlacklistMapper;
    private final LoginFailRecordMapper loginFailRecordMapper;

    /**
     * 登录失败多少次后自动封禁
     */
    @Value("${security.ip-blacklist.fail-threshold:10}")
    private int failThreshold;

    /**
     * 统计失败次数的时间窗口（分钟）
     */
    @Value("${security.ip-blacklist.fail-window-minutes:30}")
    private int failWindowMinutes;

    /**
     * 自动封禁时长（分钟）
     */
    @Value("${security.ip-blacklist.auto-block-minutes:60}")
    private int autoBlockMinutes;

    /**
     * 是否启用IP黑名单
     */
    @Value("${security.ip-blacklist.enabled:true}")
    private boolean enabled;

    /**
     * 内存缓存封禁IP列表，提高检查性能
     */
    private final Set<String> blockedIpCache = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() {
        if (enabled) {
            refreshCache();
            log.info("IP黑名单服务已启动, 失败阈值: {}, 时间窗口: {}分钟, 自动封禁时长: {}分钟",
                    failThreshold, failWindowMinutes, autoBlockMinutes);
        } else {
            log.info("IP黑名单服务已禁用");
        }
    }

    @Override
    public boolean isBlocked(String ipAddress) {
        if (!enabled || ipAddress == null) {
            return false;
        }

        // 先检查缓存
        if (blockedIpCache.contains(ipAddress)) {
            return true;
        }

        // 缓存未命中，查询数据库
        boolean blocked = ipBlacklistMapper.isBlocked(ipAddress);
        if (blocked) {
            blockedIpCache.add(ipAddress);
        }
        return blocked;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToBlacklist(IpBlacklistAddDTO dto) {
        // 检查是否已存在
        IpBlacklist existing = ipBlacklistMapper.findActiveByIp(dto.getIpAddress());
        if (existing != null) {
            log.warn("IP {} 已在黑名单中", dto.getIpAddress());
            return false;
        }

        IpBlacklist entity = new IpBlacklist();
        entity.setIpAddress(dto.getIpAddress());
        entity.setReason(dto.getReason());
        entity.setSource(IpBlacklist.Source.MANUAL.name());
        entity.setIsPermanent(dto.getIsPermanent());
        entity.setStatus(true);
        entity.setRemark(dto.getRemark());
        entity.setCreatedBy(UserContext.getUserId());

        if (!dto.getIsPermanent() && dto.getDurationMinutes() != null) {
            entity.setExpireTime(LocalDateTime.now().plusMinutes(dto.getDurationMinutes()));
        }

        int result = ipBlacklistMapper.insert(entity);
        if (result > 0) {
            blockedIpCache.add(dto.getIpAddress());
            log.info("手动添加IP {} 到黑名单, 永久: {}, 时长: {}分钟",
                    dto.getIpAddress(), dto.getIsPermanent(), dto.getDurationMinutes());
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoBlock(String ipAddress, String reason, int durationMinutes) {
        // 检查是否已存在
        IpBlacklist existing = ipBlacklistMapper.findActiveByIp(ipAddress);
        if (existing != null) {
            // 更新失败次数
            ipBlacklistMapper.updateFailCount(ipAddress, existing.getFailCount() + 1);
            return true;
        }

        IpBlacklist entity = new IpBlacklist();
        entity.setIpAddress(ipAddress);
        entity.setReason(reason);
        entity.setSource(IpBlacklist.Source.AUTO.name());
        entity.setFailCount(1);
        entity.setIsPermanent(false);
        entity.setExpireTime(LocalDateTime.now().plusMinutes(durationMinutes));
        entity.setStatus(true);

        int result = ipBlacklistMapper.insert(entity);
        if (result > 0) {
            blockedIpCache.add(ipAddress);
            log.warn("自动封禁IP {}, 原因: {}, 时长: {}分钟", ipAddress, reason, durationMinutes);
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unblock(Long id) {
        IpBlacklist entity = ipBlacklistMapper.selectById(id);
        if (entity == null) {
            return false;
        }

        entity.setStatus(false);
        int result = ipBlacklistMapper.updateById(entity);
        if (result > 0) {
            blockedIpCache.remove(entity.getIpAddress());
            log.info("解封IP {}", entity.getIpAddress());
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unblockByIp(String ipAddress) {
        LambdaQueryWrapper<IpBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IpBlacklist::getIpAddress, ipAddress)
               .eq(IpBlacklist::getStatus, true);

        IpBlacklist entity = ipBlacklistMapper.selectOne(wrapper);
        if (entity == null) {
            return false;
        }

        entity.setStatus(false);
        int result = ipBlacklistMapper.updateById(entity);
        if (result > 0) {
            blockedIpCache.remove(ipAddress);
            // 同时清除登录失败记录
            loginFailRecordMapper.deleteByIp(ipAddress);
            log.info("解封IP {} (按地址)", ipAddress);
        }
        return result > 0;
    }

    @Override
    public IPage<IpBlacklistVO> page(IpBlacklistQueryDTO queryDTO) {
        LambdaQueryWrapper<IpBlacklist> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getIpAddress())) {
            wrapper.like(IpBlacklist::getIpAddress, queryDTO.getIpAddress());
        }
        if (StringUtils.hasText(queryDTO.getSource())) {
            wrapper.eq(IpBlacklist::getSource, queryDTO.getSource());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(IpBlacklist::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getIsPermanent() != null) {
            wrapper.eq(IpBlacklist::getIsPermanent, queryDTO.getIsPermanent());
        }

        wrapper.orderByDesc(IpBlacklist::getCreatedTime);

        Page<IpBlacklist> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<IpBlacklist> result = ipBlacklistMapper.selectPage(page, wrapper);

        return result.convert(this::toVO);
    }

    @Override
    public IpBlacklistVO getById(Long id) {
        IpBlacklist entity = ipBlacklistMapper.selectById(id);
        return entity != null ? toVO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        IpBlacklist entity = ipBlacklistMapper.selectById(id);
        if (entity != null) {
            blockedIpCache.remove(entity.getIpAddress());
        }
        return ipBlacklistMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        // 先获取要删除的IP，从缓存中移除
        List<IpBlacklist> entities = ipBlacklistMapper.selectBatchIds(ids);
        entities.forEach(e -> blockedIpCache.remove(e.getIpAddress()));

        return ipBlacklistMapper.deleteBatchIds(ids);
    }

    @Override
    public Set<String> getAllBlockedIps() {
        List<String> ips = ipBlacklistMapper.findAllBlockedIps();
        return new HashSet<>(ips);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordLoginFail(String ipAddress, String username) {
        if (!enabled || ipAddress == null) {
            return;
        }

        // 记录失败
        LoginFailRecord record = new LoginFailRecord();
        record.setIpAddress(ipAddress);
        record.setUsername(username);
        record.setFailTime(LocalDateTime.now());
        loginFailRecordMapper.insert(record);

        // 检查是否需要自动封禁
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(failWindowMinutes);
        int failCount = loginFailRecordMapper.countFailsByIpSince(ipAddress, windowStart);

        if (failCount >= failThreshold) {
            String reason = String.format("登录失败次数过多: %d次/%d分钟内", failCount, failWindowMinutes);
            autoBlock(ipAddress, reason, autoBlockMinutes);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearLoginFailRecords(String ipAddress) {
        if (ipAddress != null) {
            loginFailRecordMapper.deleteByIp(ipAddress);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpired() {
        int count = ipBlacklistMapper.unblockExpired();
        if (count > 0) {
            refreshCache();
            log.info("清理过期IP封禁记录: {}条", count);
        }
        return count;
    }

    @Override
    public void refreshCache() {
        blockedIpCache.clear();
        blockedIpCache.addAll(getAllBlockedIps());
        log.debug("刷新IP黑名单缓存, 当前封禁数量: {}", blockedIpCache.size());
    }

    /**
     * 定时清理过期封禁
     */
    @Scheduled(fixedRate = 300000) // 每5分钟
    public void scheduledCleanup() {
        if (enabled) {
            cleanExpired();
            // 清理30天前的登录失败记录
            loginFailRecordMapper.cleanOldRecords(LocalDateTime.now().minusDays(30));
        }
    }

    /**
     * 转换为VO
     */
    private IpBlacklistVO toVO(IpBlacklist entity) {
        IpBlacklistVO vo = new IpBlacklistVO();
        BeanUtils.copyProperties(entity, vo);

        // 来源描述
        vo.setSourceDesc(getSourceDesc(entity.getSource()));

        // 状态描述
        vo.setStatusDesc(entity.getStatus() ? "封禁中" : "已解封");

        // 计算剩余封禁时间
        if (entity.getStatus() && !entity.getIsPermanent() && entity.getExpireTime() != null) {
            Duration duration = Duration.between(LocalDateTime.now(), entity.getExpireTime());
            vo.setRemainingSeconds(Math.max(0, duration.getSeconds()));
        }

        return vo;
    }

    private String getSourceDesc(String source) {
        if (source == null) return "未知";
        return switch (source) {
            case "MANUAL" -> "手动添加";
            case "AUTO" -> "自动封禁";
            case "LOGIN_FAIL" -> "登录失败过多";
            default -> source;
        };
    }
}
