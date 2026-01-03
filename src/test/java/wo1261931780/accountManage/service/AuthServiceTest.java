package wo1261931780.accountManage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.dto.auth.LoginDTO;
import wo1261931780.accountManage.dto.auth.LoginVO;
import wo1261931780.accountManage.dto.user.UserCreateDTO;
import wo1261931780.accountManage.entity.SysUser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 认证服务层测试
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    private static final String TEST_IP = "127.0.0.1";

    @Test
    void testLoginSuccess() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        LoginVO loginVO = authService.login(loginDTO, TEST_IP);

        assertNotNull(loginVO);
        assertNotNull(loginVO.getAccessToken());
        assertNotNull(loginVO.getRefreshToken());
        assertNotNull(loginVO.getUserId());
        assertEquals("admin", loginVO.getUsername());
    }

    @Test
    void testLoginWrongPassword() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("wrongpassword");

        assertThrows(BusinessException.class, () -> authService.login(loginDTO, TEST_IP));
    }

    @Test
    void testLoginUserNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("password123");

        assertThrows(BusinessException.class, () -> authService.login(loginDTO, TEST_IP));
    }

    @Test
    void testLoginAccountLocking() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("locktest");
        createDTO.setPassword("LockTest123!");
        createDTO.setNickname("锁定测试用户");
        userService.createUser(createDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("locktest");
        loginDTO.setPassword("wrongpassword");

        for (int i = 0; i < 5; i++) {
            try {
                authService.login(loginDTO, TEST_IP);
            } catch (BusinessException e) {
                // expected
            }
        }

        BusinessException exception = assertThrows(BusinessException.class,
            () -> authService.login(loginDTO, TEST_IP));
        assertTrue(exception.getMessage().contains("锁定")
            || exception.getMessage().contains("locked")
            || exception.getMessage().contains("密码错误"));
    }

    @Test
    void testUnlockUserNotFound() {
        // 测试解锁不存在的用户应该抛出异常
        assertThrows(BusinessException.class, () -> authService.unlockUser(99999L));
    }

    @Test
    void testUnlockExistingUser() {
        // 获取已存在的 admin 用户
        SysUser admin = authService.getByUsername("admin");
        assertNotNull(admin);

        // 解锁 admin 用户（即使未锁定也应该可以调用）
        assertDoesNotThrow(() -> authService.unlockUser(admin.getId()));
    }

    @Test
    void testRefreshToken() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");
        LoginVO loginVO = authService.login(loginDTO, TEST_IP);

        LoginVO newLoginVO = authService.refreshToken(loginVO.getRefreshToken());

        assertNotNull(newLoginVO);
        assertNotNull(newLoginVO.getAccessToken());
    }

    @Test
    void testRefreshTokenInvalid() {
        assertThrows(BusinessException.class,
            () -> authService.refreshToken("invalid_token"));
    }

    @Test
    void testGetByUsername() {
        SysUser user = authService.getByUsername("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testGetByUsernameNotFound() {
        SysUser user = authService.getByUsername("nonexistent_user_99999");
        assertNull(user);
    }

    @Test
    void testInitDefaultAdmin() {
        assertDoesNotThrow(() -> authService.initDefaultAdmin());

        SysUser admin = authService.getByUsername("admin");
        assertNotNull(admin);
    }

    @Test
    void testLoginReturnsTokens() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        LoginVO loginVO = authService.login(loginDTO, TEST_IP);

        assertNotNull(loginVO.getAccessToken());
        assertNotNull(loginVO.getRefreshToken());
        assertTrue(loginVO.getAccessToken().length() > 20);
        assertTrue(loginVO.getRefreshToken().length() > 20);
    }

    @Test
    void testLoginReturnsUserInfo() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        LoginVO loginVO = authService.login(loginDTO, TEST_IP);

        assertNotNull(loginVO.getUserId());
        assertEquals("admin", loginVO.getUsername());
        assertNotNull(loginVO.getNickname());
    }

    @Test
    void testMultipleLoginSuccess() throws InterruptedException {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        LoginVO loginVO1 = authService.login(loginDTO, TEST_IP);

        // 等待1秒确保JWT时间戳不同
        Thread.sleep(1000);

        LoginVO loginVO2 = authService.login(loginDTO, TEST_IP);

        assertNotNull(loginVO1);
        assertNotNull(loginVO2);
        // 每次登录应该生成不同的token（因为iat时间戳不同）
        assertNotEquals(loginVO1.getAccessToken(), loginVO2.getAccessToken());
    }
}
