package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.password.PasswordGenerateDTO;
import wo1261931780.accountManage.dto.password.PasswordGenerateVO;
import wo1261931780.accountManage.dto.password.PasswordStrengthDTO;
import wo1261931780.accountManage.service.PasswordGeneratorService;

/**
 * 密码生成器控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Tag(name = "密码生成器", description = "密码生成与强度检测")
@RestController
@RequestMapping("/api/v1/password")
@RequiredArgsConstructor
public class PasswordGeneratorController {

    private final PasswordGeneratorService passwordGeneratorService;

    /**
     * 生成密码
     */
    @Operation(summary = "生成密码", description = "根据指定规则生成随机密码")
    @PostMapping("/generate")
    public Result<PasswordGenerateVO> generate(@Valid @RequestBody PasswordGenerateDTO dto) {
        PasswordGenerateVO result = passwordGeneratorService.generate(dto);
        return Result.success(result);
    }

    /**
     * 快速生成密码
     */
    @Operation(summary = "快速生成密码", description = "使用默认规则快速生成一个16位随机密码")
    @GetMapping("/quick")
    public Result<PasswordGenerateVO> quickGenerate() {
        PasswordGenerateDTO dto = new PasswordGenerateDTO();
        dto.setLength(16);
        dto.setIncludeUppercase(true);
        dto.setIncludeLowercase(true);
        dto.setIncludeNumbers(true);
        dto.setIncludeSpecialChars(true);
        dto.setCount(1);
        PasswordGenerateVO result = passwordGeneratorService.generate(dto);
        return Result.success(result);
    }

    /**
     * 检查密码强度
     */
    @Operation(summary = "检查密码强度", description = "评估密码的安全强度")
    @PostMapping("/strength")
    public Result<PasswordGenerateVO.PasswordStrength> checkStrength(@Valid @RequestBody PasswordStrengthDTO dto) {
        PasswordGenerateVO.PasswordStrength strength = passwordGeneratorService.checkStrength(dto.getPassword());
        return Result.success(strength);
    }
}
