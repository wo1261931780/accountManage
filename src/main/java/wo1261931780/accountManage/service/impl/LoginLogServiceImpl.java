package wo1261931780.accountManage.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import wo1261931780.accountManage.entity.SysLoginLog;
import wo1261931780.accountManage.mapper.SysLoginLogMapper;
import wo1261931780.accountManage.service.LoginLogService;
import wo1261931780.accountManage.util.WebUtils;

/**
 * 登录日志服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
public class LoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements LoginLogService {

    @Override
    @Async
    public void recordLoginSuccess(Long userId, String username, String ip, String message) {
        recordLog(userId, username, "LOGIN", ip, 1, message);
    }

    @Override
    @Async
    public void recordLoginFailure(String username, String ip, String message) {
        recordLog(null, username, "LOGIN", ip, 0, message);
    }

    @Override
    @Async
    public void recordLogout(Long userId, String username, String ip) {
        if (ip == null) {
            ip = WebUtils.getClientIp();
        }
        recordLog(userId, username, "LOGOUT", ip, 1, "登出成功");
    }

    /**
     * 记录登录日志
     */
    private void recordLog(Long userId, String username, String loginType, String ip, Integer status, String message) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginType(loginType);
            loginLog.setLoginIp(ip);
            loginLog.setStatus(status);
            loginLog.setMessage(message);

            // 解析User-Agent
            String userAgentStr = WebUtils.getUserAgent();
            if (StrUtil.isNotBlank(userAgentStr)) {
                UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
                if (userAgent != null) {
                    loginLog.setBrowser(userAgent.getBrowser().getName());
                    loginLog.setOs(userAgent.getOs().getName());
                }
            }

            save(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }
}
