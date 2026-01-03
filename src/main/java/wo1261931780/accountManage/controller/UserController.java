package wo1261931780.accountManage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.user.*;
import wo1261931780.accountManage.service.AuthService;
import wo1261931780.accountManage.service.UserService;

/**
 * 用户管理控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户列表")
    @GetMapping
    public Result<IPage<UserVO>> pageUsers(UserQueryDTO queryDTO) {
        return Result.success(userService.pageUsers(queryDTO));
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 创建用户
     */
    @Operation(summary = "创建用户", description = "创建新用户")
    @PostMapping
    public Result<Long> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        Long userId = userService.createUser(createDTO);
        return Result.success(userId);
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(id, updateDTO);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "删除指定用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态", description = "启用或禁用指定用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态: 0-禁用, 1-启用") @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    /**
     * 重置用户密码
     */
    @Operation(summary = "重置用户密码", description = "重置指定用户的密码")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(id, resetPasswordDTO);
        return Result.success();
    }

    /**
     * 检查用户名是否存在
     */
    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已被使用")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(
            @Parameter(description = "用户名") @RequestParam String username) {
        return Result.success(userService.existsByUsername(username));
    }

    /**
     * 解锁用户账号
     */
    @Operation(summary = "解锁用户账号", description = "解锁因登录失败被锁定的用户账号")
    @PostMapping("/{id}/unlock")
    public Result<Void> unlockUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        authService.unlockUser(id);
        return Result.success();
    }
}
