package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.request.PlatformTypeCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformTypeUpdateDTO;
import wo1261931780.accountManage.entity.PlatformType;
import wo1261931780.accountManage.service.PlatformTypeService;

import java.util.List;

/**
 * 平台类型 Controller
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Tag(name = "平台类型管理", description = "平台类型的增删改查接口")
@RestController
@RequestMapping("/api/v1/platform-types")
@RequiredArgsConstructor
public class PlatformTypeController {

    private final PlatformTypeService platformTypeService;

    @Operation(summary = "获取所有平台类型")
    @GetMapping
    public Result<List<PlatformType>> list() {
        return Result.success(platformTypeService.listAll());
    }

    @Operation(summary = "获取所有启用的平台类型")
    @GetMapping("/enabled")
    public Result<List<PlatformType>> listEnabled() {
        return Result.success(platformTypeService.listEnabled());
    }

    @Operation(summary = "根据ID获取平台类型")
    @GetMapping("/{id}")
    public Result<PlatformType> getById(
            @Parameter(description = "平台类型ID") @PathVariable Long id) {
        return Result.success(platformTypeService.getById(id));
    }

    @Operation(summary = "创建平台类型")
    @PostMapping
    public Result<PlatformType> create(@Valid @RequestBody PlatformTypeCreateDTO dto) {
        return Result.success(platformTypeService.create(dto));
    }

    @Operation(summary = "更新平台类型")
    @PutMapping("/{id}")
    public Result<Boolean> update(
            @Parameter(description = "平台类型ID") @PathVariable Long id,
            @Valid @RequestBody PlatformTypeUpdateDTO dto) {
        dto.setId(id);
        return Result.success(platformTypeService.update(dto));
    }

    @Operation(summary = "删除平台类型")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(
            @Parameter(description = "平台类型ID") @PathVariable Long id) {
        return Result.success(platformTypeService.delete(id));
    }
}
