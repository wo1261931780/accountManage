package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.annotation.RateLimit;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.request.KeyRotationDTO;
import wo1261931780.accountManage.dto.response.KeyRotationResultVO;
import wo1261931780.accountManage.service.KeyRotationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 密钥管理控制器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/security/key")
@RequiredArgsConstructor
@Tag(name = "密钥管理", description = "AES密钥轮转和管理接口")
public class KeyRotationController {

    private final KeyRotationService keyRotationService;

    /**
     * 执行密钥轮转
     */
    @PostMapping("/rotate")
    @Operation(summary = "密钥轮转", description = "用新密钥重新加密所有账号密码（危险操作，需要二次验证）")
    @OperationLog(module = "密钥管理", type = OperationLog.OperationType.UPDATE, description = "执行密钥轮转")
    @RateLimit(key = "key-rotate", limit = 3, window = 3600, message = "密钥轮转操作过于频繁，每小时最多3次")
    public Result<KeyRotationResultVO> rotateKey(@Valid @RequestBody KeyRotationDTO dto) {
        return Result.success(keyRotationService.rotateKey(dto));
    }

    /**
     * 验证密钥有效性
     */
    @PostMapping("/validate")
    @Operation(summary = "验证密钥", description = "验证新密钥是否符合要求")
    public Result<Boolean> validateKey(@RequestBody Map<String, String> body) {
        String newKey = body.get("newKey");
        return Result.success(keyRotationService.validateKey(newKey));
    }

    /**
     * 获取当前密钥信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取密钥信息", description = "获取当前密钥信息（脱敏显示）")
    public Result<Map<String, Object>> getKeyInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("keyInfo", keyRotationService.getCurrentKeyInfo());
        info.put("lastRotationTime", keyRotationService.getLastRotationTime());
        return Result.success(info);
    }

    /**
     * 生成随机密钥
     */
    @GetMapping("/generate")
    @Operation(summary = "生成随机密钥", description = "生成一个随机的AES密钥")
    public Result<String> generateKey(
            @RequestParam(defaultValue = "32") Integer length) {
        if (length < 16 || length > 32) {
            length = 32;
        }
        String key = cn.hutool.core.util.RandomUtil.randomString(
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*",
                length
        );
        return Result.success(key);
    }
}
