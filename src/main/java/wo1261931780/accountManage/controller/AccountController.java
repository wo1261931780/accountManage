package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.request.AccountCreateDTO;
import wo1261931780.accountManage.dto.request.AccountQueryDTO;
import wo1261931780.accountManage.dto.request.AccountUpdateDTO;
import wo1261931780.accountManage.dto.response.AccountVO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.service.AccountService;

import java.util.List;

/**
 * 账号 Controller
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Tag(name = "账号管理", description = "账号的增删改查接口")
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "分页查询账号")
    @GetMapping
    public Result<PageResult<AccountVO>> page(AccountQueryDTO query) {
        return Result.success(accountService.page(query));
    }

    @Operation(summary = "根据ID获取账号（含密码）")
    @GetMapping("/{id}")
    public Result<AccountVO> getById(
            @Parameter(description = "账号ID") @PathVariable Long id) {
        return Result.success(accountService.getById(id));
    }

    @Operation(summary = "根据平台ID获取账号列表")
    @GetMapping("/platform/{platformId}")
    public Result<List<AccountVO>> listByPlatformId(
            @Parameter(description = "平台ID") @PathVariable Long platformId) {
        return Result.success(accountService.listByPlatformId(platformId));
    }

    @Operation(summary = "获取账号密码")
    @GetMapping("/{id}/password")
    public Result<String> getPassword(
            @Parameter(description = "账号ID") @PathVariable Long id) {
        return Result.success(accountService.getPassword(id));
    }

    @Operation(summary = "创建账号")
    @PostMapping
    public Result<Account> create(@Valid @RequestBody AccountCreateDTO dto) {
        return Result.success(accountService.create(dto));
    }

    @Operation(summary = "更新账号")
    @PutMapping("/{id}")
    public Result<Boolean> update(
            @Parameter(description = "账号ID") @PathVariable Long id,
            @Valid @RequestBody AccountUpdateDTO dto) {
        dto.setId(id);
        return Result.success(accountService.update(dto));
    }

    @Operation(summary = "删除账号")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(
            @Parameter(description = "账号ID") @PathVariable Long id) {
        return Result.success(accountService.delete(id));
    }

    @Operation(summary = "批量删除账号")
    @PostMapping("/batch-delete")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        return Result.success(accountService.batchDelete(ids));
    }
}
