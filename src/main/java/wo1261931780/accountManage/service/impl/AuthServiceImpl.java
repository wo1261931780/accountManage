package wo1261931780.accountManage.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.ResultCode;
import wo1261931780.accountManage.dto.auth.ChangePasswordDTO;
import wo1261931780.accountManage.dto.auth.LoginDTO;
import wo1261931780.accountManage.dto.auth.LoginVO;
import wo1261931780.accountManage.dto.auth.UserInfoVO;
import wo1261931780.accountManage.entity.SysUser;
import wo1261931780.accountManage.mapper.SysUserMapper;
import wo1261931780.accountManage.security.LoginUser;
import wo1261931780.accountManage.security.UserContext;
import wo1261931780.accountManage.service.AuthService;
import wo1261931780.accountManage.service.LoginLogService;
import wo1261931780.accountManage.util.JwtUtils;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements AuthService, CommandLineRunner {

    private final JwtUtils jwtUtils;
    private final LoginLogService loginLogService;

    @Override
    @Transactional
    public LoginVO login(LoginDTO loginDTO, String ip) {
        // 查询用户
        SysUser user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            loginLogService.recordLoginFailure(loginDTO.getUsername(), ip, "用户不存在");
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            loginLogService.recordLoginFailure(loginDTO.getUsername(), ip, "密码错误");
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            loginLogService.recordLoginFailure(loginDTO.getUsername(), ip, "账号已禁用");
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成令牌
        String accessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        updateById(user);

        // 记录登录日志
        loginLogService.recordLoginSuccess(user.getId(), user.getUsername(), ip, "登录成功");

        return LoginVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getAccessTokenExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    public void logout() {
        LoginUser loginUser = UserContext.getUser();
        if (loginUser != null) {
            loginLogService.recordLogout(loginUser.getUserId(), loginUser.getUsername(), null);
        }
        // 清除上下文
        UserContext.clear();
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌
            if (!jwtUtils.validateToken(refreshToken)) {
                throw new BusinessException(ResultCode.TOKEN_INVALID);
            }

            // 验证是否为刷新令牌
            if (!jwtUtils.isRefreshToken(refreshToken)) {
                throw new BusinessException(ResultCode.TOKEN_INVALID);
            }

            // 获取用户信息
            Long userId = jwtUtils.getUserId(refreshToken);
            String username = jwtUtils.getUsername(refreshToken);

            // 查询用户
            SysUser user = getById(userId);
            if (user == null || user.getStatus() != 1) {
                throw new BusinessException(ResultCode.USER_DISABLED);
            }

            // 生成新令牌
            String newAccessToken = jwtUtils.generateAccessToken(userId, username);
            String newRefreshToken = jwtUtils.generateRefreshToken(userId, username);

            return LoginVO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtils.getAccessTokenExpirationSeconds())
                    .userId(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .build();
        } catch (JwtException e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    @Override
    public UserInfoVO getCurrentUser() {
        LoginUser loginUser = UserContext.getUser();
        if (loginUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        SysUser user = getById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        return UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 验证确认密码
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 获取当前用户
        LoginUser loginUser = UserContext.getUser();
        if (loginUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        SysUser user = getById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!BCrypt.checkpw(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(BCrypt.hashpw(changePasswordDTO.getNewPassword()));
        updateById(user);
    }

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    @Override
    @Transactional
    public void initDefaultAdmin() {
        // 检查是否已存在admin用户
        SysUser admin = getByUsername("admin");
        if (admin == null) {
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123")); // 默认密码
            admin.setNickname("系统管理员");
            admin.setEmail("admin@example.com");
            admin.setStatus(1);
            save(admin);
            log.info("已创建默认管理员账号: admin / admin123");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // 应用启动时初始化默认管理员
        initDefaultAdmin();
    }
}
