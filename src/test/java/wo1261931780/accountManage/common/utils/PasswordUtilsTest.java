package wo1261931780.accountManage.common.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码工具类测试
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@SpringBootTest
@ActiveProfiles("test")
class PasswordUtilsTest {

    @Autowired
    private PasswordUtils passwordUtils;

    @Test
    void testGenerateSalt() {
        String salt = passwordUtils.generateSalt();
        assertNotNull(salt);
        assertEquals(16, salt.length());

        // 生成的盐值应该是随机的
        String salt2 = passwordUtils.generateSalt();
        assertNotEquals(salt, salt2);
    }

    @Test
    void testEncryptAndDecrypt() {
        String plainPassword = "MySecurePassword123!@#";
        String salt = passwordUtils.generateSalt();

        // 加密
        String encrypted = passwordUtils.encrypt(plainPassword, salt);
        assertNotNull(encrypted);
        assertNotEquals(plainPassword, encrypted);

        // 解密
        String decrypted = passwordUtils.decrypt(encrypted, salt);
        assertEquals(plainPassword, decrypted);
    }

    @Test
    void testEncryptWithDifferentSalts() {
        String plainPassword = "SamePassword";
        String salt1 = passwordUtils.generateSalt();
        String salt2 = passwordUtils.generateSalt();

        String encrypted1 = passwordUtils.encrypt(plainPassword, salt1);
        String encrypted2 = passwordUtils.encrypt(plainPassword, salt2);

        // 相同密码不同盐值应该产生不同的加密结果
        assertNotEquals(encrypted1, encrypted2);

        // 但都能正确解密
        assertEquals(plainPassword, passwordUtils.decrypt(encrypted1, salt1));
        assertEquals(plainPassword, passwordUtils.decrypt(encrypted2, salt2));
    }

    @Test
    void testEncryptEmptyPassword() {
        String salt = passwordUtils.generateSalt();
        String encrypted = passwordUtils.encrypt("", salt);
        assertNotNull(encrypted);

        String decrypted = passwordUtils.decrypt(encrypted, salt);
        assertEquals("", decrypted);
    }

    @Test
    void testEncryptChinesePassword() {
        String plainPassword = "中文密码123";
        String salt = passwordUtils.generateSalt();

        String encrypted = passwordUtils.encrypt(plainPassword, salt);
        String decrypted = passwordUtils.decrypt(encrypted, salt);

        assertEquals(plainPassword, decrypted);
    }

    @Test
    void testEncryptSpecialCharacters() {
        String plainPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        String salt = passwordUtils.generateSalt();

        String encrypted = passwordUtils.encrypt(plainPassword, salt);
        String decrypted = passwordUtils.decrypt(encrypted, salt);

        assertEquals(plainPassword, decrypted);
    }

    @Test
    void testEncryptLongPassword() {
        // 测试长密码
        String plainPassword = "A".repeat(500);
        String salt = passwordUtils.generateSalt();

        String encrypted = passwordUtils.encrypt(plainPassword, salt);
        String decrypted = passwordUtils.decrypt(encrypted, salt);

        assertEquals(plainPassword, decrypted);
    }
}
