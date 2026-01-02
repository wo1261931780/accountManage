package wo1261931780.accountManage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import wo1261931780.accountManage.common.exception.BusinessException;
import wo1261931780.accountManage.common.result.PageResult;
import wo1261931780.accountManage.dto.request.PlatformCreateDTO;
import wo1261931780.accountManage.dto.request.PlatformQueryDTO;
import wo1261931780.accountManage.dto.request.PlatformUpdateDTO;
import wo1261931780.accountManage.dto.response.PlatformVO;
import wo1261931780.accountManage.entity.Platform;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 平台 Service 测试
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlatformServiceTest {

    @Autowired
    private PlatformService platformService;

    @Test
    void testPage() {
        PlatformQueryDTO query = new PlatformQueryDTO();
        query.setCurrent(1L);
        query.setSize(10L);

        PageResult<PlatformVO> result = platformService.page(query);
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testPageWithFilter() {
        PlatformQueryDTO query = new PlatformQueryDTO();
        query.setCurrent(1L);
        query.setSize(10L);
        query.setPlatformName("微信");

        PageResult<PlatformVO> result = platformService.page(query);
        assertNotNull(result);
        // 应该能找到微信
        assertTrue(result.getRecords().stream()
                .anyMatch(p -> p.getPlatformName().contains("微信")));
    }

    @Test
    void testListAll() {
        List<PlatformVO> list = platformService.listAll();
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void testGetById() {
        PlatformVO platform = platformService.getById(101L);
        assertNotNull(platform);
        assertEquals("WECHAT", platform.getPlatformCode());
        assertEquals("微信", platform.getPlatformName());
        assertNotNull(platform.getPlatformTypeName());
    }

    @Test
    void testGetByIdNotFound() {
        assertThrows(BusinessException.class, () -> {
            platformService.getById(99999L);
        });
    }

    @Test
    void testListByTypeId() {
        List<PlatformVO> list = platformService.listByTypeId(1L);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        // 所有返回的平台类型应该都是社交通讯
        for (PlatformVO platform : list) {
            assertEquals("社交通讯", platform.getPlatformTypeName());
        }
    }

    @Test
    void testCreate() {
        PlatformCreateDTO dto = new PlatformCreateDTO();
        dto.setPlatformCode("TEST_PLATFORM");
        dto.setPlatformName("测试平台");
        dto.setPlatformNameEn("Test Platform");
        dto.setPlatformTypeId(1L);
        dto.setPlatformUrl("https://test.com");
        dto.setCountry("CN");
        dto.setCountryName("中国");

        Platform created = platformService.create(dto);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("TEST_PLATFORM", created.getPlatformCode());
    }

    @Test
    void testCreateDuplicateCode() {
        PlatformCreateDTO dto = new PlatformCreateDTO();
        dto.setPlatformCode("WECHAT"); // 已存在
        dto.setPlatformName("重复平台");
        dto.setPlatformTypeId(1L);

        assertThrows(BusinessException.class, () -> {
            platformService.create(dto);
        });
    }

    @Test
    void testCreateInvalidType() {
        PlatformCreateDTO dto = new PlatformCreateDTO();
        dto.setPlatformCode("INVALID_TYPE_PLATFORM");
        dto.setPlatformName("无效类型平台");
        dto.setPlatformTypeId(99999L); // 不存在的类型

        assertThrows(BusinessException.class, () -> {
            platformService.create(dto);
        });
    }

    @Test
    void testUpdate() {
        PlatformUpdateDTO dto = new PlatformUpdateDTO();
        dto.setId(101L);
        dto.setPlatformName("微信（已更新）");

        boolean result = platformService.update(dto);
        assertTrue(result);

        PlatformVO updated = platformService.getById(101L);
        assertEquals("微信（已更新）", updated.getPlatformName());
    }

    @Test
    void testDelete() {
        // 先创建一个
        PlatformCreateDTO createDto = new PlatformCreateDTO();
        createDto.setPlatformCode("TO_DELETE_PLATFORM");
        createDto.setPlatformName("待删除平台");
        createDto.setPlatformTypeId(1L);
        Platform created = platformService.create(createDto);

        // 删除
        boolean result = platformService.delete(created.getId());
        assertTrue(result);

        // 再次获取应该抛出异常
        assertThrows(BusinessException.class, () -> {
            platformService.getById(created.getId());
        });
    }
}
