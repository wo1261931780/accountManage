package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.response.FavoriteAccountVO;
import wo1261931780.accountManage.service.AccountFavoriteService;

import java.util.List;

/**
 * 账号收藏控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "账号收藏", description = "账号收藏管理接口")
public class AccountFavoriteController {

    private final AccountFavoriteService accountFavoriteService;

    /**
     * 获取收藏列表
     */
    @GetMapping
    @Operation(summary = "获取收藏列表", description = "获取当前用户的所有收藏账号")
    public Result<List<FavoriteAccountVO>> getFavorites() {
        return Result.success(accountFavoriteService.getFavorites());
    }

    /**
     * 添加收藏
     */
    @PostMapping("/{accountId}")
    @Operation(summary = "添加收藏", description = "将账号添加到收藏")
    @OperationLog(module = "账号收藏", type = OperationLog.OperationType.CREATE, description = "添加收藏")
    public Result<Boolean> addFavorite(
            @Parameter(description = "账号ID") @PathVariable Long accountId) {
        return Result.success(accountFavoriteService.addFavorite(accountId));
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{accountId}")
    @Operation(summary = "取消收藏", description = "从收藏中移除账号")
    @OperationLog(module = "账号收藏", type = OperationLog.OperationType.DELETE, description = "取消收藏")
    public Result<Boolean> removeFavorite(
            @Parameter(description = "账号ID") @PathVariable Long accountId) {
        return Result.success(accountFavoriteService.removeFavorite(accountId));
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{accountId}")
    @Operation(summary = "检查是否已收藏", description = "检查账号是否已被收藏")
    public Result<Boolean> isFavorite(
            @Parameter(description = "账号ID") @PathVariable Long accountId) {
        return Result.success(accountFavoriteService.isFavorite(accountId));
    }

    /**
     * 批量检查收藏状态
     */
    @PostMapping("/check-batch")
    @Operation(summary = "批量检查收藏状态", description = "批量检查账号是否已被收藏")
    public Result<List<Long>> checkBatch(@RequestBody List<Long> accountIds) {
        return Result.success(accountFavoriteService.getFavoriteAccountIds(accountIds));
    }

    /**
     * 置顶收藏
     */
    @PostMapping("/{accountId}/pin")
    @Operation(summary = "置顶收藏", description = "将收藏的账号置顶")
    public Result<Boolean> pinToTop(
            @Parameter(description = "账号ID") @PathVariable Long accountId) {
        return Result.success(accountFavoriteService.pinToTop(accountId));
    }

    /**
     * 更新收藏排序
     */
    @PutMapping("/sort")
    @Operation(summary = "更新收藏排序", description = "批量更新收藏的排序")
    public Result<Boolean> updateSortOrder(@RequestBody List<Long> accountIds) {
        return Result.success(accountFavoriteService.batchUpdateSortOrder(accountIds));
    }
}
