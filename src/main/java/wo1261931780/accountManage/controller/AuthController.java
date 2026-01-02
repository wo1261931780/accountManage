package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.annotation.RateLimit;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.auth.ChangePasswordDTO;
import wo1261931780.accountManage.dto.auth.LoginDTO;
import wo1261931780.accountManage.dto.auth.LoginVO;
import wo1261931780.accountManage.dto.auth.UserInfoVO;
import wo1261931780.accountManage.service.AuthService;
import wo1261931780.accountManage.util.WebUtils;

/**
 * 认证控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Tag(name = "认证管理", description = "用户登录、登出、令牌刷新等")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "使用用户名密码登录，获取访问令牌")
    @PostMapping("/login")
    @OperationLog(module = "认证管理", type = OperationLog.OperationType.LOGIN, description = "用户登录")
    @RateLimit(key = "login", limit = 5, window = 60, message = "登录尝试过于频繁，请1分钟后再试")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ip = WebUtils.getClientIp(request);
        LoginVO loginVO = authService.login(loginDTO, ip);
        return Result.success(loginVO);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "用户登出，清除登录状态")
    @PostMapping("/logout")
    @OperationLog(module = "认证管理", type = OperationLog.OperationType.LOGOUT, description = "用户登出")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 刷新令牌
     */
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    @RateLimit(key = "refresh", limit = 10, window = 60, message = "刷新令牌过于频繁，请稍后再试")
    public Result<LoginVO> refreshToken(@RequestParam String refreshToken) {
        LoginVO loginVO = authService.refreshToken(refreshToken);
        return Result.success(loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/me")
    public Result<UserInfoVO> getCurrentUser() {
        UserInfoVO userInfo = authService.getCurrentUser();
        return Result.success(userInfo);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "修改当前用户密码")
    @PutMapping("/password")
    @OperationLog(module = "认证管理", type = OperationLog.OperationType.UPDATE, description = "修改密码", saveParams = false)
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        authService.changePassword(changePasswordDTO);
        return Result.success();
    }
}
