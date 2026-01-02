package wo1261931780.accountManage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.dto.request.PlatformTypeCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformTypeUpdateDTO;
import wo1261931780.accountManage.entity.PlatformType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 平台类型 Service 测试
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlatformTypeServiceTest {

    @Autowired
    private PlatformTypeService platformTypeService;

    @Test
    void testListAll() {
        List<PlatformType> list = platformTypeService.listAll();
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void testListEnabled() {
        List<PlatformType> list = platformTypeService.listEnabled();
        assertNotNull(list);
        // 所有返回的都应该是启用状态
        for (PlatformType type : list) {
            assertEquals(1, type.getStatus());
        }
    }

    @Test
    void testGetById() {
        PlatformType type = platformTypeService.getById(1L);
        assertNotNull(type);
        assertEquals("SOCIAL", type.getTypeCode());
        assertEquals("社交通讯", type.getTypeName());
    }

    @Test
    void testGetByIdNotFound() {
        assertThrows(BusinessException.class, () -> {
            platformTypeService.getById(99999L);
        });
    }

    @Test
    void testGetByCode() {
        PlatformType type = platformTypeService.getByCode("SOCIAL");
        assertNotNull(type);
        assertEquals(1L, type.getId());
    }

    @Test
    void testCreate() {
        PlatformTypeCreateDTO dto = new PlatformTypeCreateDTO();
        dto.setTypeCode("TEST_TYPE");
        dto.setTypeName("测试类型");
        dto.setTypeNameEn("Test Type");
        dto.setSortOrder(100);
        dto.setStatus(1);

        PlatformType created = platformTypeService.create(dto);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("TEST_TYPE", created.getTypeCode());
        assertEquals("测试类型", created.getTypeName());
    }

    @Test
    void testCreateDuplicateCode() {
        PlatformTypeCreateDTO dto = new PlatformTypeCreateDTO();
        dto.setTypeCode("SOCIAL"); // 已存在的编码
        dto.setTypeName("重复类型");

        assertThrows(BusinessException.class, () -> {
            platformTypeService.create(dto);
        });
    }

    @Test
    void testUpdate() {
        PlatformTypeUpdateDTO dto = new PlatformTypeUpdateDTO();
        dto.setId(1L);
        dto.setTypeName("社交通讯（已更新）");

        boolean result = platformTypeService.update(dto);
        assertTrue(result);

        PlatformType updated = platformTypeService.getById(1L);
        assertEquals("社交通讯（已更新）", updated.getTypeName());
    }

    @Test
    void testUpdateNotFound() {
        PlatformTypeUpdateDTO dto = new PlatformTypeUpdateDTO();
        dto.setId(99999L);
        dto.setTypeName("不存在");

        assertThrows(BusinessException.class, () -> {
            platformTypeService.update(dto);
        });
    }

    @Test
    void testDelete() {
        // 先创建一个
        PlatformTypeCreateDTO createDto = new PlatformTypeCreateDTO();
        createDto.setTypeCode("TO_DELETE");
        createDto.setTypeName("待删除");
        PlatformType created = platformTypeService.create(createDto);

        // 删除
        boolean result = platformTypeService.delete(created.getId());
        assertTrue(result);

        // 再次获取应该抛出异常
        assertThrows(BusinessException.class, () -> {
            platformTypeService.getById(created.getId());
        });
    }
}
