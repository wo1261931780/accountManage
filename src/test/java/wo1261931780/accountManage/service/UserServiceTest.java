package wo1261931780.accountManage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.dto.user.*;
import wo1261931780.accountManage.entity.SysUser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务层测试
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("newuser");
        createDTO.setPassword("NewUser123!");
        createDTO.setNickname("新用户");
        createDTO.setEmail("newuser@example.com");

        Long userId = userService.createUser(createDTO);

        assertNotNull(userId);
        assertTrue(userId > 0);
    }

    @Test
    void testCreateUserDuplicateUsername() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("duplicateuser");
        createDTO.setPassword("Duplicate123!");
        createDTO.setNickname("重复用户");
        userService.createUser(createDTO);

        UserCreateDTO duplicateDTO = new UserCreateDTO();
        duplicateDTO.setUsername("duplicateuser");
        duplicateDTO.setPassword("Duplicate456!");
        duplicateDTO.setNickname("重复用户2");

        assertThrows(BusinessException.class, () -> userService.createUser(duplicateDTO));
    }

    @Test
    void testGetUserById() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("getbyiduser");
        createDTO.setPassword("GetById123!");
        createDTO.setNickname("获取用户");
        Long userId = userService.createUser(createDTO);

        UserVO user = userService.getUserById(userId);

        assertNotNull(user);
        assertEquals("getbyiduser", user.getUsername());
        assertEquals("获取用户", user.getNickname());
    }

    @Test
    void testGetByIdWithIService() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("getbynameuser");
        createDTO.setPassword("GetByName123!");
        createDTO.setNickname("按用户名获取");
        Long userId = userService.createUser(createDTO);

        SysUser user = userService.getById(userId);

        assertNotNull(user);
        assertEquals("getbynameuser", user.getUsername());
    }

    @Test
    void testUpdateUser() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("updateuser");
        createDTO.setPassword("Update123!");
        createDTO.setNickname("更新前用户");
        Long userId = userService.createUser(createDTO);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setNickname("更新后用户");
        updateDTO.setEmail("updated@example.com");

        boolean result = userService.updateUser(userId, updateDTO);
        assertTrue(result);

        UserVO user = userService.getUserById(userId);
        assertEquals("更新后用户", user.getNickname());
        assertEquals("updated@example.com", user.getEmail());
    }

    @Test
    void testUpdateUserStatus() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("statususer");
        createDTO.setPassword("Status123!");
        createDTO.setNickname("状态用户");
        Long userId = userService.createUser(createDTO);

        boolean disableResult = userService.updateUserStatus(userId, 0);
        assertTrue(disableResult);

        SysUser user = userService.getById(userId);
        assertEquals(0, user.getStatus());

        boolean enableResult = userService.updateUserStatus(userId, 1);
        assertTrue(enableResult);

        user = userService.getById(userId);
        assertEquals(1, user.getStatus());
    }

    @Test
    void testDeleteUser() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("deleteuser");
        createDTO.setPassword("Delete123!");
        createDTO.setNickname("删除用户");
        Long userId = userService.createUser(createDTO);

        boolean result = userService.deleteUser(userId);
        assertTrue(result);
    }

    @Test
    void testResetPassword() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("resetpwduser");
        createDTO.setPassword("ResetPwd123!");
        createDTO.setNickname("重置密码用户");
        Long userId = userService.createUser(createDTO);

        ResetPasswordDTO resetDTO = new ResetPasswordDTO();
        resetDTO.setNewPassword("NewPassword456!");

        boolean result = userService.resetPassword(userId, resetDTO);
        assertTrue(result);
    }

    @Test
    void testPageUsers() {
        for (int i = 1; i <= 5; i++) {
            UserCreateDTO createDTO = new UserCreateDTO();
            createDTO.setUsername("pageuser" + i);
            createDTO.setPassword("PageUser" + i + "23!");
            createDTO.setNickname("分页用户" + i);
            userService.createUser(createDTO);
        }

        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setCurrent(1);
        queryDTO.setSize(10);

        IPage<UserVO> result = userService.pageUsers(queryDTO);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
    }

    @Test
    void testPageUsersWithKeyword() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("searchuser");
        createDTO.setPassword("Search123!");
        createDTO.setNickname("搜索用户");
        userService.createUser(createDTO);

        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setCurrent(1);
        queryDTO.setSize(10);
        queryDTO.setKeyword("searchuser");

        IPage<UserVO> result = userService.pageUsers(queryDTO);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
    }

    @Test
    void testExistsByUsername() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("checkexistsuser");
        createDTO.setPassword("CheckExists123!");
        createDTO.setNickname("检查存在用户");
        userService.createUser(createDTO);

        boolean exists = userService.existsByUsername("checkexistsuser");
        assertTrue(exists);

        boolean notExists = userService.existsByUsername("nonexistent_user_check");
        assertFalse(notExists);
    }

    @Test
    void testExistsByEmail() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("checkemailuser");
        createDTO.setPassword("CheckEmail123!");
        createDTO.setNickname("检查邮箱用户");
        createDTO.setEmail("checkemail@example.com");
        userService.createUser(createDTO);

        boolean exists = userService.existsByEmail("checkemail@example.com", null);
        assertTrue(exists);

        boolean notExists = userService.existsByEmail("nonexistent@example.com", null);
        assertFalse(notExists);
    }

    @Test
    void testExistsByPhone() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("checkphoneuser");
        createDTO.setPassword("CheckPhone123!");
        createDTO.setNickname("检查手机用户");
        createDTO.setPhone("13800138000");
        userService.createUser(createDTO);

        boolean exists = userService.existsByPhone("13800138000", null);
        assertTrue(exists);

        boolean notExists = userService.existsByPhone("13900139000", null);
        assertFalse(notExists);
    }

    @Test
    void testAdminUserExists() {
        boolean exists = userService.existsByUsername("admin");
        assertTrue(exists);
    }
}
