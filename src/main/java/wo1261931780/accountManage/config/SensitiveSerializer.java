package wo1261931780.accountManage.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import wo1261931780.accountManage.annotation.Sensitive;
import wo1261931780.accountManage.annotation.Sensitive.SensitiveType;

import java.io.IOException;
import java.util.Objects;

/**
 * 敏感数据脱敏序列化器
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveType type;
    private int prefixLength;
    private int suffixLength;

    public SensitiveSerializer() {
    }

    public SensitiveSerializer(SensitiveType type, int prefixLength, int suffixLength) {
        this.type = type;
        this.prefixLength = prefixLength;
        this.suffixLength = suffixLength;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StrUtil.isBlank(value)) {
            gen.writeString(value);
            return;
        }
        gen.writeString(desensitize(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property == null) {
            return this;
        }
        Sensitive sensitive = property.getAnnotation(Sensitive.class);
        if (sensitive == null) {
            sensitive = property.getContextAnnotation(Sensitive.class);
        }
        if (sensitive != null) {
            return new SensitiveSerializer(sensitive.type(), sensitive.prefixLength(), sensitive.suffixLength());
        }
        return this;
    }

    /**
     * 执行脱敏
     */
    private String desensitize(String value) {
        if (type == null) {
            return value;
        }
        return switch (type) {
            case EMAIL -> desensitizeEmail(value);
            case PHONE -> desensitizePhone(value);
            case ID_CARD -> desensitizeIdCard(value);
            case BANK_CARD -> desensitizeBankCard(value);
            case PASSWORD -> desensitizePassword(value);
            case ADDRESS -> desensitizeAddress(value);
            case NAME -> desensitizeName(value);
            case CUSTOM -> desensitizeCustom(value, prefixLength, suffixLength);
            default -> desensitizeDefault(value);
        };
    }

    /**
     * 默认脱敏（保留首尾各1位）
     */
    private String desensitizeDefault(String value) {
        if (value.length() <= 2) {
            return "*".repeat(value.length());
        }
        return value.charAt(0) + "*".repeat(value.length() - 2) + value.charAt(value.length() - 1);
    }

    /**
     * 邮箱脱敏
     * 示例: t***@example.com
     */
    private String desensitizeEmail(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 1) {
            return value;
        }
        return value.charAt(0) + "***" + value.substring(atIndex);
    }

    /**
     * 手机号脱敏（保留前3后4）
     * 示例: 138****1234
     */
    private String desensitizePhone(String value) {
        if (value.length() < 7) {
            return value;
        }
        return value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    /**
     * 身份证脱敏（保留前6后4）
     * 示例: 110101********1234
     */
    private String desensitizeIdCard(String value) {
        if (value.length() < 10) {
            return value;
        }
        return value.substring(0, 6) + "*".repeat(value.length() - 10) + value.substring(value.length() - 4);
    }

    /**
     * 银行卡脱敏（保留前4后4）
     * 示例: 6222****1234
     */
    private String desensitizeBankCard(String value) {
        if (value.length() < 8) {
            return value;
        }
        return value.substring(0, 4) + "*".repeat(value.length() - 8) + value.substring(value.length() - 4);
    }

    /**
     * 密码脱敏（全部替换）
     */
    private String desensitizePassword(String value) {
        return "******";
    }

    /**
     * 地址脱敏（保留前6位）
     */
    private String desensitizeAddress(String value) {
        if (value.length() <= 6) {
            return value;
        }
        return value.substring(0, 6) + "******";
    }

    /**
     * 姓名脱敏（保留姓）
     */
    private String desensitizeName(String value) {
        if (value.length() <= 1) {
            return "*";
        }
        return value.charAt(0) + "*".repeat(value.length() - 1);
    }

    /**
     * 自定义脱敏
     */
    private String desensitizeCustom(String value, int prefixLen, int suffixLen) {
        if (value.length() <= prefixLen + suffixLen) {
            return "*".repeat(value.length());
        }
        String prefix = prefixLen > 0 ? value.substring(0, prefixLen) : "";
        String suffix = suffixLen > 0 ? value.substring(value.length() - suffixLen) : "";
        int maskLen = value.length() - prefixLen - suffixLen;
        return prefix + "*".repeat(maskLen) + suffix;
    }
}
