package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.excel.ImportResultVO;
import wo1261931780.accountManage.service.AccountExcelService;

/**
 * 账号导入导出控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Tag(name = "账号导入导出", description = "账号的Excel导入导出功能")
@RestController
@RequestMapping("/api/v1/account/excel")
@RequiredArgsConstructor
public class AccountExcelController {

    private final AccountExcelService accountExcelService;

    /**
     * 导出所有账号
     */
    @Operation(summary = "导出所有账号", description = "将所有账号导出为Excel文件")
    @GetMapping("/export")
    @OperationLog(module = "账号管理", type = OperationLog.OperationType.EXPORT, description = "导出所有账号")
    public void exportAll(HttpServletResponse response) {
        accountExcelService.exportAll(response);
    }

    /**
     * 按平台类型导出账号
     */
    @Operation(summary = "按平台类型导出", description = "按平台类型导出账号为Excel文件")
    @GetMapping("/export/type/{typeId}")
    @OperationLog(module = "账号管理", type = OperationLog.OperationType.EXPORT, description = "按类型导出账号")
    public void exportByType(
            @Parameter(description = "平台类型ID") @PathVariable Long typeId,
            HttpServletResponse response) {
        accountExcelService.exportByType(typeId, response);
    }

    /**
     * 按平台导出账号
     */
    @Operation(summary = "按平台导出", description = "按平台导出账号为Excel文件")
    @GetMapping("/export/platform/{platformId}")
    @OperationLog(module = "账号管理", type = OperationLog.OperationType.EXPORT, description = "按平台导出账号")
    public void exportByPlatform(
            @Parameter(description = "平台ID") @PathVariable Long platformId,
            HttpServletResponse response) {
        accountExcelService.exportByPlatform(platformId, response);
    }

    /**
     * 导入账号
     */
    @Operation(summary = "导入账号", description = "从Excel文件导入账号")
    @PostMapping("/import")
    @OperationLog(module = "账号管理", type = OperationLog.OperationType.IMPORT, description = "导入账号")
    public Result<ImportResultVO> importAccounts(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        ImportResultVO result = accountExcelService.importAccounts(file);
        return Result.success(result);
    }

    /**
     * 下载导入模板
     */
    @Operation(summary = "下载导入模板", description = "下载账号导入的Excel模板")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        accountExcelService.downloadTemplate(response);
    }
}
