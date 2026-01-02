package wo1261931780.accountManage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wo1261931780.accountManage.annotation.OperationLog;
import wo1261931780.accountManage.common.result.Result;
import wo1261931780.accountManage.dto.auth.SecondaryVerifyDTO;
import wo1261931780.accountManage.dto.response.PasswordViewVO;
import wo1261931780.accountManage.service.SecondaryVerifyService;

/**
 * 二次验证控制器
 * 用于敏感操作的二次密码验证
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
@Tag(name = "二次验证", description = "敏感操作二次验证接口")
public class SecondaryVerifyController {

    private final SecondaryVerifyService secondaryVerifyService;

    /**
     * 验证当前用户密码
     */
    @PostMapping("/password")
    @Operation(summary = "验证当前用户密码", description = "验证当前登录用户的密码是否正确")
    @OperationLog(module = "二次验证", type = OperationLog.OperationType.QUERY, description = "验证用户密码")
    public Result<Boolean> verifyPassword(@RequestBody @Valid PasswordVerifyRequest request) {
        boolean result = secondaryVerifyService.verifyUserPassword(request.getPassword());
        return Result.success(result);
    }

    /**
     * 二次验证后查看密码
     */
    @PostMapping("/view-password")
    @Operation(summary = "二次验证查看密码", description = "通过二次密码验证后查看账号的明文密码")
    @OperationLog(module = "二次验证", type = OperationLog.OperationType.QUERY, description = "二次验证查看密码")
    public Result<PasswordViewVO> viewPasswordWithVerification(
            @RequestBody @Valid SecondaryVerifyDTO dto) {
        PasswordViewVO vo = secondaryVerifyService.viewPasswordWithVerification(dto);
        return Result.success(vo);
    }

    /**
     * 密码验证请求
     */
    @lombok.Data
    public static class PasswordVerifyRequest {
        @jakarta.validation.constraints.NotBlank(message = "密码不能为空")
        private String password;
    }
}
