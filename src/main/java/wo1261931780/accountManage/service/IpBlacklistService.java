package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import wo1261931780.accountManage.dto.ip.IpBlacklistAddDTO;
import wo1261931780.accountManage.dto.ip.IpBlacklistQueryDTO;
import wo1261931780.accountManage.dto.ip.IpBlacklistVO;
import wo1261931780.accountManage.entity.IpBlacklist;

import java.util.List;
import java.util.Set;

/**
 * IP黑名单服务接口
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
public interface IpBlacklistService {

    /**
     * 检查IP是否被封禁
     *
     * @param ipAddress IP地址
     * @return 是否封禁
     */
    boolean isBlocked(String ipAddress);

    /**
     * 手动添加IP到黑名单
     *
     * @param dto 添加请求
     * @return 是否成功
     */
    boolean addToBlacklist(IpBlacklistAddDTO dto);

    /**
     * 自动封禁IP（登录失败过多）
     *
     * @param ipAddress IP地址
     * @param reason 原因
     * @param durationMinutes 封禁时长（分钟）
     * @return 是否成功
     */
    boolean autoBlock(String ipAddress, String reason, int durationMinutes);

    /**
     * 解封IP
     *
     * @param id 黑名单记录ID
     * @return 是否成功
     */
    boolean unblock(Long id);

    /**
     * 根据IP地址解封
     *
     * @param ipAddress IP地址
     * @return 是否成功
     */
    boolean unblockByIp(String ipAddress);

    /**
     * 分页查询黑名单
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<IpBlacklistVO> page(IpBlacklistQueryDTO queryDTO);

    /**
     * 获取黑名单详情
     *
     * @param id ID
     * @return 黑名单信息
     */
    IpBlacklistVO getById(Long id);

    /**
     * 删除黑名单记录
     *
     * @param id ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 批量删除
     *
     * @param ids ID列表
     * @return 删除数量
     */
    int batchDelete(List<Long> ids);

    /**
     * 获取所有封禁中的IP列表（用于缓存）
     *
     * @return IP集合
     */
    Set<String> getAllBlockedIps();

    /**
     * 记录登录失败
     *
     * @param ipAddress IP地址
     * @param username 尝试的用户名
     */
    void recordLoginFail(String ipAddress, String username);

    /**
     * 清除登录失败记录（登录成功后调用）
     *
     * @param ipAddress IP地址
     */
    void clearLoginFailRecords(String ipAddress);

    /**
     * 清理过期的封禁记录
     *
     * @return 清理数量
     */
    int cleanExpired();

    /**
     * 刷新IP黑名单缓存
     */
    void refreshCache();
}
