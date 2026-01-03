package wo1261931780.accountManage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.ip.IpBlacklistAddDTO;
import wo1261931780.accountManage.dto.ip.IpBlacklistQueryDTO;
import wo1261931780.accountManage.dto.ip.IpBlacklistVO;
import wo1261931780.accountManage.service.IpBlacklistService;

import java.util.List;
import java.util.Map;

/**
 * IP黑名单管理控制器
 *
 * @author wo1261931780
 * @since 2026-01-03
 */
@Tag(name = "IP黑名单管理", description = "管理封禁IP地址")
@RestController
@RequestMapping("/api/v1/ip-blacklist")
@RequiredArgsConstructor
public class IpBlacklistController {

    private final IpBlacklistService ipBlacklistService;

    @Operation(summary = "检查IP是否被封禁")
    @GetMapping("/check")
    public Result<Map<String, Object>> checkIp(
            @Parameter(description = "IP地址") @RequestParam String ip) {
        boolean blocked = ipBlacklistService.isBlocked(ip);
        return Result.success(Map.of(
                "ip", ip,
                "blocked", blocked
        ));
    }

    @Operation(summary = "添加IP到黑名单")
    @PostMapping
    @OperationLog(module = "IP黑名单", type = OperationLog.OperationType.CREATE, description = "手动添加IP到黑名单")
    public Result<Void> add(@Valid @RequestBody IpBlacklistAddDTO dto) {
        boolean success = ipBlacklistService.addToBlacklist(dto);
        return success ? Result.success() : Result.fail("添加失败，IP可能已在黑名单中");
    }

    @Operation(summary = "解封IP")
    @PostMapping("/{id}/unblock")
    @OperationLog(module = "IP黑名单", type = OperationLog.OperationType.UPDATE, description = "解封指定IP")
    public Result<Void> unblock(
            @Parameter(description = "黑名单记录ID") @PathVariable Long id) {
        boolean success = ipBlacklistService.unblock(id);
        return success ? Result.success() : Result.fail("解封失败，记录不存在");
    }

    @Operation(summary = "根据IP地址解封")
    @PostMapping("/unblock-by-ip")
    @OperationLog(module = "IP黑名单", type = OperationLog.OperationType.UPDATE, description = "根据IP地址解封")
    public Result<Void> unblockByIp(
            @Parameter(description = "IP地址") @RequestParam String ip) {
        boolean success = ipBlacklistService.unblockByIp(ip);
        return success ? Result.success() : Result.fail("解封失败，IP未被封禁");
    }

    @Operation(summary = "分页查询黑名单")
    @GetMapping
    public Result<IPage<IpBlacklistVO>> page(IpBlacklistQueryDTO queryDTO) {
        return Result.success(ipBlacklistService.page(queryDTO));
    }

    @Operation(summary = "获取黑名单详情")
    @GetMapping("/{id}")
    public Result<IpBlacklistVO> getById(
            @Parameter(description = "黑名单记录ID") @PathVariable Long id) {
        IpBlacklistVO vo = ipBlacklistService.getById(id);
        return vo != null ? Result.success(vo) : Result.fail("记录不存在");
    }

    @Operation(summary = "删除黑名单记录")
    @DeleteMapping("/{id}")
    @OperationLog(module = "IP黑名单", type = OperationLog.OperationType.DELETE, description = "删除黑名单记录")
    public Result<Void> delete(
            @Parameter(description = "黑名单记录ID") @PathVariable Long id) {
        boolean success = ipBlacklistService.delete(id);
        return success ? Result.success() : Result.fail("删除失败");
    }

    @Operation(summary = "批量删除黑名单记录")
    @DeleteMapping("/batch")
    @OperationLog(module = "IP黑名单", type = OperationLog.OperationType.DELETE, description = "批量删除黑名单记录")
    public Result<Map<String, Integer>> batchDelete(
            @Parameter(description = "ID列表") @RequestBody List<Long> ids) {
        int count = ipBlacklistService.batchDelete(ids);
        return Result.success(Map.of("deleted", count));
    }

    @Operation(summary = "手动清理过期封禁")
    @PostMapping("/clean-expired")
    @OperationLog(module = "IP黑名单", type = OperationLog.OperationType.OTHER, description = "清理过期封禁记录")
    public Result<Map<String, Integer>> cleanExpired() {
        int count = ipBlacklistService.cleanExpired();
        return Result.success(Map.of("cleaned", count));
    }

    @Operation(summary = "刷新黑名单缓存")
    @PostMapping("/refresh-cache")
    @OperationLog(module = "IP黑名单", type = OperationLog.OperationType.OTHER, description = "刷新IP黑名单缓存")
    public Result<Void> refreshCache() {
        ipBlacklistService.refreshCache();
        return Result.success();
    }

    @Operation(summary = "获取黑名单统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        var blockedIps = ipBlacklistService.getAllBlockedIps();
        return Result.success(Map.of(
                "totalBlocked", blockedIps.size(),
                "blockedIps", blockedIps
        ));
    }
}
