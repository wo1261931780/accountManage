package wo1261931780.accountManage.service.impl;

import org.springframework.stereotype.Service;
import wo1261931780.accountManage.dto.password.PasswordGenerateDTO;
import wo1261931780.accountManage.dto.password.PasswordGenerateVO;
import wo1261931780.accountManage.service.PasswordGeneratorService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 密码生成服务实现
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@Service
public class PasswordGeneratorServiceImpl implements PasswordGeneratorService {

    /**
     * 大写字母
     */
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 小写字母
     */
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 数字
     */
    private static final String NUMBERS = "0123456789";

    /**
     * 特殊字符
     */
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    /**
     * 相似字符
     */
    private static final String SIMILAR_CHARS = "0O1lI";

    /**
     * 歧义字符
     */
    private static final String AMBIGUOUS_CHARS = "{}[]()/'\"\\`~,;:.<>";

    private final SecureRandom random = new SecureRandom();

    @Override
    public PasswordGenerateVO generate(PasswordGenerateDTO dto) {
        // 构建字符集
        StringBuilder charPool = new StringBuilder();
        List<String> requiredCharSets = new ArrayList<>();

        if (Boolean.TRUE.equals(dto.getIncludeUppercase())) {
            String chars = filterChars(UPPERCASE, dto);
            if (!chars.isEmpty()) {
                charPool.append(chars);
                requiredCharSets.add(chars);
            }
        }

        if (Boolean.TRUE.equals(dto.getIncludeLowercase())) {
            String chars = filterChars(LOWERCASE, dto);
            if (!chars.isEmpty()) {
                charPool.append(chars);
                requiredCharSets.add(chars);
            }
        }

        if (Boolean.TRUE.equals(dto.getIncludeNumbers())) {
            String chars = filterChars(NUMBERS, dto);
            if (!chars.isEmpty()) {
                charPool.append(chars);
                requiredCharSets.add(chars);
            }
        }

        if (Boolean.TRUE.equals(dto.getIncludeSpecialChars())) {
            String specialChars = dto.getCustomSpecialChars() != null ?
                    dto.getCustomSpecialChars() : SPECIAL_CHARS;
            String chars = filterChars(specialChars, dto);
            if (!chars.isEmpty()) {
                charPool.append(chars);
                requiredCharSets.add(chars);
            }
        }

        // 如果没有选择任何字符类型，默认使用小写字母
        if (charPool.isEmpty()) {
            charPool.append(LOWERCASE);
            requiredCharSets.add(LOWERCASE);
        }

        // 生成密码
        List<String> passwords = new ArrayList<>();
        int count = dto.getCount() != null ? dto.getCount() : 1;
        int length = dto.getLength() != null ? dto.getLength() : 16;

        for (int i = 0; i < count; i++) {
            String password = generatePassword(charPool.toString(), requiredCharSets, length);
            passwords.add(password);
        }

        // 评估密码强度 (取第一个密码作为示例)
        PasswordGenerateVO.PasswordStrength strength = checkStrength(passwords.get(0));

        return PasswordGenerateVO.builder()
                .passwords(passwords)
                .strength(strength)
                .build();
    }

    @Override
    public PasswordGenerateVO.PasswordStrength checkStrength(String password) {
        int score = 0;
        int length = password.length();

        // 长度评分 (最高30分)
        if (length >= 8) score += 10;
        if (length >= 12) score += 10;
        if (length >= 16) score += 10;

        // 字符多样性评分 (最高40分)
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> SPECIAL_CHARS.indexOf(c) >= 0);

        if (hasUppercase) score += 10;
        if (hasLowercase) score += 10;
        if (hasDigit) score += 10;
        if (hasSpecial) score += 10;

        // 无连续字符加分 (最高15分)
        if (!hasConsecutiveChars(password)) score += 15;

        // 无重复字符加分 (最高15分)
        if (!hasRepeatedChars(password)) score += 15;

        // 确定强度等级
        String level;
        String description;
        String crackTime;

        if (score < 40) {
            level = "WEAK";
            description = "密码强度较弱，建议增加长度和字符多样性";
            crackTime = "可能在几秒到几分钟内被破解";
        } else if (score < 60) {
            level = "MEDIUM";
            description = "密码强度中等，可以适当加强";
            crackTime = "可能在几小时到几天内被破解";
        } else if (score < 80) {
            level = "STRONG";
            description = "密码强度较高，安全性良好";
            crackTime = "可能需要数月到数年才能破解";
        } else {
            level = "VERY_STRONG";
            description = "密码强度非常高，安全性极佳";
            crackTime = "可能需要数百年甚至更长时间才能破解";
        }

        return PasswordGenerateVO.PasswordStrength.builder()
                .level(level)
                .score(score)
                .description(description)
                .crackTime(crackTime)
                .build();
    }

    /**
     * 生成单个密码
     */
    private String generatePassword(String charPool, List<String> requiredCharSets, int length) {
        List<Character> passwordChars = new ArrayList<>();

        // 确保每种必需的字符集至少有一个字符
        for (String charSet : requiredCharSets) {
            if (!charSet.isEmpty()) {
                passwordChars.add(charSet.charAt(random.nextInt(charSet.length())));
            }
        }

        // 填充剩余字符
        while (passwordChars.size() < length) {
            passwordChars.add(charPool.charAt(random.nextInt(charPool.length())));
        }

        // 打乱顺序
        Collections.shuffle(passwordChars, random);

        // 构建密码字符串
        StringBuilder password = new StringBuilder();
        for (Character c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    /**
     * 过滤字符集
     */
    private String filterChars(String chars, PasswordGenerateDTO dto) {
        StringBuilder filtered = new StringBuilder();
        for (char c : chars.toCharArray()) {
            // 排除相似字符
            if (Boolean.TRUE.equals(dto.getExcludeSimilar()) && SIMILAR_CHARS.indexOf(c) >= 0) {
                continue;
            }
            // 排除歧义字符
            if (Boolean.TRUE.equals(dto.getExcludeAmbiguous()) && AMBIGUOUS_CHARS.indexOf(c) >= 0) {
                continue;
            }
            filtered.append(c);
        }
        return filtered.toString();
    }

    /**
     * 检查是否有连续字符 (如 abc, 123)
     */
    private boolean hasConsecutiveChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            if (c2 - c1 == 1 && c3 - c2 == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有重复字符 (如 aaa, 111)
     */
    private boolean hasRepeatedChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            if (c1 == c2 && c2 == c3) {
                return true;
            }
        }
        return false;
    }
}
