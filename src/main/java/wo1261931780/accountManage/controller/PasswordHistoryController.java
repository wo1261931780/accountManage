package wo1261931780.accountManage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.backup.PasswordHistoryVO;
import wo1261931780.accountManage.service.PasswordHistoryService;

import java.util.List;

/**
 * 密码历史控制器
 */
@Tag(name = "密码历史管理", description = "密码修改历史相关接口")
@RestController
@RequestMapping("/api/v1/password-history")
@RequiredArgsConstructor
public class PasswordHistoryController {

    private final PasswordHistoryService passwordHistoryService;

    @Operation(summary = "分页查询密码历史", description = "查询指定账号的密码修改历史")
    @GetMapping("/account/{accountId}")
    public Result<Page<PasswordHistoryVO>> getPasswordHistory(
            @Parameter(description = "账号ID") @PathVariable Long accountId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Page<PasswordHistoryVO> pageParam = new Page<>(page, size);
        return Result.success(passwordHistoryService.getPasswordHistory(accountId, pageParam));
    }

    @Operation(summary = "获取最近密码历史", description = "获取指定账号最近N次密码修改记录")
    @GetMapping("/account/{accountId}/recent")
    public Result<List<PasswordHistoryVO>> getRecentHistory(
            @Parameter(description = "账号ID") @PathVariable Long accountId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(passwordHistoryService.getRecentHistory(accountId, limit));
    }

    @Operation(summary = "检查密码是否重复", description = "检查新密码是否与最近N次密码重复")
    @PostMapping("/account/{accountId}/check-reuse")
    public Result<Boolean> checkPasswordReuse(
            @Parameter(description = "账号ID") @PathVariable Long accountId,
            @Parameter(description = "新密码") @RequestParam String newPassword,
            @Parameter(description = "检查最近N次") @RequestParam(defaultValue = "5") Integer checkCount) {
        boolean isReused = passwordHistoryService.isPasswordReused(accountId, newPassword, checkCount);
        return Result.success(isReused);
    }
}
