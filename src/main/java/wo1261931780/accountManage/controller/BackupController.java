package wo1261931780.accountManage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.annotation.OperationLog.OperationType;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.backup.BackupCreateDTO;
import wo1261931780.accountManage.dto.backup.BackupVO;
import wo1261931780.accountManage.service.BackupService;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 数据备份控制器
 */
@Tag(name = "数据备份管理", description = "数据备份与恢复相关接口")
@RestController
@RequestMapping("/api/v1/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @Operation(summary = "创建备份", description = "手动创建数据备份")
    @PostMapping
    @OperationLog(module = "数据备份", type = OperationType.CREATE, description = "创建数据备份")
    public Result<BackupVO> createBackup(@RequestBody(required = false) BackupCreateDTO dto) {
        return Result.success(backupService.createBackup(dto));
    }

    @Operation(summary = "恢复备份", description = "从备份恢复数据（谨慎操作）")
    @PostMapping("/{id}/restore")
    @OperationLog(module = "数据备份", type = OperationType.UPDATE, description = "恢复数据备份")
    public Result<Boolean> restoreBackup(
            @Parameter(description = "备份ID") @PathVariable Long id) {
        return Result.success(backupService.restoreBackup(id));
    }

    @Operation(summary = "分页查询备份列表")
    @GetMapping
    public Result<Page<BackupVO>> listBackups(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "备份类型: 1-手动 2-自动") @RequestParam(required = false) Integer backupType,
            @Parameter(description = "状态: 0-进行中 1-成功 2-失败") @RequestParam(required = false) Integer status) {
        Page<BackupVO> pageParam = new Page<>(page, size);
        return Result.success(backupService.listBackups(pageParam, backupType, status));
    }

    @Operation(summary = "获取备份详情")
    @GetMapping("/{id}")
    public Result<BackupVO> getBackup(
            @Parameter(description = "备份ID") @PathVariable Long id) {
        return Result.success(backupService.getBackupById(id));
    }

    @Operation(summary = "删除备份")
    @DeleteMapping("/{id}")
    @OperationLog(module = "数据备份", type = OperationType.DELETE, description = "删除数据备份")
    public Result<Boolean> deleteBackup(
            @Parameter(description = "备份ID") @PathVariable Long id) {
        return Result.success(backupService.deleteBackup(id));
    }

    @Operation(summary = "下载备份文件")
    @GetMapping("/{id}/download")
    public void downloadBackup(
            @Parameter(description = "备份ID") @PathVariable Long id,
            HttpServletResponse response) throws IOException {
        String filePath = backupService.getBackupFilePath(id);
        File file = new File(filePath);

        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
        response.setContentLength((int) file.length());

        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }
}
