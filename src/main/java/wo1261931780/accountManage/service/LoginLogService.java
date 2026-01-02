package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wo1261931780.accountManage.entity.SysLoginLog;

/**
 * 登录日志服务接口
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public interface LoginLogService extends IService<SysLoginLog> {

    /**
     * 记录登录成功
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param ip       IP地址
     * @param message  消息
     */
    void recordLoginSuccess(Long userId, String username, String ip, String message);

    /**
     * 记录登录失败
     *
     * @param username 用户名
     * @param ip       IP地址
     * @param message  消息
     */
    void recordLoginFailure(String username, String ip, String message);

    /**
     * 记录登出
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param ip       IP地址
     */
    void recordLogout(Long userId, String username, String ip);
}
