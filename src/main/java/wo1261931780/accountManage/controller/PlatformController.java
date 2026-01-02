package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.request.PlatformCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformQueryDTO;
import wo1261931780.accountManage.dto.request.PlatformUpdateDTO;
import wo1261931780.accountManage.dto.response.PlatformVO;
import wo1261931780.accountManage.entity.Platform;
import wo1261931780.accountManage.service.PlatformService;

import java.util.List;

/**
 * 平台 Controller
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Tag(name = "平台管理", description = "平台的增删改查接口")
@RestController
@RequestMapping("/api/v1/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @Operation(summary = "分页查询平台")
    @GetMapping
    public Result<PageResult<PlatformVO>> page(PlatformQueryDTO query) {
        return Result.success(platformService.page(query));
    }

    @Operation(summary = "获取所有平台（下拉选择用）")
    @GetMapping("/all")
    public Result<List<PlatformVO>> listAll() {
        return Result.success(platformService.listAll());
    }

    @Operation(summary = "根据ID获取平台")
    @GetMapping("/{id}")
    public Result<PlatformVO> getById(
            @Parameter(description = "平台ID") @PathVariable Long id) {
        return Result.success(platformService.getById(id));
    }

    @Operation(summary = "根据类型ID获取平台列表")
    @GetMapping("/type/{typeId}")
    public Result<List<PlatformVO>> listByTypeId(
            @Parameter(description = "平台类型ID") @PathVariable Long typeId) {
        return Result.success(platformService.listByTypeId(typeId));
    }

    @Operation(summary = "创建平台")
    @PostMapping
    public Result<Platform> create(@Valid @RequestBody PlatformCreateDTO dto) {
        return Result.success(platformService.create(dto));
    }

    @Operation(summary = "更新平台")
    @PutMapping("/{id}")
    public Result<Boolean> update(
            @Parameter(description = "平台ID") @PathVariable Long id,
            @Valid @RequestBody PlatformUpdateDTO dto) {
        dto.setId(id);
        return Result.success(platformService.update(dto));
    }

    @Operation(summary = "删除平台")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(
            @Parameter(description = "平台ID") @PathVariable Long id) {
        return Result.success(platformService.delete(id));
    }
}
