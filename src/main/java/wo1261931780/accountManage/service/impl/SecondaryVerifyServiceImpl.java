package wo1261931780.accountManage.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.common.utils.PasswordUtils;
import wo1261931780.accountManage.dto.auth.SecondaryVerifyDTO;
import wo1261931780.accountManage.dto.response.PasswordViewVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.PasswordHistory;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.entity.SysUser;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.PasswordHistoryMapper;
import wo1261931780.accountManage.mapper.PlatformMapper;
import wo1261931780.accountManage.mapper.SysUserMapper;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.OperationLogService;
import wo1261931780.accountManage.service.SecondaryVerifyService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 二次验证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecondaryVerifyServiceImpl implements SecondaryVerifyService {

    private final SysUserMapper sysUserMapper;
    private final AccountMapper accountMapper;
    private final PlatformMapper platformMapper;
    private final OperationLogService operationLogService;
    private final PasswordUtils passwordUtils;

    @Override
    public boolean verifyUserPassword(String password) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        return BCrypt.checkpw(password, user.getPassword());
    }

    @Override
    public PasswordViewVO viewPasswordWithVerification(SecondaryVerifyDTO dto) {
        // 1. 二次验证用户密码
        if (!verifyUserPassword(dto.getPassword())) {
            log.warn("二次验证失败, userId: {}, accountId: {}",
                    UserContext.getUserId(), dto.getAccountId());
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "验证密码错误");
        }

        // 2. 查询账号信息
        Account account = accountMapper.selectById(dto.getAccountId());
        if (account == null || account.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "账号不存在");
        }

        // 3. 解密密码
        String plainPassword;
        try {
            plainPassword = passwordUtils.decrypt(
                    account.getPasswordEncrypted(),
                    account.getPasswordSalt()
            );
        } catch (Exception e) {
            log.error("密码解密失败, accountId: {}", dto.getAccountId(), e);
            throw new BusinessException(ResultCode.PASSWORD_DECRYPT_ERROR);
        }

        // 4. 获取平台信息
        String platformName = "";
        if (account.getPlatformId() != null) {
            Platform platform = platformMapper.selectById(account.getPlatformId());
            platformName = platform != null ? platform.getPlatformName() : "";
        }

        // 5. 构建响应
        PasswordViewVO vo = new PasswordViewVO();
        vo.setAccountId(account.getId());
        vo.setAccountName(account.getAccountName());
        vo.setPlatformName(platformName);
        vo.setPassword(plainPassword);

        // 密码过期信息
        if (account.getPasswordUpdateTime() != null) {
            vo.setPasswordUpdateTime(account.getPasswordUpdateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            if (account.getPasswordValidDays() != null && account.getPasswordValidDays() > 0) {
                vo.setPasswordValidDays(account.getPasswordValidDays());
                LocalDateTime expireTime = account.getPasswordUpdateTime()
                        .plusDays(account.getPasswordValidDays());
                long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), expireTime);
                vo.setRemainingDays((int) Math.max(0, remainingDays));
                vo.setWillExpire(remainingDays <= 7);
            }
        }

        log.info("二次验证查看密码成功, userId: {}, accountId: {}, reason: {}",
                UserContext.getUserId(), dto.getAccountId(), dto.getReason());
        return vo;
    }
}
