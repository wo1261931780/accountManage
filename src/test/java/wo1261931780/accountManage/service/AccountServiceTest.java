package wo1261931780.accountManage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.dto.request.AccountCreateDTO;
import wo1261931780.accountManage.dto.request.AccountQueryDTO;
import wo1261931780.accountManage.dto.request.AccountUpdateDTO;
import wo1261931780.accountManage.dto.response.AccountVO;
import wo1261931780.accountManage.entity.Account;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 账号 Service 测试
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    private Long testAccountId;

    @BeforeEach
    void setUp() {
        // 创建测试账号
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setPlatformId(101L);
        dto.setAccountName("test@example.com");
        dto.setAccountAlias("测试账号");
        dto.setUid("123456789");
        dto.setBindPhone("13800138000");
        dto.setBindEmail("test@example.com");
        dto.setPassword("TestPassword123!");
        dto.setHasSecurityTool(0);
        dto.setAccountStatus(1);
        dto.setImportanceLevel(2);
        dto.setIsMainAccount(1);
        dto.setRegisterTime(LocalDate.of(2020, 1, 1));

        Account created = accountService.create(dto);
        testAccountId = created.getId();
    }

    @Test
    void testPage() {
        AccountQueryDTO query = new AccountQueryDTO();
        query.setCurrent(1L);
        query.setSize(10L);

        PageResult<AccountVO> result = accountService.page(query);
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testPageWithFilter() {
        AccountQueryDTO query = new AccountQueryDTO();
        query.setCurrent(1L);
        query.setSize(10L);
        query.setAccountName("test");

        PageResult<AccountVO> result = accountService.page(query);
        assertNotNull(result);
        assertTrue(result.getRecords().stream()
                .anyMatch(a -> a.getAccountName().contains("test")));
    }

    @Test
    void testPageWithKeyword() {
        AccountQueryDTO query = new AccountQueryDTO();
        query.setCurrent(1L);
        query.setSize(10L);
        query.setKeyword("测试");

        PageResult<AccountVO> result = accountService.page(query);
        assertNotNull(result);
    }

    @Test
    void testGetById() {
        AccountVO account = accountService.getById(testAccountId);
        assertNotNull(account);
        assertEquals("test@example.com", account.getAccountName());
        assertEquals("测试账号", account.getAccountAlias());
        // 应该返回解密后的密码
        assertNotNull(account.getPassword());
        assertEquals("TestPassword123!", account.getPassword());
        // 应该关联平台信息
        assertNotNull(account.getPlatformName());
    }

    @Test
    void testGetByIdNotFound() {
        assertThrows(BusinessException.class, () -> {
            accountService.getById(99999L);
        });
    }

    @Test
    void testListByPlatformId() {
        List<AccountVO> list = accountService.listByPlatformId(101L);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        // 应该包含解密后的密码
        for (AccountVO account : list) {
            assertNotNull(account.getPassword());
        }
    }

    @Test
    void testGetPassword() {
        String password = accountService.getPassword(testAccountId);
        assertNotNull(password);
        assertEquals("TestPassword123!", password);
    }

    @Test
    void testCreate() {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setPlatformId(102L);
        dto.setAccountName("newuser@qq.com");
        dto.setAccountAlias("新账号");
        dto.setPassword("NewPassword456!");
        dto.setAccountStatus(1);

        Account created = accountService.create(dto);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertNotNull(created.getPasswordEncrypted());
        assertNotNull(created.getPasswordSalt());
        // 密码应该是加密存储的
        assertNotEquals("NewPassword456!", created.getPasswordEncrypted());
    }

    @Test
    void testCreateInvalidPlatform() {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setPlatformId(99999L); // 不存在的平台
        dto.setAccountName("invalid@test.com");
        dto.setPassword("Password123!");

        assertThrows(BusinessException.class, () -> {
            accountService.create(dto);
        });
    }

    @Test
    void testUpdate() {
        AccountUpdateDTO dto = new AccountUpdateDTO();
        dto.setId(testAccountId);
        dto.setAccountAlias("更新后的别名");
        dto.setImportanceLevel(4);

        boolean result = accountService.update(dto);
        assertTrue(result);

        AccountVO updated = accountService.getById(testAccountId);
        assertEquals("更新后的别名", updated.getAccountAlias());
        assertEquals(4, updated.getImportanceLevel());
        // 密码应该保持不变
        assertEquals("TestPassword123!", updated.getPassword());
    }

    @Test
    void testUpdatePassword() {
        AccountUpdateDTO dto = new AccountUpdateDTO();
        dto.setId(testAccountId);
        dto.setPassword("NewUpdatedPassword789!");

        boolean result = accountService.update(dto);
        assertTrue(result);

        // 验证密码已更新
        String newPassword = accountService.getPassword(testAccountId);
        assertEquals("NewUpdatedPassword789!", newPassword);
    }

    @Test
    void testDelete() {
        boolean result = accountService.delete(testAccountId);
        assertTrue(result);

        assertThrows(BusinessException.class, () -> {
            accountService.getById(testAccountId);
        });
    }

    @Test
    void testBatchDelete() {
        // 创建多个账号
        AccountCreateDTO dto1 = new AccountCreateDTO();
        dto1.setPlatformId(101L);
        dto1.setAccountName("batch1@test.com");
        dto1.setPassword("Password1!");
        Account account1 = accountService.create(dto1);

        AccountCreateDTO dto2 = new AccountCreateDTO();
        dto2.setPlatformId(101L);
        dto2.setAccountName("batch2@test.com");
        dto2.setPassword("Password2!");
        Account account2 = accountService.create(dto2);

        // 批量删除
        boolean result = accountService.batchDelete(Arrays.asList(account1.getId(), account2.getId()));
        assertTrue(result);

        // 验证都被删除
        assertThrows(BusinessException.class, () -> accountService.getById(account1.getId()));
        assertThrows(BusinessException.class, () -> accountService.getById(account2.getId()));
    }

    @Test
    void testChinesePassword() {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setPlatformId(101L);
        dto.setAccountName("chinese@test.com");
        dto.setPassword("中文密码测试123");

        Account created = accountService.create(dto);
        String password = accountService.getPassword(created.getId());
        assertEquals("中文密码测试123", password);
    }

    @Test
    void testSpecialCharacterPassword() {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setPlatformId(101L);
        dto.setAccountName("special@test.com");
        dto.setPassword("!@#$%^&*()_+-=[]{}|;':\",./<>?");

        Account created = accountService.create(dto);
        String password = accountService.getPassword(created.getId());
        assertEquals("!@#$%^&*()_+-=[]{}|;':\",./<>?", password);
    }
}
