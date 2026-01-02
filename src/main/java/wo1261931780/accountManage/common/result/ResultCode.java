package wo1261931780.accountManage.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 服务器内部错误
     */
    INTERNAL_ERROR(500, "服务器内部错误"),

    /**
     * 数据已存在
     */
    DATA_EXIST(1001, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(1002, "数据不存在"),

    /**
     * 密码加密失败
     */
    PASSWORD_ENCRYPT_ERROR(2001, "密码加密失败"),

    /**
     * 密码解密失败
     */
    PASSWORD_DECRYPT_ERROR(2002, "密码解密失败"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(3001, "用户不存在"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(3002, "密码错误"),

    /**
     * 用户已禁用
     */
    USER_DISABLED(3003, "账号已禁用"),

    /**
     * 令牌无效
     */
    TOKEN_INVALID(3004, "令牌无效"),

    /**
     * 令牌已过期
     */
    TOKEN_EXPIRED(3005, "令牌已过期");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}
