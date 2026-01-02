package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import wo1261931780.accountManage.dto.backup.PasswordHistoryVO;

import java.util.List;

/**
 * 密码历史服务接口
 */
public interface PasswordHistoryService {

    /**
     * 记录密码变更历史
     * @param accountId 账号ID
     * @param encryptedPassword 加密后的密码
     * @param salt 盐值
     * @param changeType 变更类型
     * @param changeReason 变更原因
     * @param operatorId 操作人ID
     */
    void recordPasswordChange(Long accountId, String encryptedPassword, String salt,
                              Integer changeType, String changeReason, Long operatorId);

    /**
     * 查询账号的密码历史
     * @param accountId 账号ID
     * @param page 分页参数
     * @return 分页结果
     */
    Page<PasswordHistoryVO> getPasswordHistory(Long accountId, Page<PasswordHistoryVO> page);

    /**
     * 查询账号最近N次密码历史
     * @param accountId 账号ID
     * @param limit 数量限制
     * @return 历史列表
     */
    List<PasswordHistoryVO> getRecentHistory(Long accountId, int limit);

    /**
     * 检查密码是否与历史密码重复
     * @param accountId 账号ID
     * @param newPassword 新密码明文
     * @param checkCount 检查最近N次
     * @return 是否重复
     */
    boolean isPasswordReused(Long accountId, String newPassword, int checkCount);

    /**
     * 删除账号的所有密码历史（账号删除时调用）
     * @param accountId 账号ID
     */
    void deleteByAccountId(Long accountId);
}
