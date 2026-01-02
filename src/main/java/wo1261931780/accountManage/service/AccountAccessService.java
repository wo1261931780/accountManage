package wo1261931780.accountManage.service;

import wo1261931780.accountManage.dto.response.RecentAccountVO;

import java.util.List;

/**
 * 账号访问记录服务
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface AccountAccessService {

    /**
     * 记录账号访问
     *
     * @param accountId  账号ID
     * @param accessType 访问类型: 1-查看, 2-复制密码, 3-编辑
     */
    void recordAccess(Long accountId, Integer accessType);

    /**
     * 获取最近访问的账号
     *
     * @param limit 返回数量
     * @return 最近访问账号列表
     */
    List<RecentAccountVO> getRecentAccounts(int limit);

    /**
     * 获取最常访问的账号
     *
     * @param limit 返回数量
     * @return 常用账号列表
     */
    List<RecentAccountVO> getMostAccessedAccounts(int limit);

    /**
     * 清除账号的访问记录
     *
     * @param accountId 账号ID
     */
    void clearAccessLog(Long accountId);

    /**
     * 清除用户的所有访问记录
     */
    void clearAllAccessLogs();

    /**
     * 清除指定天数之前的访问记录
     *
     * @param days 天数
     * @return 清除的记录数
     */
    int clearOldAccessLogs(int days);
}
