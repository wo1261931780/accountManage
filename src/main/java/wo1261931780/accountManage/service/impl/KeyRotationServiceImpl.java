package wo1261931780.accountManage.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.dto.request.KeyRotationDTO;
import wo1261931780.accountManage.dto.response.KeyRotationResultVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.entity.SysUser;
import wo1261931780.accountManage.mapper.AccountMapper;
import wo1261931780.accountManage.mapper.SysUserMapper;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.KeyRotationService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 密钥轮转服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeyRotationServiceImpl implements KeyRotationService {

    private final AccountMapper accountMapper;
    private final SysUserMapper sysUserMapper;

    @Value("${account.security.aes-key}")
    private String currentAesKey;

    @Value("${backup.path:./backups}")
    private String backupBasePath;

    // 记录上次轮转时间（实际应该存储在数据库）
    private static LocalDateTime lastRotationTime;

    @Override
    @Transactional
    public KeyRotationResultVO rotateKey(KeyRotationDTO dto) {
        KeyRotationResultVO result = new KeyRotationResultVO();
        result.setStartTime(LocalDateTime.now());
        result.setFailedAccountIds(new ArrayList<>());

        // 1. 二次验证用户密码
        if (!verifyCurrentUserPassword(dto.getCurrentPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "当前密码验证失败");
        }

        // 2. 验证新密钥有效性
        if (!validateKey(dto.getNewAesKey())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密钥无效，长度必须为16-32位");
        }

        log.warn("开始密钥轮转操作, 操作用户: {}", UserContext.getUsername());

        // 3. 备份旧密钥
        if (Boolean.TRUE.equals(dto.getBackupOldKey())) {
            String backupPath = backupOldKey();
            result.setBackupFilePath(backupPath);
        }

        // 4. 获取所有账号
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getDeleted, 0);
        List<Account> accounts = accountMapper.selectList(wrapper);

        result.setTotalAccounts(accounts.size());
        int successCount = 0;
        int failedCount = 0;

        // 5. 逐个账号重新加密
        for (Account account : accounts) {
            try {
                // 用旧密钥解密
                String plainPassword = decryptWithKey(
                        account.getPasswordEncrypted(),
                        account.getPasswordSalt(),
                        currentAesKey
                );

                // 生成新盐值
                String newSalt = RandomUtil.randomString(16);

                // 用新密钥加密
                String newEncrypted = encryptWithKey(plainPassword, newSalt, dto.getNewAesKey());

                // 更新账号
                account.setPasswordEncrypted(newEncrypted);
                account.setPasswordSalt(newSalt);
                accountMapper.updateById(account);

                successCount++;
            } catch (Exception e) {
                log.error("账号密钥轮转失败, accountId: {}, error: {}", account.getId(), e.getMessage());
                failedCount++;
                result.getFailedAccountIds().add(account.getId());
            }
        }

        result.setSuccessCount(successCount);
        result.setFailedCount(failedCount);
        result.setSuccess(failedCount == 0);
        result.setEndTime(LocalDateTime.now());
        result.setDurationMs(java.time.Duration.between(result.getStartTime(), result.getEndTime()).toMillis());

        if (failedCount > 0) {
            result.setErrorMessage("部分账号轮转失败，请检查失败账号ID列表");
            log.error("密钥轮转完成, 成功: {}, 失败: {}", successCount, failedCount);
        } else {
            log.info("密钥轮转成功完成, 处理账号数: {}", successCount);
            lastRotationTime = LocalDateTime.now();
        }

        // 注意：新密钥需要手动更新到配置文件中！
        log.warn("重要提示: 请将新密钥更新到 application.yaml 的 account.security.aes-key 配置中");

        return result;
    }

    @Override
    public boolean validateKey(String newKey) {
        if (newKey == null) {
            return false;
        }
        int length = newKey.length();
        return length >= 16 && length <= 32;
    }

    @Override
    public String getCurrentKeyInfo() {
        if (currentAesKey == null || currentAesKey.length() < 4) {
            return "未配置";
        }
        // 只显示前2位和后2位
        return currentAesKey.substring(0, 2) + "****" +
               currentAesKey.substring(currentAesKey.length() - 2) +
               " (长度: " + currentAesKey.length() + ")";
    }

    @Override
    public LocalDateTime getLastRotationTime() {
        return lastRotationTime;
    }

    private boolean verifyCurrentUserPassword(String password) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        return BCrypt.checkpw(password, user.getPassword());
    }

    private String backupOldKey() {
        String backupDir = backupBasePath + File.separator + "keys";
        FileUtil.mkdir(backupDir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFile = backupDir + File.separator + "key_backup_" + timestamp + ".txt";

        // 注意：这里只是示例，实际生产环境应该用更安全的方式备份密钥
        String content = "# AES Key Backup\n" +
                "# Time: " + LocalDateTime.now() + "\n" +
                "# Key Length: " + currentAesKey.length() + "\n" +
                "# Key Hash (SHA256): " + SecureUtil.sha256(currentAesKey) + "\n" +
                "# WARNING: Do not share this file!\n";

        FileUtil.writeString(content, backupFile, StandardCharsets.UTF_8);
        log.info("密钥备份已创建: {}", backupFile);

        return backupFile;
    }

    private String decryptWithKey(String encryptedPassword, String salt, String aesKey) {
        String combinedKey = padKey(aesKey + salt);
        AES aes = SecureUtil.aes(combinedKey.getBytes(StandardCharsets.UTF_8));
        return aes.decryptStr(encryptedPassword);
    }

    private String encryptWithKey(String plainPassword, String salt, String aesKey) {
        String combinedKey = padKey(aesKey + salt);
        AES aes = SecureUtil.aes(combinedKey.getBytes(StandardCharsets.UTF_8));
        return aes.encryptHex(plainPassword);
    }

    private String padKey(String key) {
        if (key.length() >= 32) {
            return key.substring(0, 32);
        }
        StringBuilder sb = new StringBuilder(key);
        while (sb.length() < 32) {
            sb.append("0");
        }
        return sb.toString();
    }
}
