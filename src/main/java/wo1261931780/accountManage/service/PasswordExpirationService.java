package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.account.PasswordExpirationVO;

/**
 * 密码过期提醒服务接口
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface PasswordExpirationService {

    /**
     * 获取密码过期提醒
     *
     * @return 过期提醒结果
     */
    PasswordExpirationVO getExpirationReminder();

    /**
     * 获取指定天数内即将过期的账号
     *
     * @param days 天数
     * @return 过期提醒结果
     */
    PasswordExpirationVO getExpiringWithinDays(int days);

    /**
     * 更新账号密码最后修改时间
     *
     * @param accountId 账号ID
     */
    void updatePasswordTime(Long accountId);

    /**
     * 设置账号密码有效期
     *
     * @param accountId 账号ID
     * @param validDays 有效期天数，0表示永不过期
     */
    void setPasswordValidDays(Long accountId, Integer validDays);
}
