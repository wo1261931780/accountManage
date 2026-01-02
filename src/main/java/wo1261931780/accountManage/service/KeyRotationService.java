package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.request.KeyRotationDTO;
import wo1261931780.accountManage.dto.response.KeyRotationResultVO;

/**
 * 密钥轮转服务
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface KeyRotationService {

    /**
     * 执行密钥轮转
     * 将所有账号的密码用新密钥重新加密
     *
     * @param dto 密钥轮转请求
     * @return 轮转结果
     */
    KeyRotationResultVO rotateKey(KeyRotationDTO dto);

    /**
     * 验证新密钥有效性
     *
     * @param newKey 新密钥
     * @return 是否有效
     */
    boolean validateKey(String newKey);

    /**
     * 获取当前密钥信息（脱敏）
     *
     * @return 密钥信息
     */
    String getCurrentKeyInfo();

    /**
     * 获取上次轮转时间
     *
     * @return 上次轮转时间
     */
    java.time.LocalDateTime getLastRotationTime();
}
