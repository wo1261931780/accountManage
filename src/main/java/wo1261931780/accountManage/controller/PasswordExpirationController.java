package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.account.PasswordExpirationVO;
import wo1261931780.accountManage.service.PasswordExpirationService;

/**
 * 密码过期提醒控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Tag(name = "密码过期提醒", description = "密码过期检测与提醒")
@RestController
@RequestMapping("/api/v1/password/expiration")
@RequiredArgsConstructor
public class PasswordExpirationController {

    private final PasswordExpirationService passwordExpirationService;

    /**
     * 获取密码过期提醒
     */
    @Operation(summary = "获取密码过期提醒", description = "获取已过期和即将过期(7天内)的账号列表")
    @GetMapping
    public Result<PasswordExpirationVO> getReminder() {
        PasswordExpirationVO result = passwordExpirationService.getExpirationReminder();
        return Result.success(result);
    }

    /**
     * 获取指定天数内即将过期的账号
     */
    @Operation(summary = "获取即将过期账号", description = "获取指定天数内即将过期的账号列表")
    @GetMapping("/within/{days}")
    public Result<PasswordExpirationVO> getExpiringWithinDays(
            @Parameter(description = "天数") @PathVariable Integer days) {
        PasswordExpirationVO result = passwordExpirationService.getExpiringWithinDays(days);
        return Result.success(result);
    }

    /**
     * 更新账号密码修改时间
     */
    @Operation(summary = "更新密码修改时间", description = "将账号的密码修改时间更新为当前时间")
    @PutMapping("/{accountId}/refresh")
    public Result<Void> refreshPasswordTime(
            @Parameter(description = "账号ID") @PathVariable Long accountId) {
        passwordExpirationService.updatePasswordTime(accountId);
        return Result.success();
    }

    /**
     * 设置账号密码有效期
     */
    @Operation(summary = "设置密码有效期", description = "设置账号的密码有效期，0表示永不过期")
    @PutMapping("/{accountId}/valid-days")
    public Result<Void> setValidDays(
            @Parameter(description = "账号ID") @PathVariable Long accountId,
            @Parameter(description = "有效期天数") @RequestParam Integer days) {
        passwordExpirationService.setPasswordValidDays(accountId, days);
        return Result.success();
    }
}
