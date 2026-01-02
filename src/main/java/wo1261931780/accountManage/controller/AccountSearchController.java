package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.request.AccountSearchDTO;
import wo1261931780.accountManage.dto.response.AccountSearchVO;
import wo1261931780.accountManage.service.AccountSearchService;

import java.util.List;

/**
 * 账号搜索控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "账号搜索", description = "账号高级搜索接口")
public class AccountSearchController {

    private final AccountSearchService accountSearchService;

    /**
     * 高级搜索
     */
    @PostMapping("/accounts")
    @Operation(summary = "高级搜索账号", description = "支持多条件组合筛选")
    @OperationLog(module = "账号搜索", type = OperationLog.OperationType.QUERY, description = "高级搜索账号")
    public Result<PageResult<AccountSearchVO>> search(@RequestBody AccountSearchDTO dto) {
        return Result.success(accountSearchService.search(dto));
    }

    /**
     * 快速搜索
     */
    @GetMapping("/quick")
    @Operation(summary = "快速搜索", description = "根据关键词全文搜索账号")
    public Result<PageResult<AccountSearchVO>> quickSearch(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(accountSearchService.fullTextSearch(keyword, page, size));
    }

    /**
     * 搜索建议（自动补全）
     */
    @GetMapping("/suggestions")
    @Operation(summary = "搜索建议", description = "输入时自动补全")
    public Result<List<String>> suggestions(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(accountSearchService.searchSuggestions(keyword, limit));
    }
}
