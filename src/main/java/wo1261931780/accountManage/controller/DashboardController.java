package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.response.DashboardStatsVO;
import wo1261931780.accountManage.service.DashboardService;

/**
 * 统计仪表盘控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "统计仪表盘", description = "数据统计和仪表盘接口")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取统计数据", description = "获取仪表盘的所有统计数据")
    public Result<DashboardStatsVO> getStats() {
        return Result.success(dashboardService.getStats());
    }

    /**
     * 获取账号总数
     */
    @GetMapping("/total-accounts")
    @Operation(summary = "获取账号总数", description = "获取系统中的账号总数")
    public Result<Long> getTotalAccounts() {
        return Result.success(dashboardService.getTotalAccounts());
    }

    /**
     * 获取平台总数
     */
    @GetMapping("/total-platforms")
    @Operation(summary = "获取平台总数", description = "获取系统中的平台总数")
    public Result<Long> getTotalPlatforms() {
        return Result.success(dashboardService.getTotalPlatforms());
    }
}
