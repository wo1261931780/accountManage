package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.annotation.OperationLog.OperationType;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.tag.TagDTO;
import wo1261931780.accountManage.dto.tag.TagVO;
import wo1261931780.accountManage.entity.Tag;
import wo1261931780.accountManage.service.TagService;

import java.util.List;

/**
 * 标签控制器
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "账号标签相关接口")
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping
    public Result<List<TagVO>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }

    @Operation(summary = "获取标签详情")
    @GetMapping("/{id}")
    public Result<TagVO> getTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        return Result.success(tagService.getTagById(id));
    }

    @Operation(summary = "创建标签")
    @PostMapping
    @OperationLog(module = "标签管理", type = OperationType.CREATE, description = "创建标签")
    public Result<TagVO> createTag(@Valid @RequestBody TagDTO dto) {
        return Result.success(tagService.createTag(dto));
    }

    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    @OperationLog(module = "标签管理", type = OperationType.UPDATE, description = "更新标签")
    public Result<TagVO> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Valid @RequestBody TagDTO dto) {
        return Result.success(tagService.updateTag(id, dto));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    @OperationLog(module = "标签管理", type = OperationType.DELETE, description = "删除标签")
    public Result<Boolean> deleteTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        return Result.success(tagService.deleteTag(id));
    }

    @Operation(summary = "获取账号的标签")
    @GetMapping("/account/{accountId}")
    public Result<List<Tag>> getAccountTags(
            @Parameter(description = "账号ID") @PathVariable Long accountId) {
        return Result.success(tagService.getTagsByAccountId(accountId));
    }

    @Operation(summary = "设置账号标签", description = "替换账号的所有标签")
    @PutMapping("/account/{accountId}")
    @OperationLog(module = "标签管理", type = OperationType.UPDATE, description = "设置账号标签")
    public Result<Void> setAccountTags(
            @Parameter(description = "账号ID") @PathVariable Long accountId,
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        tagService.setAccountTags(accountId, tagIds);
        return Result.success(null);
    }

    @Operation(summary = "为账号添加标签")
    @PostMapping("/account/{accountId}/tag/{tagId}")
    @OperationLog(module = "标签管理", type = OperationType.CREATE, description = "添加账号标签")
    public Result<Void> addTagToAccount(
            @Parameter(description = "账号ID") @PathVariable Long accountId,
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        tagService.addTagToAccount(accountId, tagId);
        return Result.success(null);
    }

    @Operation(summary = "移除账号的标签")
    @DeleteMapping("/account/{accountId}/tag/{tagId}")
    @OperationLog(module = "标签管理", type = OperationType.DELETE, description = "移除账号标签")
    public Result<Void> removeTagFromAccount(
            @Parameter(description = "账号ID") @PathVariable Long accountId,
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        tagService.removeTagFromAccount(accountId, tagId);
        return Result.success(null);
    }

    @Operation(summary = "根据标签获取账号ID列表")
    @GetMapping("/{tagId}/accounts")
    public Result<List<Long>> getAccountsByTag(
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        return Result.success(tagService.getAccountIdsByTagId(tagId));
    }
}
