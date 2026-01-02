package wo1261931780.accountManage.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import wo1261931780.accountManage.config.SensitiveSerializer;

import java.lang.annotation.*;

/**
 * 敏感数据脱敏注解
 * 用于标记需要脱敏的字段
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface Sensitive {

    /**
     * 脱敏类型
     */
    SensitiveType type() default SensitiveType.DEFAULT;

    /**
     * 前置保留长度
     */
    int prefixLength() default 0;

    /**
     * 后置保留长度
     */
    int suffixLength() default 0;

    /**
     * 脱敏类型枚举
     */
    enum SensitiveType {
        /**
         * 默认脱敏（保留首尾各1位）
         */
        DEFAULT,

        /**
         * 邮箱脱敏（保留@前首字符和@后域名）
         */
        EMAIL,

        /**
         * 手机号脱敏（保留前3后4）
         */
        PHONE,

        /**
         * 身份证脱敏（保留前6后4）
         */
        ID_CARD,

        /**
         * 银行卡脱敏（保留前4后4）
         */
        BANK_CARD,

        /**
         * 密码脱敏（全部替换为*）
         */
        PASSWORD,

        /**
         * 地址脱敏（保留前6位）
         */
        ADDRESS,

        /**
         * 姓名脱敏（保留姓）
         */
        NAME,

        /**
         * 自定义脱敏
         */
        CUSTOM
    }
}
