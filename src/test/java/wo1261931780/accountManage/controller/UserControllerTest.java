package wo1261931780.accountManage.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.dto.auth.LoginDTO;
import wo1261931780.accountManage.dto.user.UserCreateDTO;
import wo1261931780.accountManage.dto.user.UserUpdateDTO;
import wo1261931780.accountManage.dto.user.ResetPasswordDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户管理 Controller 测试
 *
 * @author wo1261931780
 * @since 2026-01-02
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 获取管理员Token
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int httpStatus = result.getResponse().getStatus();

        // 确保登录成功
        assertEquals(200, httpStatus, "登录请求失败，HTTP状态码: " + httpStatus + ", 响应: " + response);

        JsonNode jsonNode = objectMapper.readTree(response);
        assertEquals(200, jsonNode.path("code").asInt(), "登录业务失败: " + response);

        accessToken = jsonNode.path("data").path("accessToken").asText();
        assertNotNull(accessToken, "登录获取token失败，响应: " + response);
        assertFalse(accessToken.isEmpty(), "Token不能为空，响应: " + response);
    }

    @Test
    void testPageUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void testPageUsersWithKeyword() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("current", "1")
                        .param("size", "10")
                        .param("keyword", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCreateUser() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("testuser");
        createDTO.setPassword("TestUser123!");
        createDTO.setNickname("测试用户");
        createDTO.setEmail("testuser@example.com");

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void testCreateUserDuplicateUsername() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("admin"); // 已存在
        createDTO.setPassword("TestUser123!");
        createDTO.setNickname("测试用户");

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateUserValidation() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        // 缺少必填字段

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById() throws Exception {
        // 先创建用户
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("getusertest");
        createDTO.setPassword("GetUser123!");
        createDTO.setNickname("获取测试用户");

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        int start = createResponse.indexOf("\"data\":") + 7;
        int end = createResponse.indexOf(",", start);
        if (end == -1) end = createResponse.indexOf("}", start);
        String userId = createResponse.substring(start, end).trim();

        // 获取用户详情
        mockMvc.perform(get("/api/v1/users/" + userId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("getusertest"));
    }

    @Test
    void testUpdateUser() throws Exception {
        // 先创建用户
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("updateusertest");
        createDTO.setPassword("UpdateUser123!");
        createDTO.setNickname("更新测试用户");

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        int start = createResponse.indexOf("\"data\":") + 7;
        int end = createResponse.indexOf(",", start);
        if (end == -1) end = createResponse.indexOf("}", start);
        String userId = createResponse.substring(start, end).trim();

        // 更新用户
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setNickname("更新后的昵称");
        updateDTO.setEmail("updated@example.com");

        mockMvc.perform(put("/api/v1/users/" + userId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateUserStatus() throws Exception {
        // 先创建用户
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("statususertest");
        createDTO.setPassword("StatusUser123!");
        createDTO.setNickname("状态测试用户");

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        int start = createResponse.indexOf("\"data\":") + 7;
        int end = createResponse.indexOf(",", start);
        if (end == -1) end = createResponse.indexOf("}", start);
        String userId = createResponse.substring(start, end).trim();

        // 禁用用户
        mockMvc.perform(put("/api/v1/users/" + userId + "/status")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 启用用户
        mockMvc.perform(put("/api/v1/users/" + userId + "/status")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testResetPassword() throws Exception {
        // 先创建用户
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("resetpwdtest");
        createDTO.setPassword("ResetPwd123!");
        createDTO.setNickname("重置密码测试用户");

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        int start = createResponse.indexOf("\"data\":") + 7;
        int end = createResponse.indexOf(",", start);
        if (end == -1) end = createResponse.indexOf("}", start);
        String userId = createResponse.substring(start, end).trim();

        // 重置密码
        ResetPasswordDTO resetDTO = new ResetPasswordDTO();
        resetDTO.setNewPassword("NewPassword123!");

        mockMvc.perform(post("/api/v1/users/" + userId + "/reset-password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteUser() throws Exception {
        // 先创建用户
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("deleteusertest");
        createDTO.setPassword("DeleteUser123!");
        createDTO.setNickname("删除测试用户");

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        int start = createResponse.indexOf("\"data\":") + 7;
        int end = createResponse.indexOf(",", start);
        if (end == -1) end = createResponse.indexOf("}", start);
        String userId = createResponse.substring(start, end).trim();

        // 删除用户
        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUnlockUser() throws Exception {
        // 先创建用户
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setUsername("unlockusertest");
        createDTO.setPassword("UnlockUser123!");
        createDTO.setNickname("解锁测试用户");

        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        int start = createResponse.indexOf("\"data\":") + 7;
        int end = createResponse.indexOf(",", start);
        if (end == -1) end = createResponse.indexOf("}", start);
        String userId = createResponse.substring(start, end).trim();

        // 解锁用户
        mockMvc.perform(post("/api/v1/users/" + userId + "/unlock")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCheckUsername() throws Exception {
        mockMvc.perform(get("/api/v1/users/check-username")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true)); // 已存在

        mockMvc.perform(get("/api/v1/users/check-username")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("username", "nonexistent_user_12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false)); // 不存在
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // 没有 Token 访问受保护资源，应该返回 HTTP 401
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }
}
