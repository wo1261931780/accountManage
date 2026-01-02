package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.response.RecentAccountVO;
import wo1261931780.accountManage.service.AccountAccessService;

import java.util.List;

/**
 * 账号访问记录控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/recent")
@RequiredArgsConstructor
@Tag(name = "最近使用", description = "账号访问记录和最近使用接口")
public class RecentAccountController {

    private final AccountAccessService accountAccessService;

    /**
     * 获取最近访问的账号
     */
    @GetMapping
    @Operation(summary = "获取最近访问", description = "获取最近访问过的账号列表")
    public Result<List<RecentAccountVO>> getRecentAccounts(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(accountAccessService.getRecentAccounts(limit));
    }

    /**
     * 获取常用账号
     */
    @GetMapping("/frequent")
    @Operation(summary = "获取常用账号", description = "获取访问次数最多的账号")
    public Result<List<RecentAccountVO>> getMostAccessedAccounts(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(accountAccessService.getMostAccessedAccounts(limit));
    }

    /**
     * 清除单个账号的访问记录
     */
    @DeleteMapping("/{accountId}")
    @Operation(summary = "清除账号访问记录", description = "清除指定账号的访问记录")
    public Result<Boolean> clearAccessLog(
            @Parameter(description = "账号ID") @PathVariable Long accountId) {
        accountAccessService.clearAccessLog(accountId);
        return Result.success(true);
    }

    /**
     * 清除所有访问记录
     */
    @DeleteMapping("/all")
    @Operation(summary = "清除所有访问记录", description = "清除当前用户的所有访问记录")
    public Result<Boolean> clearAllAccessLogs() {
        accountAccessService.clearAllAccessLogs();
        return Result.success(true);
    }
}
