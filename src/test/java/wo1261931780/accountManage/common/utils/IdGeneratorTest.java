package wo1261931780.accountManage.common.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ID 生成器测试
 *
 * @author wo1261931780
 * @since 2024-12-30
 */
@SpringBootTest
@ActiveProfiles("test")
class IdGeneratorTest {

    @Autowired
    private IdGenerator idGenerator;

    @Test
    void testNextId() {
        Long id = idGenerator.nextId();
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void testNextIdStr() {
        String idStr = idGenerator.nextIdStr();
        assertNotNull(idStr);
        assertFalse(idStr.isEmpty());

        // 应该是数字字符串
        assertDoesNotThrow(() -> Long.parseLong(idStr));
    }

    @Test
    void testIdUniqueness() {
        Set<Long> ids = new HashSet<>();
        int count = 10000;

        for (int i = 0; i < count; i++) {
            ids.add(idGenerator.nextId());
        }

        // 所有 ID 应该都是唯一的
        assertEquals(count, ids.size());
    }

    @Test
    void testIdIncreasing() {
        Long id1 = idGenerator.nextId();
        Long id2 = idGenerator.nextId();
        Long id3 = idGenerator.nextId();

        // ID 应该是递增的
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }
}
