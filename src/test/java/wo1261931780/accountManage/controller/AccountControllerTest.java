package wo1261931780.accountManage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.dto.request.AccountCreateDTO;
import wo1261931780.accountManage.dto.request.AccountUpdateDTO;
import wo1261931780.accountManage.entity.Account;
import wo1261931780.accountManage.service.AccountService;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 账号 Controller 测试
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    private Long testAccountId;

    @BeforeEach
    void setUp() {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setPlatformId(101L);
        dto.setAccountName("controller_test@example.com");
        dto.setAccountAlias("Controller测试账号");
        dto.setPassword("ControllerTest123!");
        Account created = accountService.create(dto);
        testAccountId = created.getId();
    }

    @Test
    void testPage() throws Exception {
        mockMvc.perform(get("/api/v1/accounts")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void testPageWithFilter() throws Exception {
        mockMvc.perform(get("/api/v1/accounts")
                        .param("current", "1")
                        .param("size", "10")
                        .param("accountName", "controller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetById() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/" + testAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accountName").value("controller_test@example.com"))
                .andExpect(jsonPath("$.data.password").value("ControllerTest123!"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1002));
    }

    @Test
    void testListByPlatformId() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/platform/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetPassword() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/" + testAccountId + "/password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("ControllerTest123!"));
    }

    @Test
    void testCreate() throws Exception {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setPlatformId(102L);
        dto.setAccountName("api_create@example.com");
        dto.setAccountAlias("API创建账号");
        dto.setPassword("ApiCreate123!");

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accountName").value("api_create@example.com"));
    }

    @Test
    void testCreateValidationError() throws Exception {
        AccountCreateDTO dto = new AccountCreateDTO();
        // 缺少必填字段

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        AccountUpdateDTO dto = new AccountUpdateDTO();
        dto.setId(testAccountId);
        dto.setAccountAlias("API更新后的别名");

        mockMvc.perform(put("/api/v1/accounts/" + testAccountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/accounts/" + testAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testBatchDelete() throws Exception {
        // 创建额外账号
        AccountCreateDTO dto1 = new AccountCreateDTO();
        dto1.setPlatformId(101L);
        dto1.setAccountName("batch_delete1@test.com");
        dto1.setPassword("Batch1!");
        Account account1 = accountService.create(dto1);

        AccountCreateDTO dto2 = new AccountCreateDTO();
        dto2.setPlatformId(101L);
        dto2.setAccountName("batch_delete2@test.com");
        dto2.setPassword("Batch2!");
        Account account2 = accountService.create(dto2);

        mockMvc.perform(post("/api/v1/accounts/batch-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(account1.getId(), account2.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
