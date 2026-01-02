package wo1261931780.accountManage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.utils.PasswordUtils;
import wo1261931780.accountManage.dto.backup.PasswordHistoryVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.PasswordHistory;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.entity.SysUser;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PasswordHistoryMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.mapper.SysUserMapper;
import wo1261931780.accountManage.service.PasswordHistoryService;

import java.util.List;

/**
 * 密码历史服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

    private final PasswordHistoryMapper passwordHistoryMapper;
    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;
    private final SysUserMapper sysUserMapper;
    private final PasswordUtils passwordUtils;

    @Override
    @Async
    @Transactional
    public void recordPasswordChange(Long accountId, String encryptedPassword, String salt,
                                     Integer changeType, String changeReason, Long operatorId) {
        PasswordHistory history = new PasswordHistory();
        history.setAccountId(accountId);
        history.setPasswordEncrypted(encryptedPassword);
        history.setPasswordSalt(salt);
        history.setChangeType(changeType);
        history.setChangeReason(changeReason);
        history.setCreateBy(operatorId);

        passwordHistoryMapper.insert(history);
        log.debug("记录密码变更历史, accountId: {}, changeType: {}", accountId, changeType);
    }

    @Override
    public Page<PasswordHistoryVO> getPasswordHistory(Long accountId, Page<PasswordHistoryVO> page) {
        LambdaQueryWrapper<PasswordHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordHistory::getAccountId, accountId)
                .orderByDesc(PasswordHistory::getCreateTime);

        Page<PasswordHistory> historyPage = new Page<>(page.getCurrent(), page.getSize());
        Page<PasswordHistory> result = passwordHistoryMapper.selectPage(historyPage, wrapper);

        // 获取账号信息
        Account account = accountMapper.selectById(accountId);
        String accountName = account != null ? account.getAccountName() : "";
        String platformName = "";
        if (account != null && account.getPlatformId() != null) {
            Platform platform = platformMapper.selectById(account.getPlatformId());
            platformName = platform != null ? platform.getPlatformName() : "";
        }

        String finalPlatformName = platformName;
        Page<PasswordHistoryVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(h -> convertToVO(h, accountName, finalPlatformName))
                .toList());
        return voPage;
    }

    @Override
    public List<PasswordHistoryVO> getRecentHistory(Long accountId, int limit) {
        LambdaQueryWrapper<PasswordHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordHistory::getAccountId, accountId)
                .orderByDesc(PasswordHistory::getCreateTime)
                .last("LIMIT " + limit);

        List<PasswordHistory> histories = passwordHistoryMapper.selectList(wrapper);

        // 获取账号信息
        Account account = accountMapper.selectById(accountId);
        String accountName = account != null ? account.getAccountName() : "";
        String platformName = "";
        if (account != null && account.getPlatformId() != null) {
            Platform platform = platformMapper.selectById(account.getPlatformId());
            platformName = platform != null ? platform.getPlatformName() : "";
        }

        String finalPlatformName = platformName;
        return histories.stream()
                .map(h -> convertToVO(h, accountName, finalPlatformName))
                .toList();
    }

    @Override
    public boolean isPasswordReused(Long accountId, String newPassword, int checkCount) {
        List<PasswordHistory> histories = getRecentHistoryEntities(accountId, checkCount);

        for (PasswordHistory history : histories) {
            try {
                // 使用相同的盐值加密新密码，比较是否相同
                String encrypted = passwordUtils.encrypt(newPassword, history.getPasswordSalt());
                if (encrypted.equals(history.getPasswordEncrypted())) {
                    return true; // 密码重复
                }
            } catch (Exception e) {
                log.warn("密码比较失败", e);
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteByAccountId(Long accountId) {
        LambdaQueryWrapper<PasswordHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordHistory::getAccountId, accountId);
        passwordHistoryMapper.delete(wrapper);
        log.info("删除账号的密码历史, accountId: {}", accountId);
    }

    private List<PasswordHistory> getRecentHistoryEntities(Long accountId, int limit) {
        LambdaQueryWrapper<PasswordHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordHistory::getAccountId, accountId)
                .orderByDesc(PasswordHistory::getCreateTime)
                .last("LIMIT " + limit);
        return passwordHistoryMapper.selectList(wrapper);
    }

    private PasswordHistoryVO convertToVO(PasswordHistory history, String accountName, String platformName) {
        PasswordHistoryVO vo = new PasswordHistoryVO();
        vo.setId(history.getId());
        vo.setAccountId(history.getAccountId());
        vo.setAccountName(accountName);
        vo.setPlatformName(platformName);
        vo.setChangeReason(history.getChangeReason());
        vo.setChangeType(history.getChangeType());
        vo.setChangeTypeName(PasswordHistoryVO.getChangeTypeName(history.getChangeType()));
        vo.setCreateTime(history.getCreateTime());
        vo.setCreateBy(history.getCreateBy());

        // 获取操作人名称
        if (history.getCreateBy() != null) {
            SysUser user = sysUserMapper.selectById(history.getCreateBy());
            vo.setCreateByName(user != null ? user.getNickname() : null);
        }

        return vo;
    }
}
