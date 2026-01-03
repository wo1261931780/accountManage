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
import wo1261931780.accountManage.dto.log.LoginLogQueryDTO;
import wo1261931780.accountManage.dto.log.LoginLogVO;
import wo1261931780.accountManage.entity.SysLoginLog;
import wo1261931780.accountManage.service.LoginLogService;

/**
 * 登录日志控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Tag(name = "登录日志", description = "登录日志查询相关接口")
@RestController
@RequestMapping("/api/v1/login-logs")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    /**
     * 分页查询登录日志
     */
    @Operation(summary = "分页查询登录日志", description = "根据条件分页查询登录日志")
    @GetMapping
    public Result<IPage<LoginLogVO>> pageLogs(LoginLogQueryDTO queryDTO) {
        Page<SysLoginLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (StrUtil.isNotBlank(queryDTO.getUsername())) {
            wrapper.like(SysLoginLog::getUsername, queryDTO.getUsername());
        }

        // 登录IP模糊查询
        if (StrUtil.isNotBlank(queryDTO.getLoginIp())) {
            wrapper.like(SysLoginLog::getLoginIp, queryDTO.getLoginIp());
        }

        // 登录类型
        if (StrUtil.isNotBlank(queryDTO.getLoginType())) {
            wrapper.eq(SysLoginLog::getLoginType, queryDTO.getLoginType());
        }

        // 状态
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysLoginLog::getStatus, queryDTO.getStatus());
        }

        // 时间范围
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SysLoginLog::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(SysLoginLog::getCreateTime, queryDTO.getEndTime());
        }

        // 按时间倒序
        wrapper.orderByDesc(SysLoginLog::getCreateTime);

        IPage<SysLoginLog> logPage = loginLogService.page(page, wrapper);

        // 转换为VO
        IPage<LoginLogVO> voPage = logPage.convert(log -> {
            LoginLogVO vo = new LoginLogVO();
            BeanUtil.copyProperties(log, vo);
            return vo;
        });

        return Result.success(voPage);
    }

    /**
     * 获取登录日志详情
     */
    @Operation(summary = "获取登录日志详情", description = "根据ID获取登录日志详情")
    @GetMapping("/{id}")
    public Result<LoginLogVO> getLogById(
            @Parameter(description = "日志ID") @PathVariable Long id) {
        SysLoginLog log = loginLogService.getById(id);
        if (log == null) {
            return Result.fail("日志不存在");
        }
        LoginLogVO vo = new LoginLogVO();
        BeanUtil.copyProperties(log, vo);
        return Result.success(vo);
    }

    /**
     * 清除登录日志
     */
    @Operation(summary = "清除登录日志", description = "清除指定天数之前的登录日志")
    @DeleteMapping("/clean")
    public Result<Integer> cleanLogs(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer keepDays) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysLoginLog::getCreateTime, java.time.LocalDateTime.now().minusDays(keepDays));

        long count = loginLogService.count(wrapper);
        if (count > 0) {
            loginLogService.remove(wrapper);
        }

        return Result.success("清除成功", (int) count);
    }
}
