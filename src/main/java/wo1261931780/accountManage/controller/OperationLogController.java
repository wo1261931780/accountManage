package wo1261931780.accountManage.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.log.OperationLogQueryDTO;
import wo1261931780.accountManage.dto.log.OperationLogVO;
import wo1261931780.accountManage.entity.SysOperationLog;
import wo1261931780.accountManage.service.OperationLogService;

/**
 * 操作日志控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Tag(name = "操作日志", description = "操作日志查询相关接口")
@RestController
@RequestMapping("/api/v1/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @Operation(summary = "分页查询操作日志", description = "根据条件分页查询操作日志")
    @GetMapping
    public Result<IPage<OperationLogVO>> pageLogs(OperationLogQueryDTO queryDTO) {
        Page<SysOperationLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (StrUtil.isNotBlank(queryDTO.getUsername())) {
            wrapper.like(SysOperationLog::getUsername, queryDTO.getUsername());
        }

        // 操作模块
        if (StrUtil.isNotBlank(queryDTO.getModule())) {
            wrapper.eq(SysOperationLog::getModule, queryDTO.getModule());
        }

        // 操作类型
        if (StrUtil.isNotBlank(queryDTO.getOperationType())) {
            wrapper.eq(SysOperationLog::getOperationType, queryDTO.getOperationType());
        }

        // 状态
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysOperationLog::getStatus, queryDTO.getStatus());
        }

        // 时间范围
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SysOperationLog::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(SysOperationLog::getCreateTime, queryDTO.getEndTime());
        }

        // 按时间倒序
        wrapper.orderByDesc(SysOperationLog::getCreateTime);

        IPage<SysOperationLog> logPage = operationLogService.page(page, wrapper);

        // 转换为VO
        IPage<OperationLogVO> voPage = logPage.convert(log -> {
            OperationLogVO vo = new OperationLogVO();
            BeanUtil.copyProperties(log, vo);
            return vo;
        });

        return Result.success(voPage);
    }

    /**
     * 获取操作日志详情
     */
    @Operation(summary = "获取操作日志详情", description = "根据ID获取操作日志详情")
    @GetMapping("/{id}")
    public Result<OperationLogVO> getLogById(
            @Parameter(description = "日志ID") @PathVariable Long id) {
        SysOperationLog log = operationLogService.getById(id);
        if (log == null) {
            return Result.fail("日志不存在");
        }
        OperationLogVO vo = new OperationLogVO();
        BeanUtil.copyProperties(log, vo);
        return Result.success(vo);
    }

    /**
     * 清除操作日志
     */
    @Operation(summary = "清除操作日志", description = "清除指定天数之前的操作日志")
    @DeleteMapping("/clean")
    public Result<Integer> cleanLogs(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "90") Integer keepDays) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysOperationLog::getCreateTime, java.time.LocalDateTime.now().minusDays(keepDays));

        long count = operationLogService.count(wrapper);
        if (count > 0) {
            operationLogService.remove(wrapper);
        }

        return Result.success("清除成功", (int) count);
    }

    /**
     * 获取所有操作模块列表
     */
    @Operation(summary = "获取操作模块列表", description = "获取所有操作模块名称列表")
    @GetMapping("/modules")
    public Result<java.util.List<String>> getModules() {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysOperationLog::getModule);
        wrapper.groupBy(SysOperationLog::getModule);
        wrapper.orderByAsc(SysOperationLog::getModule);

        java.util.List<SysOperationLog> logs = operationLogService.list(wrapper);
        java.util.List<String> modules = logs.stream()
                .map(SysOperationLog::getModule)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();

        return Result.success(modules);
    }
}
