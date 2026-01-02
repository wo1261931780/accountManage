package wo1261931780.accountManage.common.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;

import java.nio.charset.StandardCharsets;

/**
 * 密码加解密工具类
 * 使用 AES 对称加密，每个账号有独立的盐值
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Slf4j
@Component
public class PasswordUtils {

    /**
     * AES 密钥（必须是 16/24/32 字节）
     */
    @Value("${account.security.aes-key}")
    private String aesKey;

    /**
     * 盐值长度
     */
    private static final int SALT_LENGTH = 16;

    /**
     * 生成随机盐值
     *
     * @return 盐值
     */
    public String generateSalt() {
        return RandomUtil.randomString(SALT_LENGTH);
    }

    /**
     * 加密密码
     *
     * @param plainPassword 明文密码
     * @param salt          盐值
     * @return 加密后的密码
     */
    public String encrypt(String plainPassword, String salt) {
        try {
            // 将密钥和盐值组合，确保每个账号的加密结果不同
            String combinedKey = padKey(aesKey + salt);
            AES aes = SecureUtil.aes(combinedKey.getBytes(StandardCharsets.UTF_8));
            return aes.encryptHex(plainPassword);
        } catch (Exception e) {
            log.error("密码加密失败", e);
            throw new BusinessException(ResultCode.PASSWORD_ENCRYPT_ERROR);
        }
    }

    /**
     * 解密密码
     *
     * @param encryptedPassword 加密后的密码
     * @param salt              盐值
     * @return 明文密码
     */
    public String decrypt(String encryptedPassword, String salt) {
        try {
            // 将密钥和盐值组合
            String combinedKey = padKey(aesKey + salt);
            AES aes = SecureUtil.aes(combinedKey.getBytes(StandardCharsets.UTF_8));
            return aes.decryptStr(encryptedPassword);
        } catch (Exception e) {
            log.error("密码解密失败", e);
            throw new BusinessException(ResultCode.PASSWORD_DECRYPT_ERROR);
        }
    }

    /**
     * 将密钥填充/截取到 32 字节（AES-256）
     *
     * @param key 原始密钥
     * @return 32字节密钥
     */
    private String padKey(String key) {
        if (key.length() >= 32) {
            return key.substring(0, 32);
        }
        // 如果不足 32 字节，用 0 填充
        StringBuilder sb = new StringBuilder(key);
        while (sb.length() < 32) {
            sb.append("0");
        }
        return sb.toString();
    }
}
