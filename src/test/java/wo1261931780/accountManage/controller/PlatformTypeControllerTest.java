package wo1261931780.accountManage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.dto.request.PlatformTypeCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformTypeUpdateDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 平台类型 Controller 测试
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PlatformTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testList() throws Exception {
        mockMvc.perform(get("/api/v1/platform-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testListEnabled() throws Exception {
        mockMvc.perform(get("/api/v1/platform-types/enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetById() throws Exception {
        mockMvc.perform(get("/api/v1/platform-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.typeCode").value("SOCIAL"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/platform-types/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1002));
    }

    @Test
    void testCreate() throws Exception {
        PlatformTypeCreateDTO dto = new PlatformTypeCreateDTO();
        dto.setTypeCode("API_TEST");
        dto.setTypeName("API测试类型");
        dto.setTypeNameEn("API Test Type");

        mockMvc.perform(post("/api/v1/platform-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.typeCode").value("API_TEST"));
    }

    @Test
    void testCreateValidationError() throws Exception {
        PlatformTypeCreateDTO dto = new PlatformTypeCreateDTO();
        // 缺少必填字段

        mockMvc.perform(post("/api/v1/platform-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        PlatformTypeUpdateDTO dto = new PlatformTypeUpdateDTO();
        dto.setId(1L);
        dto.setTypeName("社交通讯（API更新）");

        mockMvc.perform(put("/api/v1/platform-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testDelete() throws Exception {
        // 先创建一个
        PlatformTypeCreateDTO createDto = new PlatformTypeCreateDTO();
        createDto.setTypeCode("TO_DELETE_API");
        createDto.setTypeName("待删除API");

        String createResult = mockMvc.perform(post("/api/v1/platform-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andReturn().getResponse().getContentAsString();

        // 提取 ID
        Long id = objectMapper.readTree(createResult).path("data").path("id").asLong();

        // 删除
        mockMvc.perform(delete("/api/v1/platform-types/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
}
